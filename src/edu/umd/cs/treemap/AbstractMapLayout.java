/**
 * Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA
 * and Martin Wattenberg, w@bewitched.com
 * All rights reserved.
 * Authors: Benjamin B. Bederson and Martin Wattenberg
 * http://www.cs.umd.edu/hcil/treemaps
 */

package edu.umd.cs.treemap;

/**
 *
 * Abstract class holding utility routines that several implementations of
 * MapLayout use.
 *
 */
public abstract class AbstractMapLayout implements MapLayout {
   // Flags for type of rectangle division
   // and sort orders.
   /** Type of rectangle division. */
   public static final int VERTICAL = 0;
   /** Type of rectangle division. */
   public static final int HORIZONTAL = 1;
   /** A sort order. */
   public static final int ASCENDING = 0;
   /** A sort order. */
   public static final int DESCENDING = 1;

   /** Subclasses implement this method themselves. */
   public abstract void layout(Mappable[] items, Rect bounds);

   public void layout(final MapModel model, final Rect bounds) {
      layout(model.getItems(), bounds);
   }

   public static double totalSize(final Mappable[] items) {
      // System.out.println("DEBUG: totalSize(Mappable[] items)
      // items.length:"+items.length);
      return totalSize(items, 0, items.length - 1);
   }

   public static double totalSize(final Mappable[] items, final int start,
      final int end) {
      // System.out.print("DEBUG: totalSize(Mappable[] items, int start, int
      // end) items.length:"+items.length);
      // System.out.print(" start: "+start+" end"+end+"\n");
      double sum = 0;
      for (int i = start; i <= end; i++) {
         if (i != items.length) {
            sum += items[i].getSize();
         }
      }
      return sum;
   }

   // For a production system, use a quicksort...
   public Mappable[] sortDescending(final Mappable[] items) {
      final Mappable[] s = new Mappable[items.length];
      System.arraycopy(items, 0, s, 0, items.length);
      final int n = s.length;
      boolean outOfOrder = true;
      while (outOfOrder) {
         outOfOrder = false;
         for (int i = 0; i < n - 1; i++) {
            final boolean wrong = (s[i].getSize() < s[i + 1].getSize());
            if (wrong) {
               final Mappable temp = s[i];
               s[i] = s[i + 1];
               s[i + 1] = temp;
               outOfOrder = true;
            }
         }
      }
      return s;
   }

   public static void sliceLayout(final Mappable[] items, final int start,
      final int end, final Rect bounds, final int orientation) {
      sliceLayout(items, start, end, bounds, orientation, ASCENDING);
   }

   public static void sliceLayout(final Mappable[] items, final int start,
      int end, final Rect bounds, final int orientation, final int order) {
      end = sliceLayoutEnd(items, end);

      final double total = totalSize(items, start, end);
      double a = 0;
      final boolean vertical = orientation == VERTICAL;

      for (int i = start; i <= end; i++) {
         Rect r = new Rect();
         final double b = items[i].getSize() / total;
         if (vertical) {
            r = sliceLayoutVertical(bounds, order, a, b);
         } else {
            if (order == ASCENDING) {
               r.mX = bounds.mX + bounds.mW * a;
            } else {
               r.mX = bounds.mX + bounds.mW * (1 - a - b);
            }
            r.mW = bounds.mW * b;
            r.mY = bounds.mY;
            r.mH = bounds.mH;
         }
         items[i].setBounds(r);
         a += b;
      }
   }

   public static int sliceLayoutEnd(final Mappable[] items, int end) {
      if (end == items.length) {
         end = (items.length == 0) ? 0 : end - 1;
      }
      return end;
   }

   public static Rect sliceLayoutVertical(final Rect bounds, final int order,
      final double a, final double b) {
      final Rect r = new Rect();

      r.mX = bounds.mX;
      r.mW = bounds.mW;
      if (order == ASCENDING) {
         r.mY = bounds.mY + bounds.mH * a;
      } else {
         r.mY = bounds.mY + bounds.mH * (1 - a - b);
      }
      r.mH = bounds.mH * b;

      return r;
   }
}
