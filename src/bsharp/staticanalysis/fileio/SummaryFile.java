package bsharp.staticanalysis.fileio;

import java.util.Vector;

import bsharp.fileio.EasyFile;

/**
 * @author brandon_sharp@tvworks.com
 *
 */
public class SummaryFile extends EasyFile {
   
   private static final String SEPARATOR = " ";
   
   private Vector summaryNames = new Vector();
   private Vector summaryValues = new Vector();
   
   /**
    * Create new summary file io object.
    * @param filename
    */
   public SummaryFile( final String filename ) {
      super(filename);
   }
   
   public void setContents( final Vector summaryNames, final Vector summaryValues ) {
      this.summaryNames = summaryNames;
      this.summaryValues = summaryValues;
   }
   
   public Vector getSummaryNames() {
      return summaryNames;
   }
   
   public String getSummaryValue( final String summaryName ) {
      for (int i = 0; i < summaryNames.size(); i++ ) {
         if ( summaryName.equals(summaryNames.elementAt(i)) ) {
            return (String) summaryValues.elementAt(i);
         }
      }
      return null;
   }
   
   public void write() {
      
      openForWriting();      
      write( toString() );
      closeAfterWrite();
   }
   
   public String toString() {
      
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < summaryValues.size(); i++ ) {
         
         sb.append( summaryValues.elementAt(i) + SEPARATOR + summaryNames.elementAt(i));
         sb.append("\n");
      }
      return sb.toString();
   }
   
   public void read() {
      
      String line;
      String value;
      String name;
      
      openForReading();     

      while ( (line = readLine()) != null ) {
         
         int separator = line.indexOf(SEPARATOR);
         value = line.substring(0, separator);
         name = line.substring(separator, line.length());
         summaryNames.addElement( name );
         summaryValues.addElement( value );         
      }
      
      closeAfterRead();
   }

}
