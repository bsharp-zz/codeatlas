package bsharp.staticanalysis.fileio;

import bsharp.fileio.EasyFile;
import bsharp.staticanalysis.treemaps.JavaCodeMapItem;
import bsharp.staticanalysis.treemaps.JavaCodeMapItemFixed;
import bsharp.strings.Split;
import edu.umd.cs.treemap.Rect;

/**
 * A class to create or read a treemap (coordinates of squares).
 * @author brandon_sharp@tvworks.com
 *
 */
public class TreemapFlatFile extends EasyFile {

   /** The treemap height. */
   public static final int HEIGHT = 600;
   /** The treemap width. */
   public static final int WIDTH = 700;

   /** Delimiter between fields. */
   private static final String DELIM = ":";

   /**
    * Create a helper class to
    * create or read a treemap (coordinates of squares).
    * @param filename
    */
   public TreemapFlatFile( final String filename ) {
      super(filename);
   }

   /**
    * Write an item (coordinates of square) to the file.
    * @param item
    */
   public void writeItem( final JavaCodeMapItem item ) {
      write("MAP-ITEM: "); // col 0
      write("Size:"); // col 1
      write(new Double(item.getSize()).toString()); //col 2
      write(DELIM);
      // "Rect: "  // col 3
      write(item.getBounds().toString()); // col4
      write(":Type:"); //col5
      write(String.valueOf(item.type)); //col6
      write(": P:"); // col7
      write(item.packageName); // col8
      write(": C:"); // col9
      write(item.className); // col10
      write(": M:"); // col11
      write(item.methodName); // col12
      write( "\n" );
   }

   /**
    * Read a map item from the file.
    * @return
    */
   public JavaCodeMapItemFixed readItem() {

      final String line = readLine();

      if ( line == null ) {
         return null;
      }

      final Split parts = new Split( line, DELIM);

      final double size = Double.parseDouble( parts.get(2));
      final Rect bounds = readRect(parts.get(4));
      final int type = Integer.parseInt(parts.get(6));
      final String packageName = parts.get(8);
      final String className = parts.get(10);
      final String methodName = parts.get(12);

      final JavaCodeMapItemFixed newItem =
         new JavaCodeMapItemFixed( packageName, className, methodName, size, type, bounds );

      return newItem;
   }

   /**
    * Read a rectangle from an item entry in the file.
    * @param segment
    * @return
    */
   private Rect readRect(final String segment) {

      final Split parts = new Split( segment, ",");

      int i = 0;
      final double x = Double.parseDouble( parts.get(i++).trim());
      final double y = Double.parseDouble( parts.get(i++).trim());
      final double w = Double.parseDouble( parts.get(i++).trim());
      final double h = Double.parseDouble( parts.get(i++).trim());

      return new Rect(x, y, w, h);
   }

}
