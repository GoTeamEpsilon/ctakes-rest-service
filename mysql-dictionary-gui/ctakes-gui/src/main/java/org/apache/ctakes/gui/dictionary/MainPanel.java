package org.apache.ctakes.gui.dictionary;

import org.apache.ctakes.gui.component.DisablerPane;
import org.apache.ctakes.gui.component.FileChooserPanel;
import org.apache.ctakes.gui.component.LoggerPanel;
import org.apache.ctakes.gui.component.PositionedSplitPane;
import org.apache.ctakes.gui.dictionary.umls.MrconsoIndex;
import org.apache.ctakes.gui.dictionary.umls.SourceTableModel;
import org.apache.ctakes.gui.dictionary.umls.Tui;
import org.apache.ctakes.gui.dictionary.umls.TuiTableModel;
import org.apache.ctakes.gui.dictionary.util.FileUtil;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 12/10/2015
 */
final class MainPanel extends JPanel {

   static private final Logger LOGGER = Logger.getLogger( "MainPanel" );

   private String _umlsDirPath = System.getProperty( "user.dir" );
   private String _ctakesPath = System.getProperty( "user.dir" );
   private final TuiTableModel _tuiModel = new TuiTableModel();
   private final SourceTableModel _sourceModel = new SourceTableModel();

   MainPanel() {
      super( new BorderLayout() );
      final JComponent sourceDirPanel = new JPanel( new GridLayout( 2, 1 ) );
      sourceDirPanel.add( new FileChooserPanel( "cTAKES Installation:", _ctakesPath, true, new CtakesDirListener() ) );
      sourceDirPanel.add( new FileChooserPanel( "UMLS Installation:", _umlsDirPath, true, new UmlsDirListener() ) );
      add( sourceDirPanel, BorderLayout.NORTH );

      add( createCenterPanel( _sourceModel, _tuiModel ), BorderLayout.CENTER );
   }

   private JComponent createCenterPanel( final TableModel sourceModel, final TableModel tuiModel ) {
      final JSplitPane centerSplit = new PositionedSplitPane();
      centerSplit.setLeftComponent( createSourceTable( sourceModel ) );
      centerSplit.setRightComponent( createTuiTable( tuiModel ) );
      centerSplit.setDividerLocation( 0.5d );

      final JPanel umlsPanel = new JPanel( new BorderLayout() );
      umlsPanel.add( centerSplit, BorderLayout.CENTER );
      umlsPanel.add( createGoPanel(), BorderLayout.SOUTH );

      final JSplitPane logSplit = new PositionedSplitPane( JSplitPane.VERTICAL_SPLIT );
      logSplit.setTopComponent( umlsPanel );
      logSplit.setBottomComponent( LoggerPanel.createLoggerPanel() );
      logSplit.setDividerLocation( 0.6d );

      return logSplit;
   }

   static private JComponent createTuiTable( final TableModel tuiModel ) {
      final JTable tuiTable = new JTable( tuiModel );
      tuiTable.setCellSelectionEnabled( false );
      tuiTable.setShowVerticalLines( false );
      tuiTable.setAutoCreateRowSorter( true );
      tuiTable.setAutoResizeMode( JTable.AUTO_RESIZE_LAST_COLUMN );
      tuiTable.getColumnModel().getColumn( 0 ).setMaxWidth( 50 );
      tuiTable.getColumnModel().getColumn( 1 ).setMaxWidth( 50 );
      return new JScrollPane( tuiTable );
   }

   static private JComponent createSourceTable( final TableModel sourceModel ) {
      final JTable tuiTable = new JTable( sourceModel );
      tuiTable.setCellSelectionEnabled( false );
      tuiTable.setShowVerticalLines( false );
      tuiTable.setAutoCreateRowSorter( true );
      tuiTable.setAutoResizeMode( JTable.AUTO_RESIZE_LAST_COLUMN );
      tuiTable.getColumnModel().getColumn( 0 ).setMaxWidth( 50 );
      tuiTable.getColumnModel().getColumn( 1 ).setMaxWidth( 50 );
      return new JScrollPane( tuiTable );
   }

