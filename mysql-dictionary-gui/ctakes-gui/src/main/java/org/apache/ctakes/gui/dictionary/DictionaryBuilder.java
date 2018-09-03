package org.apache.ctakes.gui.dictionary;


import org.apache.ctakes.core.util.collection.CollectionMap;
import org.apache.ctakes.core.util.collection.HashSetMap;
import org.apache.ctakes.gui.dictionary.umls.*;
import org.apache.ctakes.gui.dictionary.util.HsqlUtil;
import org.apache.ctakes.gui.dictionary.util.MysqlUtil;
import org.apache.ctakes.gui.dictionary.util.JdbcUtil;
import org.apache.ctakes.gui.dictionary.util.RareWordDbWriter;
import org.apache.log4j.Logger;
import org.apache.ctakes.gui.dictionary.DatabaseSource;

import java.io.File;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 12/11/2015
 */
final class DictionaryBuilder {

   static private final Logger LOGGER = Logger.getLogger( "DictionaryBuilder" );

   static private final String DEFAULT_DATA_DIR = "org/apache/ctakes/gui/dictionary/data/tiny";
   static private final String CTAKES_APP_DB_PATH = "resources/org/apache/ctakes/dictionary/lookup/fast";
   static private final String CTAKES_RES_MODULE = "ctakes-dictionary-lookup-fast-res";
   static private final String CTAKES_RES_DB_PATH = CTAKES_RES_MODULE + "/src/main/" + CTAKES_APP_DB_PATH;
   static private final int MIN_CHAR_LENGTH = 2;
   static private final int MAX_CHAR_LENGTH = 48;
   static private final int MAX_WORD_COUNT = 12;
   static private final int MAX_SYM_COUNT = 7;
   static private final int WSD_DIVISOR = 2;
   static private final int ANAT_MULTIPLIER = 2;


   private DictionaryBuilder() {
   }


   static boolean buildDictionary( final String umlsDirPath,
                                   final String ctakesDirPath,
                                   final String dictionaryName,
                                   final String dictionaryDestination,
                                   final Collection<String> wantedLanguages,
                                   final Collection<String> wantedSources,
                                   final Collection<String> wantedTargets,
                                   final Collection<Tui> wantedTuis ) {
      // Set up the term utility
      final UmlsTermUtil umlsTermUtil = new UmlsTermUtil( DEFAULT_DATA_DIR );
      final Map<Long, Concept> conceptMap
            = parseAll( umlsTermUtil, umlsDirPath, wantedLanguages, wantedSources, wantedTargets, wantedTuis );
      return writeDatabase( ctakesDirPath, dictionaryName, dictionaryDestination, conceptMap );
   }


   static private Map<Long, Concept> parseAll( final UmlsTermUtil umlsTermUtil,
                                               final String umlsDirPath,
                                               final Collection<String> wantedLanguages,
                                               final Collection<String> wantedSources,
                                               final Collection<String> wantedTargets,
                                               final Collection<Tui> wantedTuis ) {
      LOGGER.info( "Parsing Concepts" );
      // Create a map of Cuis to empty Concepts for all wanted Tuis and source vocabularies
      final Map<Long, Concept> conceptMap
            = ConceptMapFactory.createInitialConceptMap( umlsDirPath, wantedSources, wantedTuis );
      // Fill in information for all valid concepts
      MrconsoParser.parseAllConcepts( umlsDirPath, conceptMap, wantedSources, wantedTargets, umlsTermUtil,
            wantedLanguages, true, MIN_CHAR_LENGTH, MAX_CHAR_LENGTH, MAX_WORD_COUNT, MAX_SYM_COUNT );
      removeWsdRarities( conceptMap, wantedTuis, WSD_DIVISOR, ANAT_MULTIPLIER );
//      removeUnwantedDrugs( conceptMap, wantedTuis );
      // Cull non-ANAT texts by ANAT texts as determined by ANAT tuis
//      removeAnatTexts( conceptMap, wantedTuis );
      LOGGER.info( "Done Parsing Concepts" );
      return conceptMap;
   }

