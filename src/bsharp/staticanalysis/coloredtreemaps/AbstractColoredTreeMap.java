package bsharp.staticanalysis.coloredtreemaps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.imageio.ImageIO;

import bsharp.html.BoxOverToolTip;
import bsharp.html.HtmlColors;
import bsharp.staticanalysis.codeatlas.JavaNcssToTreemap;
import bsharp.staticanalysis.fileio.ImageMapFile;
import bsharp.staticanalysis.fileio.TreemapFlatFile;
import bsharp.staticanalysis.treemaps.JavaCodeMapItem;
import bsharp.staticanalysis.treemaps.JavaCodeMapItemFixed;
import bsharp.strings.Strings;
import edu.umd.cs.treemap.Rect;

/**
 * A class that builds a colored treemap.
 *
 */
public abstract class AbstractColoredTreeMap implements Runnable {

   private static final Color LIGHT_GREEN = HtmlColors.LIGHT_GREEN;
   private static final Color LIGHT_YELLOW = HtmlColors.LIGHT_YELLOW;
   private static final Color LIGHT_RED = HtmlColors.LIGHT_RED;
   private static final int HEIGHT = TreemapFlatFile.HEIGHT;
   private static final int WIDTH = TreemapFlatFile.WIDTH;

   private static final boolean PACKAGE_SUB_TREEMAPS_ENABLED = true;

   private static TreemapFlatFile treemapFile;
   private static ImageMapFile imagemapFile;

   private final String[] arguments;
   private Vector summaryNames = new Vector();
   private Vector summaryValues = new Vector();
   /** (String) Package names mapped to (String[][]) Summary values. */
   private HashMap packageSummaries = new HashMap();


   /** The argument number that is the treemap file to read in. */
   private static final int TREEMAP_FILE_ARG = 0;
   /** The argument number that is the output file to write to. */
   private static final int OUTPUT_FILE_ARG = 1;
   /** The argument number that is the report to read in. */
   protected static final int REPORT_FILE_ARG = 2;

   /** Printed at the start of a summary item in the html image map. */
   public static final String SUMMARY_TAG = "<!--Summary-->";
   /** Printed before the value of a summary item in the html image map. */
   public static final String SUMMARY_DELIM = ":";

   /**
    * A start a colored treemap.
    *
    * @param args
    */
   public AbstractColoredTreeMap( final String[] args ) {
      this.arguments = args;
   }

   /**
    * Create a colored treemap.
    */
   public void run() {

      // Read the items from the treemap file.
      final JavaCodeMapItemFixed[] items = readItems( arguments[TREEMAP_FILE_ARG] );

      // Get subclass to read its report.
      readMetricReport( arguments );

      // Get subclass to style the items based on its report
      styleItems( items );

      // Create the summary
      summary();

      // Draw the html image map (html file + image)
      drawProjectImageMap( items );

      // Optionally do treemaps for each package individually
      if ( PACKAGE_SUB_TREEMAPS_ENABLED ) {
         drawIndividualPackageImageMaps(items);
      }

      System.out.println("DONE.");
   }

   private void drawIndividualPackageImageMaps(final JavaCodeMapItemFixed[] items) {
      final String fs = File.separator;
      final File packageDir =
         new File( JavaNcssToTreemap.PACKAGES_FOLDER);
      packageDir.mkdir();

      for ( int i = 0; i < items.length; i++ ) {

         if ( items[i].type == JavaCodeMapItem.TYPE_PACKAGE) {

            drawIndividualPackageImageMap(items, fs, packageDir, items[i]);
         }
      }
   }