   private JComponent createGoPanel() {
      final JPanel panel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 5 ) );
      panel.setBorder( new EmptyBorder( 2, 10, 2, 10 ) );
      final JLabel nameLabel = new JLabel( "Dictionary Name:" );
      final JTextField nameTextField = new JTextField( "custom" );
      nameTextField.setColumns( 15 );
      final JLabel destinationLabel = new JLabel( "Dictionary Destination:" );
      JComboBox<String> destinationCombo = new JComboBox<String>();
      destinationCombo.addItem( DatabaseSource.HSQL.toString() );
      destinationCombo.addItem( DatabaseSource.MYSQL.toString() );
      final JButton buildButton = new JButton( new BuildDictionaryAction( nameTextField, destinationCombo ) );
      panel.add( nameLabel );
      panel.add( nameTextField );
      panel.add( destinationLabel );
      panel.add( destinationCombo );
      panel.add( buildButton );
      return panel;
   }


   private String setUmlsDirPath( final String umlsDirPath ) {
      File mrConso = new File( umlsDirPath, "MRCONSO.RRF" );
      if ( mrConso.isFile() ) {
         _umlsDirPath = mrConso.getParentFile().getParent();
      } else {
         final String plusMetaPath = new File( umlsDirPath, "META" ).getPath();
         mrConso = new File( plusMetaPath, "MRCONSO.RRF" );
         if ( mrConso.isFile() ) {
            _umlsDirPath = umlsDirPath;
         } else {
            error( "Invalid UMLS Installation", umlsDirPath + " is not a valid path to a UMLS installation" );
         }
      }
      return _umlsDirPath;
   }

   private void loadSources() {
      final ExecutorService executor = Executors.newSingleThreadExecutor();
      executor.execute( new SourceLoadRunner( _umlsDirPath ) );
   }

   private class SourceLoadRunner implements Runnable {
      private final String __umlsDirPath;

      private SourceLoadRunner( final String umlsDirPath ) {
         __umlsDirPath = umlsDirPath;
      }

      @Override
      public void run() {
         final JFrame frame = (JFrame)SwingUtilities.getRoot( MainPanel.this );
         frame.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
         DisablerPane.getInstance().setVisible( true );
         final File mrConso = new File( __umlsDirPath + "/META", "MRCONSO.RRF" );
         final String mrConsoPath = mrConso.getPath();
         LOGGER.info( "Parsing vocabulary types from " + mrConsoPath );
         final Collection<String> sources = new HashSet<>();
         try ( final BufferedReader reader = FileUtil.createReader( mrConsoPath ) ) {
            int lineCount = 0;
            java.util.List<String> tokens = FileUtil.readBsvTokens( reader, mrConsoPath );
            while ( tokens != null ) {
               lineCount++;
               if ( tokens.size() > MrconsoIndex.SOURCE._index ) {
                  sources.add( tokens.get( MrconsoIndex.SOURCE._index ) );
               }
               if ( lineCount % 100000 == 0 ) {
                  LOGGER.info( "File Line " + lineCount + "\t Vocabularies " + sources.size() );
               }
               tokens = FileUtil.readBsvTokens( reader, mrConsoPath );
            }
            LOGGER.info( "Parsed " + sources.size() + " vocabulary types" );
            _sourceModel.setSources( sources );
         } catch ( IOException ioE ) {
            error( "Vocabulary Parse Error", ioE.getMessage() );
         }
         DisablerPane.getInstance().setVisible( false );
         frame.setCursor( Cursor.getDefaultCursor() );
      }
   }

   private void buildDictionary( final String dictionaryName, final String dictionaryDestination ) {
      final ExecutorService executor = Executors.newSingleThreadExecutor();
      executor.execute( new DictionaryBuildRunner( _umlsDirPath, _ctakesPath, dictionaryName,
                        dictionaryDestination, _sourceModel
            .getWantedSources(),
            _sourceModel.getWantedTargets(), _tuiModel.getWantedTuis() ) );
   }

   private void error( final String title, final String message ) {
      LOGGER.error( message );
      JOptionPane.showMessageDialog( MainPanel.this, message, title, JOptionPane.ERROR_MESSAGE );
   }


   private class DictionaryBuildRunner implements Runnable {
      private final String __umlsDirPath;
      private final String __ctakesDirPath;
      private final String __dictionaryName;
      private final String __dictionaryDestination;
      private final Collection<String> __wantedSources;
      private final Collection<String> __wantedTargets;
      private final Collection<Tui> __wantedTuis;

      private DictionaryBuildRunner( final String umlsDirPath, final String ctakesDirPath, final String dictionaryName,
                                     final String dictionaryDestination,
                                     final Collection<String> wantedSources,
                                     final Collection<String> wantedTargets,
                                     final Collection<Tui> wantedTuis ) {
         __umlsDirPath = umlsDirPath;
         __ctakesDirPath = ctakesDirPath;
         __dictionaryName = dictionaryName;
         __dictionaryDestination = dictionaryDestination;
         __wantedSources = wantedSources;
         __wantedTargets = new ArrayList<>( wantedTargets );
         __wantedTuis = new ArrayList<>( wantedTuis );
      }

      @Override
      public void run() {
         SwingUtilities.getRoot( MainPanel.this ).setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
         DisablerPane.getInstance().setVisible( true );
         if ( DictionaryBuilder.buildDictionary( __umlsDirPath, __ctakesDirPath, __dictionaryName,
               __dictionaryDestination, Collections.singletonList( "ENG" ),
               __wantedSources, __wantedTargets, __wantedTuis ) ) {
            final String message = "Dictionary " + __dictionaryName + " successfully built in " + __ctakesDirPath;
            LOGGER.info( message );
            JOptionPane
                  .showMessageDialog( MainPanel.this, message, "Dictionary Built", JOptionPane.INFORMATION_MESSAGE );
         } else {
            error( "Build Failure", "Dictionary " + __dictionaryName + " could not be built in " + __ctakesDirPath );
         }
         DisablerPane.getInstance().setVisible( false );
         SwingUtilities.getRoot( MainPanel.this ).setCursor( Cursor.getDefaultCursor() );
      }
   }


   private class UmlsDirListener implements ActionListener {
      @Override
      public void actionPerformed( final ActionEvent event ) {
         final String oldPath = _umlsDirPath;
         final String newPath = setUmlsDirPath( event.getActionCommand() );
         if ( !oldPath.equals( newPath ) ) {
            loadSources();
         }
      }
   }


   private class CtakesDirListener implements ActionListener {
      @Override
      public void actionPerformed( final ActionEvent event ) {
         _ctakesPath = event.getActionCommand();
      }
   }


   /**
    * Builds the dictionary
    */
   private class BuildDictionaryAction extends AbstractAction {
      private final JTextComponent __textComponent;
      private final JComboBox<String> __comboComponent;

      private BuildDictionaryAction( final JTextComponent textComponent,
                                     final JComboBox<String> comboComponent ) {
         super( "Build Dictionary" );
         __textComponent = textComponent;
         __comboComponent = comboComponent;
      }

      @Override
      public void actionPerformed( final ActionEvent event ) {
         if ( _sourceModel.getRowCount() == 0 ) {
            error( "UMLS not yet loaded", "Please specify a UMLS installation." );
            return;
         }
         final String dictionaryName = __textComponent.getText();
         final String dictionaryDestination = String.valueOf( __comboComponent.getSelectedItem() );
         if ( dictionaryName != null && !dictionaryName.isEmpty() ) {
            buildDictionary( dictionaryName.toLowerCase(), dictionaryDestination );
         } else {
            error( "Invalid Dictionary Name", "Please Specify a Dictionary Name" );
         }
      }
   }


}