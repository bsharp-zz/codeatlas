package bsharp.staticanalysis.fileio;

import bsharp.fileio.EasyFile;

/**
 * The format of the text file report.
 *
 * @author brandon_sharp@tvworks.com
 *
 */
public class JavaNcssFile extends EasyFile {

   /** Start of packages. */
   public static final String START_OF_PACKAGES =
      "Nr.   Classes Functions      NCSS  Javadocs Package";
   /** Start of classes. */
   public static final String START_OF_CLASSES = "Nr. NCSS Functions Classes Javadocs Class";
   /** Start of methods. */
   public static final String START_OF_METHODS = "Nr. NCSS CCN JVDC Function";

   /** End of packages section. */
   public static final String END_OF_PACKAGES = "---";

   /** Total lines for projects. */
   public static final String TOTAL_LINES = "Program NCSS:";

   /** Average Lines per method. */
   public static final String AVG_METHOD_LINES = "Average Function NCSS:";
   /** Column number when a line is split. */
   public static final int COL_AVG_METHOD_LINES = 3;

   /** The start of line holding this metric. */
   public static final String AVG_METHOD_COMPLEXITY = "Average Function CCN:";
   /** Column number when a line is split. */
   public static final int COL_AVG_METHOD_COMPLEXITY = 3;

   /** The start of line holding this metric. */
   public static final String AVG_METHOD_JAVADOC = "Average Function JVDC:";
   /** Column number when a line is split. */
   public static final int COL_AVG_METHOD_JAVADOC = 3;

   // Counting from 0

   /** Column number when a line is split. */
   public static final int COL_PACKAGE_NAME = 5;
   /** Column number when a line is split. */
   public static final int COL_PACKAGE_NCSS = 3;
   /** Column number when a line is split. */
   public static final int COL_CLASS_NAME = 5;
   /** Column number when a line is split. */
   public static final int COL_CLASS_INNERCLASSES = 3;
   /** Column number when a line is split. */
   public static final int COL_CLASS_NCSS = 1;
   /** Column number when a line is split. */
   public static final int COL_METHOD_NAME = 4;
   /** Column number when a line is split. */
   public static final int COL_METHOD_NCSS = 1;
   /** Column number when a line is split. */
   public static final int COL_METHOD_CCN = 2;
   /** Column number when a line is split. */
   public static final int COL_METHOD_JAVADOC = 3;

   /** Column number when a line is split. */
   public static final int COL_NCSS = 1;
   /** Column number when a line is split. */
   public static final int COL_JAVADOCS = 3;
   /** A state while parsing the file. */
   public static final int STATE_NONE = -1;
   /** A state while parsing the package section of the file. */
   public static final int STATE_PACKAGES = 0;
   /** A state while parsing the class section of the file. */
   public static final int STATE_CLASSES = 1;
   /** A state while parsing the method section of the file. */
   public static final int STATE_METHODS = 2;

   /**
    * Create a new helper class for reading/writing.
    * @param filename the file to access.
    */
   public JavaNcssFile( final String filename ) {
      super(filename);
   }

   /**
    * True if string formats to integer correctly.
    * @param s the string to test.
    * @return True if string formats to integer correctly.
    */
   public static boolean isNumeric(final String s) {
      try {
         Integer.parseInt(s.trim());
         return true;
      } catch ( final NumberFormatException e) {
         return false;
      }
   }
}