   private void drawIndividualPackageImageMap(final JavaCodeMapItemFixed[] items, final String fs,
      final File packageDir, final JavaCodeMapItemFixed packageItem) {
      final String treemapFilename =
         packageDir.getAbsolutePath() + fs + packageItem.getName() + fs
            + packageItem.getName() + ".treemap";

      // Read the items bounds from the individual package treemap file.
      final JavaCodeMapItemFixed[] packageItems = readItems(treemapFilename );

      // Grab the style from the previously styled items
      for ( int j = 0; j < packageItems.length; j++ ) {

         final JavaCodeMapItemFixed styledItem =
            getItem( items, packageItems[j].getName());
         packageItems[j].style = styledItem.style;
         packageItems[j].getImageMapItem().setToolTip(styledItem.getImageMapItem().getToolTip());
      }

      drawPackageImageMap(
               packageItems,
               packageItem.getName() );
   }

   private JavaCodeMapItemFixed getItem( final JavaCodeMapItemFixed[] items, final String name ) {
      for ( int i = 0; i < items.length; i++ ) {
         if ( items[i].getName().equals(name)) {
            return items[i];
         }
      }
      return null;
   }

   /**
    * Read map iteams from a file.
    * @param args thie file to read from.
    * @return an array of items.
    */
   private JavaCodeMapItemFixed[] readItems(final String treemapFilename) {

      treemapFile = new TreemapFlatFile(treemapFilename);
      treemapFile.openForReading();

      final Vector items = new Vector();
      JavaCodeMapItemFixed nextItem;
      while ( (nextItem = treemapFile.readItem()) != null ) {
         items.add(nextItem);
      }

      treemapFile.closeAfterRead();

      return (JavaCodeMapItemFixed[]) items.toArray( new JavaCodeMapItemFixed[items.size()] );
   }

   /**
    * Only create links if the items[] array contains pacakge items.
    * @param outputFileName
    * @param items
    * @return
    */
   private String linksToIndividualPackageTreemaps( final JavaCodeMapItemFixed[] items) {

      // Optionally create list of links to packages
      if ( PACKAGE_SUB_TREEMAPS_ENABLED ) {
         final StringBuffer links = new StringBuffer();

         links.append(ImageMapFile.SEP);
         links.append("<SELECT onChange=\"document.location=options[selectedIndex].value;\">");
         links.append("<OPTION VALUE=\"\">Go to a Package...");

         final File packageDir =
            new File( JavaNcssToTreemap.PACKAGES_FOLDER);
         packageDir.mkdir();
         final int packageCount = createPackageOptionLinks(items, links);
         links.append("</SELECT>");

         if ( packageCount > 0) {
            return links.toString();
         }
      }
      return "";

   }

   private int createPackageOptionLinks(
      final JavaCodeMapItemFixed[] items, final StringBuffer links ) {

      int packageCount = 0;
      final String fs = "/";
      for ( int i = 0; i < items.length; i++ ) {
         if ( items[i].type == JavaCodeMapItem.TYPE_PACKAGE) {
            links.append("<OPTION");
            links.append(" VALUE=\"");
            links.append(JavaNcssToTreemap.PACKAGES_FOLDER + fs);
            links.append(items[i].getName());
            links.append(fs + outputFile());
            links.append("\"");
            links.append(">");
            links.append(items[i].getName());
            links.append("</OPTION>");
            packageCount++;
         }
      }
      return packageCount;
   }

   private void drawProjectImageMap( final JavaCodeMapItemFixed[] items ) {

      final String rawReportFileName = new File(arguments[REPORT_FILE_ARG]).getName();

      drawImageMap(
         outputFile(),
         items,
         rawReportFileName,
         createSummary(),
         linksToIndividualPackageTreemaps(items),
         BoxOverToolTip.JSCRIPT_FILENAME);
   }

   private void drawPackageImageMap(
      final JavaCodeMapItemFixed[] items, final String packageName ) {

      final String fs = File.separator;
      final String relPathPrefix = "../../";

      final String outputFile = new File( JavaNcssToTreemap.PACKAGES_FOLDER).getAbsolutePath()
         + fs + packageName + fs + outputFile();
      final String rawReportFileName =
         relPathPrefix + new File(arguments[REPORT_FILE_ARG]).getName();

      drawImageMap(
         outputFile,
         items,
         rawReportFileName,
         createSummary(packageName),
         "",
         relPathPrefix + BoxOverToolTip.JSCRIPT_FILENAME);

   }

