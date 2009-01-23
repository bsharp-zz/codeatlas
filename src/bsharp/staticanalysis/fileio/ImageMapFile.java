package bsharp.staticanalysis.fileio;

import java.awt.Rectangle;

import bsharp.fileio.EasyFile;
import bsharp.html.HtmlImageMapItem;

/**
 * For reading/writing an html image map file.
 * @author brandon_sharp@tvworks.com
 *
 */
public class ImageMapFile extends EasyFile {

   /** The image format for the image. */
   public static final String IMG_FORMAT = "png";
   /** The extension of the image format. */
   public static final String IMG_EXTENSION = "." + IMG_FORMAT;
   /** Separate summary items. */
   public static final String SEP = "\n&nbsp;|&nbsp;";

   private static final String NEW_LINE = "\n";
   /**
    * For reading/writing an html image map file.
    * @param filename
    */
   public ImageMapFile( final String filename ) {
      super(filename);
   }

   /**
    * Write the header.
    * @param summary
    */
   public void writeMapHeader( final String summary, final String boxoverJavaScript ) {
      final StringBuffer sb = new StringBuffer();

      // boxover
      sb.append("<head><SCRIPT SRC=\"").append(boxoverJavaScript);
      sb.append("\"></SCRIPT></head>");

      sb.append("<style>body, td, pre { font-family: Verdana, Arial, Helvetica, sans-serif;");
      sb.append(" font-size: 12px;}</style>");

      sb.append( summary );

      sb.append("<br/><IMG border=0 SRC=\"");
      sb.append(getName());
      sb.append(IMG_EXTENSION);
      sb.append("\" USEMAP=\"#web\" />");

      sb.append("<map id=\"web\" name=\"web\"> \n");

      write(sb.toString());
      write(NEW_LINE);
   }

   /**
    * Write the Footer.
    * @param summary
    */
   public void writeMapFooter() {
      write("</map>");
   }

   /**
    * Write an image map entry to the file.
    * @param item the map item to write.
    */
   public void writeMapEntry(final HtmlImageMapItem item) {
      //<area shape="rect"
      // href="SoftwareDevelopment/StaticAnalysis/Tools/
      // Checkstyle/CheckstyleConfiguration_ToolsAndCmac.html"
      // title="CheckstyleConfiguration_ToolsAndCmac.html" alt="" coords="114,459,216,469"/>
      //onmouseover="window.status='Contact'" onmouseout="window.status=''"

      final String quote = "\"";
      final String comma = ",";

      final StringBuffer sb = new StringBuffer();
      final Rectangle r = item.getBounds();

      sb.append("<area shape=\"rect\"");

//      sb.append(" href=\"").append("info://").append(item.name).append(quote);

      sb.append(" title=\"").append(item.getToolTip()).append(quote);

//         sb.append(" alt=\"").append(item.name).append(quote);

//         sb.append(" onmouseover=\"showtip('").append(item.name).append("')\"");

      //coords="left,top,right,bottom"




      final int right = (int)(r.x + r.width);
      final int bottom = (int)(r.y + r.height);
      sb.append(" coords=\"").append((int)r.x).append(comma).append((int)r.y).append(comma);
      sb.append(right).append(comma).append(bottom).append(quote);

      sb.append("/>");

      write(sb.toString());
      write(NEW_LINE);


   }


}
