package bsharp.staticanalysis.treemaps;

import java.util.Vector;

import edu.umd.cs.treemap.MapItem;
import edu.umd.cs.treemap.Rect;

/**
 * A treemap item with more attributes.
 * @author brandon_sharp@tvworks.com
 *
 */
public class MapItemExtended extends MapItem {

   private final Vector childrenList = new Vector();

   /** Children will be drawn within bounds minus innerMargin. */
   public final int innerMargin;

   /** Name or id of this item. */
   public final String name;

   private static final int DEFAULT_ORDER = 0;

   /**
    * A Map item that has NOT had its bounds calculated.
    * @param name
    * @param innerMargin
    */
   public MapItemExtended(
      final String newName,
      final double _size, final int _innerMargin) {

      super( _size, DEFAULT_ORDER);
      this.name = newName;
      this.innerMargin = _innerMargin;

   }

   /**
    * For equals or searching.
    * @param name
    */
   protected MapItemExtended( final String searchName ) {
      this.name = searchName;
      this.innerMargin = 0;
   }

   /**
    * Get the items name.
    * @return
    */
   public String getName() {
      return name;
   }

   /**
    * Return true if the map items are
    * of the same name.
    */
   public boolean equals( final Object o ) {
      if ( o instanceof  MapItemExtended) {
         return ((MapItemExtended)o).getName().equals(name);
      }
      return false;
   }




}
