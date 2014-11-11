package jacle.common.lang;

import java.io.File;

/**
 * Provides very general Java utility methods</p>
 * 
 * The default implementation is available as {@link #I}</p>
 * 
 * @author rkenney
 */
public interface JavaUtil {

	/**
	 * Static accessor to default implementation
	 */
	public static final JavaUtil I = new JavaUtilImpl();
	
	/**
	 * Returns the full qualified class name of the caller. E.g. "java.io.File". The big benefit of
	 * this method is that it can be used even from static contexts.
	 */
	public String getClassName();

	/**
	 * Calls {@link #getClassName()} <code>nestedDepth</code> entries
	 * lower in the callstack.
	 * 
	 * @param nestedDepth
	 *            The number of additional call stack elements to walk down
	 */
	public String getClassName(int nestedDepth);

	/**
	 * Returns the "simple name" (without package prefix) for the class
	 * containing the method that invokes this method. This is similar to what
	 * is returned by {@link Class#getSimpleName()}. The big benefit of
	 * this method is that it can be used even from static contexts.</p>
	 */
	public String getSimpleClassName();

	/**
	 * Calls {@link #getSimpleClassName()} <code>nestedDepth</code> entries
	 * lower in the callstack.
	 * 
	 * @param nestedDepth
	 *            The number of additional call stack elements to walk down
	 */
	public String getSimpleClassName(int nestedDepth);

	/**
	 * Returns a "simple name" (without package prefix) version of a class name
	 * from fully qualified one. This is similar to what is returend by
	 * {@link Class#getSimpleName()}.</p>
	 * 
	 * If the provided string does not match the format of a fully qualified
	 * class name, the original string is returned.</p>
	 */
	public String getSimpleClassName(String fullyQualifiedName);

	/**
	 * Returns the name of the method name of the caller as reported by a stack
	 * trace. E.g. if called from within {@link File#getPath()}, this would
	 * return "getPath". The big benefit of this method is that it can be used
	 * even from static contexts.
	 */
	public String getMethodName();

	/**
	 * Calls {@link #getMethodName()} <code>nestedDepth</code> entries
	 * lower in the callstack.
	 * 
	 * @param nestedDepth
	 *            The number of additional call stack elements to walk down
	 */
	public String getMethodName(int nestedDepth);

	/**
	 * Returns the package name of the calling method.</p>
	 */
	public String getPackageName();

	/**
	 * Calls {@link #getPackageName()} <code>nestedDepth</code> entries
	 * lower in the callstack.
	 * 
	 * @param nestedDepth
	 *            The number of additional call stack elements to walk down
	 */
	public String getPackageName(int nestedDepth);

	/**
	 * Returns the package name parsed from the provided fully qualified class
	 * name.</p>
	 * 
	 * If the provided string does not match the format of a fully qualified
	 * class name, the original string is returned.</p>
	 */
	public String getPackageName(String fullyQualifiedName);

	/**
	 * Returns the name of the calling method in the form
	 * "ClassName.methodName()". The arguments are always empty, so overloaded
	 * methods will have identical signatures. In the case of nested and inner
	 * classes, the returned string looks like
	 * "ClassName$NestedClass.methodName()" or "ClassName$4.methodName()".
	 */
	public String getSimpleFullName();

	/**
	 * Calls {@link #getSimpleFullName()} <code>nestedDepth</code> entries
	 * lower in the callstack.
	 * 
	 * @param nestedDepth
	 *            The number of additional call stack elements to walk down
	 */
	public String getSimpleFullName(int nestedDepth);
}