   /**
    * Draw the treemap image.
    * @param items the items to draw in it.
    */
   private void drawImageMap(
      final String outputFilename,
      final JavaCodeMapItemFixed[] items,
      final String rawReportFileName,
      final String summary,
      final String packageLinks,
      final String javaScriptFile) {

      imagemapFile = new ImageMapFile( outputFilename );
      imagemapFile.openForWriting();

      final StringBuffer headerContents = new StringBuffer();
      headerContents.append(rawReportLink(rawReportFileName));
      headerContents.append(ImageMapFile.SEP);
      headerContents.append(summary);
      headerContents.append(packageLinks);

      imagemapFile.writeMapHeader(
         headerContents.toString(),
         javaScriptFile);

      // Create buffered image that does not support transparency
      final BufferedImage bimage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

      // Create a graphics context on the buffered image
      final Graphics2D g2d = bimage.createGraphics();
      g2d.setColor(Color.white);
      g2d.fillRect(0, 0, WIDTH, HEIGHT);

      // Draw the image
      final Rect childBounds = new Rect(0, 0, WIDTH, HEIGHT);
      drawMapItems( items, childBounds, g2d);

      g2d.dispose();

      // Save to file
      try {
         ImageIO.write(
            bimage,
            ImageMapFile.IMG_FORMAT,
            new File( outputFilename + ImageMapFile.IMG_EXTENSION ) );
      } catch (final IOException e) {
         e.printStackTrace();
      }

      // Close output file
      imagemapFile.writeMapFooter();
      imagemapFile.closeAfterWrite();
   }

   /**
    * Draws map items according to their style.
    * @param items
    * @param bounds
    * @param g2d
    */
   private void drawMapItems(
      final JavaCodeMapItemFixed[] items, final Rect bounds, final Graphics2D g2d ) {

      for (JavaCodeMapItemFixed item : items) {

         final Rect r = item.getBounds();

         g2d.setColor(item.style.color);

         if ( item.style.visible ) {
            final Rectangle rounded = new Rectangle(
               (int)Math.round(r.mX),
               (int)Math.round(r.mY),
               (int)Math.round(r.mW),
               (int)Math.round(r.mH) );
            g2d.fill(rounded);

            // Outline
            if ( item.style.outline > 0 ) {
               g2d.setColor(item.style.outlineColor);
               g2d.drawRect((int)r.mX, (int)r.mY, (int)r.mW, (int)r.mH );
            }

            imagemapFile.writeMapEntry(item.getImageMapItem());
         }
      }
   }

   /**
    * Style each of the items before they are drawn.
    * @param items
    */
   private void styleItems( final JavaCodeMapItemFixed[] items ) {
      for (JavaCodeMapItemFixed item : items) {
         styleItem( item );
      }
   }

   /**
    * Style an item accrording to its type.
    * @param item
    */
   private void styleItem( final JavaCodeMapItemFixed item ) {

      switch ( item.type ) {
         case JavaCodeMapItem.TYPE_METHOD: {
            styleMethod( item );
            break;
         }
         case JavaCodeMapItem.TYPE_CLASS: {
            styleClass( item );
            break;
         }
         case JavaCodeMapItem.TYPE_PACKAGE: {
            stylePackage( item );
            break;
         }
      }
   }

   /**
    * Override this to print a summay at the top of the tree map.
    * @return
    */
   abstract void summary();

