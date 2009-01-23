package bsharp.staticanalysis.codeatlas;

import java.io.File;
import java.util.Vector;

import bsharp.html.Tags;
import bsharp.staticanalysis.fileio.JavaNcssFile;
import bsharp.staticanalysis.fileio.TreemapFlatFile;
import bsharp.staticanalysis.treemaps.JavaCodeMapItem;
import bsharp.strings.Split;
import bsharp.strings.Strings;
import edu.umd.cs.treemap.Rect;
import edu.umd.cs.treemap.SquarifiedLayout;

/**
 * A class for creating a treemap from a JavaNcss report.
 * @author brandon_sharp@tvworks.com
 *
 */
public class JavaNcssToTreemap {

   /** The folder name, that holds the individual package treemaps. */
   public static final String PACKAGES_FOLDER = "packages";
   /** The file name extension for treemap files. */
   public static final String TREEMAP_EXTENSION = ".treemap";

   private static final int HEIGHT = TreemapFlatFile.HEIGHT;
   private static final int WIDTH = TreemapFlatFile.WIDTH;

   private static final SquarifiedLayout ALGORITHM  = new SquarifiedLayout();

   private static final boolean PACKAGE_SUB_TREEMAPS_ENABLED = true;


   /** Delimiter between java names. */
   private static final String DELIM = ".";

   private static TreemapFlatFile treemapFile;

   /**
    * Create a class for creating a treemap from a JavaNcss report.
    * @param args filename - the javancss text report.
    */
   public static void main(final String[] args) {

      System.out.println("Loading map items from file");
      final JavaCodeMapItem[] items = javaNcssReportToMapItems( args );

      treemapFile = new TreemapFlatFile(args[0] + TREEMAP_EXTENSION);
      treemapFile.openForWriting();

      // Write treemap to text file
      final Rect childBounds = new Rect(0, 0, WIDTH, HEIGHT);
      writeMapItems( items, childBounds);

      // Close output file
      treemapFile.closeAfterWrite();

      if ( PACKAGE_SUB_TREEMAPS_ENABLED ) {

         writeIndividualPackageTreemaps(args, items, childBounds);
      }

      System.out.println("DONE.");
   }

   private static void writeIndividualPackageTreemaps(final String[] args,
      final JavaCodeMapItem[] items, final Rect childBounds) {

      final File outDir = new File( new File(args[0]).getParentFile().getAbsolutePath() );
      final String fs = File.separator;
      final File packageDir =
         new File( outDir + fs + PACKAGES_FOLDER);
      packageDir.mkdir();

      for ( int i = 0; i < items.length; i++ ) {

         if ( items[i].type == JavaCodeMapItem.TYPE_PACKAGE) {

            final JavaCodeMapItem currPackage = items[i];
            final JavaCodeMapItem[] currChildren = currPackage.getChildren();

            // Create individual package dir.
            new File( packageDir.getAbsolutePath() + fs
               + currPackage.getName()).mkdir();

            treemapFile = new TreemapFlatFile(packageDir.getAbsolutePath() + fs
               + currPackage.getName() + fs + currPackage.getName() + TREEMAP_EXTENSION);
            treemapFile.openForWriting();

            // Write treemap to text file
            final Rect packageChildBounds = new Rect(0, 0, WIDTH, HEIGHT);
            writeMapItems( currChildren, childBounds);

            treemapFile.closeAfterWrite();

         }
      }
   }

   /**
    * Transform a java ncss report to an array of items to
    * later be layed out in a tree map.
    * @param args
    * @return
    */
   private static JavaCodeMapItem[] javaNcssReportToMapItems( final String[] args ) {
      final Vector pacakgesV = new Vector();

      final JavaNcssFile javaNcssReport = new JavaNcssFile( args[0]);
      javaNcssReport.openForReading();


      String line;
      int state = JavaNcssFile.STATE_NONE;
      while ( (line = javaNcssReport.readLine()) != null ) {

         line = line.trim();

         state = switchState( line, state );

         final Split parts = new Split(line, Split.ONE_OR_MORE_SPACES);

         // Invalid line
         if ( !JavaNcssFile.isNumeric(parts.get(0)) ) {
            continue;
         }

         readItem( state, pacakgesV, parts);
      }

      javaNcssReport.closeAfterRead();

      final JavaCodeMapItem[] items =
         (JavaCodeMapItem[]) pacakgesV.toArray(new JavaCodeMapItem[pacakgesV.size()]);

      validateItems( items );

      return items;
   }

