package bsharp.staticanalysis.fileio;

import bsharp.fileio.EasyFile;

/**
 * The format of the text file report.
 *
 * @author brandon_sharp@tvworks.com
 *
 */
public class JdependReportFile extends EasyFile {

   /** The start of the Package Dependency Cycles section. */
//                                                     "- Package Dependency Cycles"
   public static final String START_PACKAGE_CYCLES = "- Package Dependency Cycles:";
//   /** Ignore lines starting with this string. */
//   public static final String IGNORE = "---";
   /** Marks the end of a section of the file. */
   public static final String END_SECTION =
      "--------------------------------------------------";

   /** The default state while parsing the file. */
   public static final int STATE_NONE = 0;
   /** Parsing through the Package Dependency Cycles section. */
   public static final int STATE_PACKAGE_CYCLES = 1;


   /**
    * Create a new helper class for reading/writing.
    * @param filename the file to access.
    */
   public JdependReportFile( final String filename ) {
      super(filename);
   }

   /**
    * Returns true if the line is to be ignored.
    * @param line true if the line is to be ignored.
    * @return
    */
   public static boolean ignore( final String line ) {
      return line.length() == 0 || line.startsWith(" ");
   }

}
