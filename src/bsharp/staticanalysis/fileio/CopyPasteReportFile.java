package bsharp.staticanalysis.fileio;

import bsharp.fileio.EasyFile;
import bsharp.strings.JavaJNIStrings;


/**
 * The format of the text file report.
 *
 * @author brandon_sharp@tvworks.com
 *
 */
public class CopyPasteReportFile extends EasyFile {

   /** The start of filename entry. */
   public static final String FILE_LINE = "Starting at line ";
   /** The column the filename is in. */
   public static final int FILE_NAME_COL = 5;

   /** The start duplication entry. */
   public static final String DUPLICATOIN_INFO = "Found a ";
   /** The column the duplication size appears in. */
   public static final int DUPLICATOIN_SIZE_COL = 2;

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
   public CopyPasteReportFile( final String filename, final String _sourceFileDir ) {
      super(filename);
      this.sourceFileDir = _sourceFileDir;
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