   static private void removeWsdRarities( final Map<Long, Concept> conceptMap, final Collection<Tui> wantedTuis,
                                          final int wsdDivisor, final int anatMultiplier ) {
      LOGGER.info( "Performing Poor man's WSD ..." );
      final Collection<Tui> wantedAnatTuis = new ArrayList<>( wantedTuis );
      wantedAnatTuis.retainAll( Arrays.asList( TuiTableModel.CTAKES_ANAT ) );
      final CollectionMap<String, Concept, Set<Concept>> synonymCodeMap = new HashSetMap<>( 500000 );
      for ( Concept concept : conceptMap.values() ) {
         concept.cullExtensions();
         concept.getTexts().forEach( t -> synonymCodeMap.placeValue( t, concept ) );
      }
      // Poor man's WSD
      for ( Map.Entry<String, Set<Concept>> synonymConcepts : synonymCodeMap ) {
         final Collection<Concept> concepts = synonymConcepts.getValue();
         if ( concepts.size() == 1 ) {
            continue;
         }
         final String synonym = synonymConcepts.getKey();
         int maxCount = 0;
         for ( Concept concept : concepts ) {
            int count = concept.getCount( synonym );
            if ( wantedAnatTuis.containsAll( concept.getTuis() ) ) {
               count *= anatMultiplier;
            }
            maxCount = Math.max( maxCount, count );
         }
         if ( maxCount <= 1 ) {
            continue;
         }
         final int threshold = (int)Math.floor( (double)maxCount / (double)wsdDivisor );
         for ( Concept concept : concepts ) {
            int count = concept.getCount( synonym );
            if ( wantedAnatTuis.containsAll( concept.getTuis() ) ) {
               count *= anatMultiplier;
            }
            if ( count <= threshold ) {
               concept.removeText( synonym );
            }
         }
      }
      final Collection<Long> empties = new ArrayList<>();
      int textCount = 0;
      for ( Map.Entry<Long, Concept> entry : conceptMap.entrySet() ) {
         final Concept concept = entry.getValue();
         if ( concept.isEmpty() ) {
            empties.add( entry.getKey() );
         } else {
            textCount += concept.getSynonymCount();
         }
      }
      conceptMap.keySet().removeAll( empties );
      LOGGER.info( "Concepts: " + conceptMap.size() + "  Texts: " + textCount );
   }

   static private void removeAnatTexts( final Map<Long, Concept> conceptMap,
                                        final Collection<Tui> wantedTuis ) {
      LOGGER.info( "Removing Non-Anatomy synonyms that are also Anatomy synonyms ..." );
      final Collection<Tui> wantedAnatTuis = new ArrayList<>( wantedTuis );
      wantedAnatTuis.retainAll( Arrays.asList( TuiTableModel.CTAKES_ANAT ) );
      final Collection<String> anatTexts = conceptMap.values().stream()
            .filter( c -> wantedAnatTuis.containsAll( c.getTuis() ) )
            .map( Concept::getTexts )
            .flatMap( Collection::stream )
            .collect( Collectors.toSet() );
      final Collection<Tui> nonAnatTuis = new ArrayList<>( wantedTuis );
      nonAnatTuis.removeAll( Arrays.asList( TuiTableModel.CTAKES_ANAT ) );
      final Collection<Long> empties = new ArrayList<>();
      int textCount = 0;
      for ( Map.Entry<Long, Concept> entry : conceptMap.entrySet() ) {
         final Concept concept = entry.getValue();
         if ( nonAnatTuis.containsAll( concept.getTuis() ) ) {
            concept.removeTexts( anatTexts );
         }
         if ( concept.isEmpty() ) {
            empties.add( entry.getKey() );
         } else {
            textCount += concept.getSynonymCount();
         }
      }
      conceptMap.keySet().removeAll( empties );
      LOGGER.info( "Concepts: " + conceptMap.size() + "  Texts: " + textCount );
   }


