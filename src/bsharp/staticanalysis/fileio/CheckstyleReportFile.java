package bsharp.staticanalysis.fileio;

import bsharp.fileio.EasyFile;
import bsharp.strings.JavaJNIStrings;
import bsharp.strings.Split;

/**
 * The format of the text file report.
 *
 * @author brandon_sharp@tvworks.com
 *
 */
public class CheckstyleReportFile extends EasyFile {

   /** Separates fields on a line. */
   public static final String FEILD_DELIMITER = ":";

   /** The column of the filename field. */
   public static final int COL_FILENAME = 0;
   /** The column of the line location field. */
   public static final int COL_LINE_NUM = 1;
   /** The column of the column location field. */
   public static final int COL_COL_NUM = 2;
   /** The column of the warning field. */
   public static final int COL_WARNING = 3;
   /** The column of the description field. */
   public static final int COL_RULE_DESC = 4;

   private final String sourceFileDir;

   /**
    * Returns true if the line is to be ignored.
    * @param line true if the line is to be ignored.
    * @return
    */
   public static boolean ignore( final String line ) {
      return line.equals( "Starting audit..." ) || line.equals("Audit done.");
   }


   /**
    * Create a new helper class for reading/writing.
    * @param filename the file to access.
    */
   public CheckstyleReportFile( final String filename, final String _sourceFileDir ) {
      super(filename);
      this.sourceFileDir = _sourceFileDir;
   }

   /**
    * Read the next violation from the file.
    * @return
    */
   public StyleViolation readNextViolation() {
      String line = super.readLine();

      // End of file.
      if ( line == null) {
         return null;
      }

      while ( ignore( line ) ) {
         line = super.readLine();

         if ( line == null) {
            return null;
         }
      }

      // For windows paths
      line = checkForWindowsDrives( line );

      final Split parts = new Split( line, FEILD_DELIMITER);

      final StyleViolation result = new StyleViolation( parts.get( COL_FILENAME ));

      // TODO more fields...

      return result;

   }

   /**
    * Checks for the windows drive in the string, as it
    * collides with the normal field delimiter.
    *
    * @param line
    * @return
    */
   private String checkForWindowsDrives( final String line ) {
      // For windows paths
      if ( line.charAt(1) == ':' ) {
         return line.replaceFirst(FEILD_DELIMITER, ".");
      }
      return line;
   }

   /**
    * A Checksytye rule violation.
    *
    * @author brandon_sharp@tvworks.com
    *
    */
   public class StyleViolation {

      /** The fully qualified classname. */
      public final String packageAndClassName;

      /**
       * Create a new Checksytye rule violation.
       * @param filename
       */
      public StyleViolation( final String _filename) {
         packageAndClassName = readClassName( _filename );
      }
   }

   /**
    * Converts an absolute file location and converts it to a java classname.
    * @param fullpath full file system path.
    * @return
    */
   public String readClassName( final String fullpath ) {
      return JavaJNIStrings.fullPathToClassname( fullpath, sourceFileDir);
   }

}
