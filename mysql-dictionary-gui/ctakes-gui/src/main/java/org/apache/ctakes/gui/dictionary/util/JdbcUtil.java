package org.apache.ctakes.gui.dictionary.util;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Author: SPF
 * Affiliation: CHIP-NLP
 * Date: 1/21/14
 */
final public class JdbcUtil {

   static private final Logger LOGGER = Logger.getLogger( "JdbcUtil" );

   private JdbcUtil() {
   }

   static public void registerDriver( final String jbdcDriver ) {
      try {
         Driver driver = (Driver)Class.forName( jbdcDriver ).newInstance();
         DriverManager.registerDriver( driver );
      } catch ( Exception e ) {
         // TODO At least four different exceptions are thrown here, and should be caught and handled individually
         LOGGER.error( "Could not register Driver " + jbdcDriver );
         LOGGER.error( e.getMessage() );
         System.exit( 1 );
      }
   }

   static public Connection createDatabaseConnection( final String url, final String jbdcDriver, final String user, final String pass ) {
      registerDriver(jbdcDriver);
      LOGGER.info( "Connecting to " + url + " as " + user );
      Connection connection = null;
      try {
	    connection = DriverManager.getConnection(url, user, pass);
      } catch ( SQLException sqlE ) {
         // thrown by Connection.prepareStatement(..) and getTotalRowCount(..)
         LOGGER.error( "Could not establish connection to " + url + " as " + user );
         LOGGER.error( sqlE.getMessage() );
         System.exit( 1 );
      }
      return connection;
   }

   //   static public String createRowInsertSql( final String tableName, final int valueCount ) {
   static public String createRowInsertSql( final String tableName, final Enum... fields ) {
      final String[] fieldNames = new String[ fields.length ];
      int i = 0;
      for ( Enum field : fields ) {
         fieldNames[ i ] = field.name();
         i++;
      }
      return createRowInsertSql( tableName.toLowerCase(), fieldNames );
   }

   static public String createCodeInsertSql( final String vocabulary ) {
      return createRowInsertSql( vocabulary.toLowerCase().replace( '.', '_' ).replace( '-', '_' ), "CUI", vocabulary );
   }

   static public String createRowInsertSql( final String tableName, final String... fieldNames ) {
      final StringBuilder sb = new StringBuilder( "insert into" );
      sb.append( " " ).append( tableName );
      sb.append( " (" );
      for ( String fieldName : fieldNames ) {
         sb.append( fieldName ).append( ',' );
      }
      // remove last comma
      sb.setLength( sb.length() - 1 );
      sb.append( ") " );
      sb.append( " values (" );
      for ( int i = 0; i < fieldNames.length - 1; i++ ) {
         sb.append( "?," );
      }
      sb.append( "?)" );
      return sb.toString();
   }

}