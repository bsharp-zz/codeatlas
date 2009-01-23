/**
 * Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA
 * and Martin Wattenberg, w@bewitched.com
 * All rights reserved.
 * Authors: Benjamin B. Bederson and Martin Wattenberg
 * http://www.cs.umd.edu/hcil/treemaps
 */

package edu.umd.cs.treemap;

/**
 * A simple implementation of the Mappable interface.
 */
public class MapItem implements Mappable {

   double size;
   Rect bounds;
   int order;
   int depth;

   public MapItem(final double _size, final int _order) {
      this.size = _size;
      this.order = _order;
      bounds = new Rect();
   }

   public MapItem() {
      this(1, 0);
   }

   public int getDepth() {
      return depth;
   }

   public void setDepth(final int _depth) {
      this.depth = _depth;
   }

   public double getSize() {
      return size;
   }

   public void setSize(final double _size) {
      this.size = _size;
   }

   public Rect getBounds() {
      return bounds;
   }

   /**
    * Set the items bounds.
    */
   public synchronized void setBounds(
      final double x, final double y, final double w, final double h) {

      if (bounds == null) {
         bounds = new Rect();
      }

      bounds.setRect(x, y, w, h);
   }

   /**
    * Set the items bounds.
    */
   public void setBounds(final Rect _bounds) {
      this.bounds = _bounds;
   }

   /**
    * Get the items order.
    */
   public int getOrder() {
      return order;
   }

   /**
    * Set the items order.
    */
   public void setOrder(final int _order) {
      this.order = _order;
   }
}