   /**
    * Change to state while parsing through the file.
    * @param line
    * @param currentState
    * @return
    */
   private static int switchState(final String line, final int currentState ) {
      int state = currentState;
      if ( line.startsWith(JavaNcssFile.START_OF_PACKAGES)) {
         state = JavaNcssFile.STATE_PACKAGES;
      } else if ( line.startsWith(JavaNcssFile.END_OF_PACKAGES)) {
         state = JavaNcssFile.STATE_NONE;
      } else if ( line.startsWith(JavaNcssFile.START_OF_CLASSES)) {
         state = JavaNcssFile.STATE_CLASSES;
      } else if ( line.startsWith(JavaNcssFile.START_OF_METHODS)) {
         state = JavaNcssFile.STATE_METHODS;
      }
      return state;
   }

   /**
    * Read a javancss item from the line parts given.
    * @param state
    * @param pacakgesV
    * @param parts
    */
   private static void readItem( final int state, final Vector pacakgesV, final Split parts ) {

      if ( state == JavaNcssFile.STATE_PACKAGES ) {

         final int margin = 4;
         final String name = parts.get(JavaNcssFile.COL_PACKAGE_NAME);
         final int ncss = parseInt(parts.get(JavaNcssFile.COL_PACKAGE_NCSS));
//         System.out.println("Adding package: [" + name + "]");
         pacakgesV.add( new JavaCodeMapItem( name, "", "",
            ncss, JavaCodeMapItem.TYPE_PACKAGE, margin ) );

      } else if ( state == JavaNcssFile.STATE_CLASSES ) {
         readClass( pacakgesV, parts );
      } else if ( state == JavaNcssFile.STATE_METHODS ) {
         readMethod( pacakgesV, parts );
      }
   }

   /**
    * Read a class from a javanss file.
    * @param pacakgesV
    * @param parts
    */
   private static void readClass(final Vector pacakgesV, final Split parts) {

      // NOTE: Inner classes are not in this list

      // Read package name
      final String fullyQualifiedName = parts.get(JavaNcssFile.COL_CLASS_NAME);
      final Split nameParts = new Split(fullyQualifiedName, Split.DOT);
      final String className = nameParts.get(nameParts.size() - 1);
      final String packageName = Strings.subtractRight(fullyQualifiedName, DELIM + className);

      // Get parent package object
//      System.out.println("Adding class, package name: [" + packageName + "]");
      final int index = pacakgesV.indexOf( JavaCodeMapItem.searchObject(packageName));
      final JavaCodeMapItem packageItem = (JavaCodeMapItem)pacakgesV.elementAt(index);

      // Add this class to its children
      final int ncss = parseInt(parts.get(JavaNcssFile.COL_CLASS_NCSS));
//      System.out.println("Adding class: " + className);
      packageItem.addChild(
         new JavaCodeMapItem( packageName, className, "", ncss, JavaCodeMapItem.TYPE_CLASS, 2 ) );

   }