   private String createSummary(final String packageName) {

      final StringBuffer sb = new StringBuffer();

      final String[][] summaries = (String[][]) packageSummaries.get(packageName);

      if ( summaries != null ) {
         for ( int i = 0; i < summaries.length; i++ ) {
            sb.append(Strings.NL).append(SUMMARY_TAG).append(summaries[i][0]);
            sb.append(SUMMARY_DELIM).append(Strings.SPACE);
            sb.append(summaries[i][1]);
            if ( i + 1 < summaries.length) {
               sb.append(ImageMapFile.SEP);
            }
         }
         sb.append(Strings.NL);
         return sb.toString();
      }
      return "";
   }
   private String createSummary() {

      final StringBuffer sb = new StringBuffer();

      for ( int i = 0; i < summaryNames.size(); i++ ) {
         sb.append(Strings.NL).append(SUMMARY_TAG).append(summaryNames.elementAt(i));
         sb.append(SUMMARY_DELIM).append(Strings.SPACE);
         sb.append(summaryValues.elementAt(i));
         if ( i + 1 < summaryNames.size()) {
            sb.append(ImageMapFile.SEP);
         }
      }
      sb.append(Strings.NL);
      return sb.toString();
   }

   public void addSummary(final String name, final String value) {

      summaryNames.add(name);
      summaryValues.add(value);
   }

   public void addPackageSummary( final String packageName, final String[][] summaries) {
      packageSummaries.put( packageName, summaries);
   }
//   private void addPackageSummaries(final JavaCodeMapItemFixed[] items) {
//
//      for ( int i = 0; i < items.length; i++) {
//         final JavaCodeMapItemFixed next = items[i];
//         if ( next.type ==  JavaCodeMapItemFixed.TYPE_PACKAGE) {
//            packageSummaries.put( next.jniSignature, addPacakgeSummay(next.jniSignature));
//
//         }
//      }
//
//   }
//
//   abstract String[] addPacakgeSummay( final String packageName );

   /**
    * Read the metric values that will be used to color the treemap.
    * @param args the arguments passed in from the command line.
    */
   abstract void readMetricReport( final String[] args );

   /**
    * Override this to style method items.
    * @param item
    */
   abstract void styleMethod( JavaCodeMapItemFixed item );

   /**
    * Override this to style class items.
    * @param item
    */
   abstract void styleClass( JavaCodeMapItemFixed item );

   /**
    * Override this to style package items.
    * @param item
    */
   abstract void stylePackage( JavaCodeMapItemFixed item );

   /**
    * The file to write the treemap to.
    * Defaults to the 2nd commandline arg.
    */
   public String outputFile() {
      // By default use the 2nd command line arg.
      return arguments[OUTPUT_FILE_ARG];
   }

   /**
    * Style and item the default green color scheme.
    * @param item the item to style.
    */
   protected void styleGreen( final JavaCodeMapItemFixed item ) {
      item.style.outline = 1;
      item.style.outlineColor = Color.green;
      item.style.color = LIGHT_GREEN;
   }

   /**
    * Style and item the default Yellow color scheme.
    * @param item the item to style.
    */
   protected void styleYellow( final JavaCodeMapItemFixed item ) {
      item.style.outline = 1;
      item.style.color = LIGHT_YELLOW;
      item.style.outlineColor = Color.yellow;
   }

   /**
    * Style and item the default red color scheme.
    * @param item the item to style.
    */
   protected void styleRed( final JavaCodeMapItemFixed item ) {
      item.style.outline = 1;
      item.style.color = LIGHT_RED;
      item.style.outlineColor = Color.red;
   }

   /**
    * Create an html link to the raw report file.
    * @return
    */
   protected String rawReportLink(final String relativeInputReportFilename) {
      final StringBuffer sb = new StringBuffer();
      sb.append("(<a href=\"").append(relativeInputReportFilename).append("\">");
      sb.append("View Raw Report");
      sb.append("</a>)");
      return sb.toString();
   }

//   private String outputFile() {
//      System.out.println(" outfile: " + this.getClass().getSimpleName() + ".html");
//      return this.getClass().getSimpleName() + ".html";
//   }
}
