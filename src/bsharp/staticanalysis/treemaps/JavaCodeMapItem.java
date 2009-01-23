package bsharp.staticanalysis.treemaps;

import java.util.Vector;

/**
 * A java code treemap item.
 * @author brandon_sharp@tvworks.com
 *
 */
public class JavaCodeMapItem extends MapItemExtended {

   /** Denotes a java method. */
   public static final int TYPE_METHOD = 0;
   /** Denotes a java class. */
   public static final int TYPE_CLASS  = 1;
   /** Denotes a java package. */
   public static final int TYPE_PACKAGE  = 2;

   /** Name or id of this item (Jni signature). */
   public final String jniSignature;

   /** The java code package name. */
   public final String packageName;

   /** The java code class name. */
   public final String className;

   /** The java code method name. */
   public final String methodName;

   /** Type of java object (package/class/method). */
   public int type;

   private Vector childrenList = new Vector();

   /**
    * A Map item that has NOT had its bounds calculated.
    * @param jniSignature
    * @param size
    * @param type
    * @param innerMargin
    */
   public JavaCodeMapItem(
      final String pkgName, final String clssName, final String mthdName,
      final double _size, final int _type, final int _innerMargin) {

      //this is not set, we override getName() to return jniSignature.
      super("OVERRIDDEN", _size, _innerMargin);

      packageName = pkgName;
      className = clssName;
      methodName = mthdName;

      jniSignature = createJniSignature(".");
      this.type = _type;
   }

   /**
    * Overrides MapItemExtended.getName().
    */
   public String getName() {
      return jniSignature;
   }

   /**
    * Create a new JavaCodeMapItem that is only for searching purposes.
    * @param searchJniSignature the name to search for.
    * @return a new JavaCodeMapItem that is only for searching purposes.
    */
   public static JavaCodeMapItem searchObject( final String searchJniSignature ) {
      return new JavaCodeMapItem(searchJniSignature);
   }

   /**
    * For equals or searching.
    * @param jniSignature
    */
   private JavaCodeMapItem( final String searchJniSignature ) {
      super(searchJniSignature);
      jniSignature = searchJniSignature;
      packageName = "";
      className = "";
      methodName = "";
   }

   /**
    * Create a jni signature separating the parts with custom delimiter.
    * @param separator a custom delimiter.
    * @return a jni signature separating the parts with custom delimiter.
    */
   String createJniSignature( final String separator ) {
      final StringBuffer sb = new StringBuffer(packageName);

      if ( !className.equals("") ) {

         sb.append(separator).append(className);

         if ( !methodName.equals("")) {
            sb.append(separator).append(methodName);
         }

      }
      return sb.toString();
   }

   /**
    * Add a child to this item.
    * @param newChild the item to add.
    */
   public void addChild( final JavaCodeMapItem newChild ) {
      childrenList.add(newChild);
   }

   /**
    * Get the child item of this item.
    * @return
    */
   public JavaCodeMapItem[] getChildren() {
      return (JavaCodeMapItem[])
         childrenList.toArray(new JavaCodeMapItem[childrenList.size()]);
   }

   /**
    * Get the child of the given name.
    * @param childName the name of the child to return.
    * @return the child of the given name.
    */
   public JavaCodeMapItem getChild( final String childName ) {
      final int cIndex = childrenList.indexOf( new JavaCodeMapItem(childName));
      final JavaCodeMapItem child = (JavaCodeMapItem)childrenList.elementAt(cIndex);
      return child;
   }

}