   /**
    * Read a method from a javanss file.
    * @param pacakgesV
    * @param parts
    */
   private static void readMethod(final Vector pacakgesV, final Split parts) {

      // Read package name
      final String fullyQualifiedName = parts.get(JavaNcssFile.COL_METHOD_NAME);

      // Strip parameter signature off
      final int parameters = fullyQualifiedName.indexOf("(");
      final String noParams = fullyQualifiedName.substring(0, parameters);

      // Find a package that exists
      final int[] packageNamePartIndex = new int[] {-1};
      final String packageName = findPackage(noParams, pacakgesV, packageNamePartIndex);

      final Split fullyQualifiedNameParts = new Split(noParams, Split.DOT);

      final int packageIndex = pacakgesV.indexOf( JavaCodeMapItem.searchObject(packageName));
      final JavaCodeMapItem packageItem = (JavaCodeMapItem)pacakgesV.elementAt(packageIndex);

      // Class name
      final String simpleClassName = fullyQualifiedNameParts.get(packageNamePartIndex[0] + 1);
      final String className = packageName + DELIM
         + simpleClassName;

      // Get parent class object
      final JavaCodeMapItem classItem = packageItem.getChild(className);

      // Add this method to its children
      final int ncss = parseInt(parts.get(JavaNcssFile.COL_METHOD_NCSS));
      final int methodIndex = className.length() + DELIM.length();
      final String methodName =
         fullyQualifiedName.substring(methodIndex, fullyQualifiedName.length());
//      System.out.println("TEMP: method Name: " + methodName);
      final String nameWithBreaks = new StringBuffer(packageName).append(
         Tags.BR).append(simpleClassName).append(Tags.BR).append(methodName).toString();
      // NOTE: this method name may have an inner class name prepended.
//      System.out.println("Adding Method: " + fullyQualifiedName);
      classItem.addChild(
         new JavaCodeMapItem( packageName, simpleClassName, methodName,
            ncss, JavaCodeMapItem.TYPE_METHOD, 0 ) );

   }

   /**
    * File the correct package name for the method name given.
    * @param noParams the method name, with the parameter signature stripped off.
    * @param pacakgesV
    * @param packageNamePartIndex
    * @return
    */
   private static String findPackage(
      final String noParams, final Vector pacakgesV, final int[] packageNamePartIndex) {

      final Split fullyQualifiedNameParts = new Split(noParams, Split.DOT);
      String possibleMatch = fullyQualifiedNameParts.get(0);
      String packageName = "";

      for ( int j = 0; j < fullyQualifiedNameParts.size(); j++ ) {
         if ( -1 != pacakgesV.indexOf( JavaCodeMapItem.searchObject(possibleMatch)) ) {
            // Replace the package name with this longer match.
            packageName = possibleMatch;
            packageNamePartIndex[0] = j;
         }
         possibleMatch = possibleMatch + DELIM + fullyQualifiedNameParts.get(j + 1);
      }
      return packageName;
   }

   /**
    * Validate that the items are as expected.
    * Trace warnings if problems are found.
    * @param items
    */
   private static void validateItems(final JavaCodeMapItem[] items  ) {
      for ( int i = 0; i < items.length; i++ ) {

         if ( items[i].type != JavaCodeMapItem.TYPE_METHOD) {
            validatePackageOrClass( items[i] );
         } else {
            validateMethod( items[i]);
         }

         final JavaCodeMapItem[] children = items[i].getChildren();
//            items[i].children.toArray(new JavaCodeMapItem[items[i].children.size()]);
         validateItems(children);
      }

   }

   /**
    * @see JavaNcssToTreemap validateItems
    * @param item
    */
   private static void validatePackageOrClass(final JavaCodeMapItem item) {
      if ( item.getChildren().length == 0 ) {
         System.out.println("WARNING: non-method item has no children: " + item.jniSignature);
      }
   }

   /**
    * @see JavaNcssToTreemap validateItems
    * @param item
    */
   private static void validateMethod( final JavaCodeMapItem item) {
      if ( item.getChildren().length != 0 ) {
         System.out.println("WARNING: method item has children: " + item.jniSignature);
      }
   }

   /**
    * Write all the map items to the output file.
    * @param items
    * @param bounds
    */
   private static void writeMapItems( final JavaCodeMapItem[] items, final Rect bounds ) {

      ALGORITHM.layout(items, bounds);

      for (JavaCodeMapItem item : items) {

         treemapFile.writeItem(item);

         // Recursively write children
         final Rect r = item.getBounds();
         final int m = item.innerMargin; // margin
         final Rect childBounds =
            new Rect((int)r.mX + m, (int)r.mY + m, (int)r.mW - m, (int)r.mH - m );
         final JavaCodeMapItem[] children = item.getChildren();
         //.children.toArray(new JavaCodeMapItem[item.children.size()]);
         writeMapItems( children, childBounds);
      }
   }

   private static int parseInt( final String s ) {
      try {
         return Integer.parseInt(s.trim());
      } catch ( final NumberFormatException e) {
         System.err.println(" WARNING ERROR parsing string to integer: " + s);
         return 0;
      }
   }


}