   // TODO too much tui confusion in non-rxnorm drugs
   static private void removeUnwantedDrugs( final Map<Long, Concept> conceptMap, Collection<Tui> wantedTuis ) {
      LOGGER.info( "Removing Drug Concepts not in rxnorm ..." );
      // remove concepts that have only drug tuis but are not in rxnorm
      final Collection<Tui> drugTuis = new ArrayList<>( wantedTuis );
      drugTuis.retainAll( Arrays.asList( TuiTableModel.CTAKES_DRUG ) );
      // remove concepts that are in rxnorm but have non-drug tuis
      final Collection<Tui> nonDrugTuis = new ArrayList<>( wantedTuis );
      nonDrugTuis.removeAll( Arrays.asList( TuiTableModel.CTAKES_DRUG ) );
      // if concept has drug tuis but is not in rxnorm || concept is in rxnorm but does not have drug tuis
      final Collection<Long> empties = new ArrayList<>();
      int textCount = 0;
      for ( Map.Entry<Long, Concept> entry : conceptMap.entrySet() ) {
         final Concept concept = entry.getValue();
         LOGGER.info( concept.getPreferredText() );
         if ( drugTuis.containsAll( concept.getTuis() ) && concept.getVocabularies().contains( "RXNORM" ) ) {
            LOGGER.info( "drug" );
            textCount += concept.getSynonymCount();
            continue;
         }
         if ( nonDrugTuis.containsAll( concept.getTuis() ) && !concept.getVocabularies().contains( "RXNORM" ) ) {
            LOGGER.info( "not drug" );
            textCount += concept.getSynonymCount();
            continue;
         }
         LOGGER.info(
               "bad " + drugTuis.containsAll( concept.getTuis() ) + " " + nonDrugTuis.containsAll( concept.getTuis() ) +
               " " + concept.getVocabularies().contains( "RXNORM" ) );
         empties.add( entry.getKey() );
      }
      conceptMap.keySet().removeAll( empties );
      LOGGER.info( "Concepts: " + conceptMap.size() + "  Texts: " + textCount );
   }


   static private boolean writeDatabase( final String ctakesDirPath,
                                         final String dictionaryName,
                                         final String dictionaryDestination,
                                         final Map<Long, Concept> conceptMap ) {

      Connection connection = null;

      LOGGER.info( "Selected dictionary destination " + dictionaryDestination );

      if ( dictionaryDestination.equals( DatabaseSource.MYSQL.toString() ) ) {
         // TODO: Configuration-driven variables
         connection = JdbcUtil.createDatabaseConnection( MysqlUtil.URL_PREFIX,
                                                         MysqlUtil.JBDC_STRING,
                                                         "root",
                                                         "root" );
          
         LOGGER.info( "Creating MySQL connection" );
         if ( !MysqlUtil.createDatabase( connection ) ) {
            return false;
         }
      } else if ( dictionaryDestination.equals( DatabaseSource.HSQL.toString() ) ) {
         final File ctakesRoot = new File( ctakesDirPath );
         String databaseDirPath = ctakesDirPath + "/" + CTAKES_APP_DB_PATH;
         if ( Arrays.asList( ctakesRoot.list() ).contains( CTAKES_RES_MODULE ) ) {
            databaseDirPath = ctakesDirPath + "/" + CTAKES_RES_DB_PATH;
         }
         final String url = HsqlUtil.URL_PREFIX + databaseDirPath.replace( '\\', '/' ) + "/" + dictionaryName + "/" +
                            dictionaryName;
         connection = JdbcUtil.createDatabaseConnection( url, HsqlUtil.JBDC_DRIVER, "SA", "" );

         LOGGER.info( "Creating HSQLDB connection" );
         if ( !HsqlUtil.createDatabase( connection ) ) {
            return false;
         }
         if ( !DictionaryXmlWriter.writeXmlFile( databaseDirPath, dictionaryName ) ) {
            return false;
         }
      }

      LOGGER.info( "writing concepts..." );
      return RareWordDbWriter.writeConcepts( connection, conceptMap );
   }
}