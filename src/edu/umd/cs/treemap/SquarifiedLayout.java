/**
 * Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA
 * and Martin Wattenberg, w@bewitched.com
 * All rights reserved.
 * Authors: Benjamin B. Bederson and Martin Wattenberg
 * http://www.cs.umd.edu/hcil/treemaps
 */

package edu.umd.cs.treemap;

/**
 * "Squarified" treemap layout invented by J.J. van Wijk.
 */
public class SquarifiedLayout extends AbstractMapLayout {
   public void layout(final Mappable[] items, final Rect bounds) {

      layout(sortDescending(items), 0, items.length - 1, bounds);
   }

   public void layout(final Mappable[] items, final int start, final int end, final Rect bounds) {

      if (layoutSkip(items, start, end, bounds)) {
         return;
      }

      final double x = bounds.mX;
      final double y = bounds.mY;
      final double w = bounds.mW;
      final double h = bounds.mH;

      final double total = sum(items, start, end);
      final int mid = start;
      final double a = items[start].getSize() / total;
      final double b = a;

      if (w < h) {
         layoutHeightWidth(items, start, end, x, y, w, h, total, mid, a, b);
      } else {
         layoutWidthHeight(items, start, end, x, y, w, h, total, mid, a, b);
      }

   }

   private boolean layoutSkip(
      final Mappable[] items, final int start, final int end, final Rect bounds) {

      if (start > end) {
         return true;
      } else if (end - start < 2) {
         SliceLayout.layoutBest(items, start, end, bounds);
         return true;
      }
      return false;
   }

   private void layoutHeightWidth(final Mappable[] items, final int start, final int end,
      final double x, final double y, final double w, final double h, final double total, int mid,
      final double a, double b) {
      // height/width
      while (mid <= end) {
         final double aspect = normAspect(h, w, a, b);
         final double q = items[mid].getSize() / total;
         if (normAspect(h, w, a, b + q) > aspect) {
            break;
         }
         mid++;
         b += q;
      }
      // System.out.println("DEBUG:
      // SquarifiedLayout.layout:line51:start:"+start+" mid:"+mid);
      SliceLayout.layoutBest(items, start, mid, new Rect(x, y, w, h * b));
      layout(items, mid + 1, end, new Rect(x, y + h * b, w, h * (1 - b)));
   }

   private void layoutWidthHeight(final Mappable[] items, final int start, final int end,
      final double x, final double y, final double w, final double h, final double total, int mid,
      final double a, double b) {
      // width/height
      while (mid <= end) {
         final double aspect = normAspect(w, h, a, b);
         final double q = items[mid].getSize() / total;
         if (normAspect(w, h, a, b + q) > aspect) {
            break;
         }
         mid++;
         b += q;
      }
      SliceLayout.layoutBest(items, start, mid, new Rect(x, y, w * b, h));
      layout(items, mid + 1, end, new Rect(x + w * b, y, w * (1 - b), h));
   }

   private double aspect(final double big, final double small, final double a, final double b) {
      return (big * b) / (small * a / b);
   }

   private double normAspect(final double big, final double small, final double a, final double b) {
      final double x = aspect(big, small, a, b);
      if (x < 1) {
         return 1 / x;
      }
      return x;
   }

   private double sum(final Mappable[] items, final int start, final int end) {
      double s = 0;
      for (int i = start; i <= end; i++) {
         s += items[i].getSize();
      }
      return s;
   }

   public String getName() {
      return "Squarified";
   }

   public String getDescription() {
      return "Algorithm used by J.J. van Wijk.";
   }

}
