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
	 * Returns the name of the class <code>nestedDepth</code> callstack entries
	 * below the caller of this method, effectively walking up stack
	 * <code>nestedDepth</code> entries beyond what {@link #getClassName()}
	 * returns.
	 * 
	 * @param nestedDepth
	 *            The number of trace elements to walk below from what
	 *            {@link #getClassName()} would return
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
}
