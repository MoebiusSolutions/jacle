package jacle.common.exec;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Strings;

/**
 * Builds the command line necessary to launch java processes using
 * approximately the same classpath as the active classloader
 * 
 * @author rkenney
 */
public class JavaArgsBuilder {

	private String mainClass;
	private String[] args = {};
	private Integer initialJvmMB;
	private Integer maxJvmMB;
	private boolean doJvmDebug;
	private boolean doJvmDebugSuspend;
	private Integer jvmDebugPort;
	/**
	 * A map of java system properties to apply to the command line. Entries
	 * with a null value will be added as "-Dkey"; entries with an empty string
	 * value will be added as "-Dkey="; and entries with a full value will be
	 * added as "-Dkey=value".
	 */
	private Map<String, String> systemProperties = new HashMap<String, String>();
	private boolean doFullyQualify;

	/**
	 * Used to build a the command line necessary to execute a java process
	 * using the classpaths of the current classloader from the specified main()
	 * class.
	 */
	public JavaArgsBuilder(Class<?> mainClass) {
		this.mainClass = mainClass.getName();
	}

	/**
	 * Used to build a the command line necessary to execute a java process
	 * using the classpaths of the current classloader from the specified main()
	 * class.
	 */
	public JavaArgsBuilder(String mainClass) {
		this.mainClass = mainClass;
	}

	/**
	 * Specifies the arguments to the main of the java process
	 */
	public JavaArgsBuilder setArgs(String... args) {
		this.args = args;
		return this;
	}

	/**
	 * Specifies a java system property (e.g. adds -D parameter to the jvm)
	 */
	public JavaArgsBuilder addJavaProperty(String name) {
		this.systemProperties.put(name, null);
		return this;
	}

	/**
	 * Specifies a java system property (e.g. adds -D parameter to the jvm)
	 */
	public JavaArgsBuilder addJavaProperty(String name, String value) {
		this.systemProperties.put(name, value);
		return this;
	}

	/**
	 * Sets the memory arguments of the JVM 
	 */
	public JavaArgsBuilder setMemory(int initialJvmMB, int maxJvmMB) {
		this.initialJvmMB = initialJvmMB;
		this.maxJvmMB = maxJvmMB;
		return this;
	}

	/**
	 * Enables remote debugging of the JVM. The listen port is automatically
	 * assigned by the JVM.
	 * 
	 * @param doSuspend
	 *            Whether or not the debugging system should halt the process
	 *            until a debug connection is established
	 */
	public JavaArgsBuilder setEnableJvmDebug(boolean doSuspend) {
		this.doJvmDebug = true;
		this.doJvmDebugSuspend = doSuspend;
		return this;
	}

	/**
	 * Enables remote debugging of the JVM.
	 * 
	 * @param doSuspend
	 *            Whether or not the debugging system should halt the process
	 *            until a debug connection is established
	 * @param listenPort
	 *            The port to listen for debug connections on
	 */
	public JavaArgsBuilder setEnableJvmDebug(boolean doSuspend, int listenPort) {
		this.doJvmDebug = true;
		this.doJvmDebugSuspend = doSuspend;
		this.jvmDebugPort = listenPort;
		return this;
	}

	/**
	 * Specifies Whether the builder should apped a fully qualified path to the
	 * java executable if available (from the JAVA_HOME env variable). Defaults
	 * to false.
	 */
	public JavaArgsBuilder setFullyQualifiedPath(boolean doFullyQualify) {
		this.doFullyQualify = doFullyQualify;
		return this;
	}

	/**
	 * Returns the command line
	 */
	public String[] build() {
		LinkedList<String> cmd = new LinkedList<String>();
		if (doFullyQualify) {
			cmd.add(getFullyQualifiedExectuable());
		} else {
			cmd.add("java");
		}
		if (this.initialJvmMB != null) {
			cmd.add("-Xms"+this.initialJvmMB+"m");
		}
		if (this.maxJvmMB != null) {
			cmd.add("-Xmx"+this.initialJvmMB+"m");
		}
		if (this.doJvmDebug) {
			cmd.add("-Xdebug");
			StringBuilder arg = new StringBuilder("-Xrunjdwp:transport=dt_socket,server=y"); 
			if (this.doJvmDebugSuspend) {
				arg.append(",suspend=y");
			} else {
				arg.append(",suspend=n");
			}
			if (this.jvmDebugPort != null) {
				arg.append(",address="+this.jvmDebugPort);
			}
			cmd.add(arg.toString());
		}
		for (Entry<String, String> property : this.systemProperties.entrySet()) {
			if (property.getValue() == null) {
				cmd.add("-D"+property.getKey());
			} else {
				cmd.add("-D"+property.getKey()+"="+property.getValue());
			}
		}
		cmd.add("-classpath");
		cmd.add(getCurrentClasspath());
		cmd.add(this.mainClass);
		for (String arg : args) {
			cmd.add(arg);
		}
		return cmd.toArray(new String[cmd.size()]);
	}

	private static String getCurrentClasspath() {
		StringBuilder classpath = new StringBuilder ();
		for (URL url : ((URLClassLoader) (Thread.currentThread() .getContextClassLoader())).getURLs()) {
		  classpath.append(new File(url.getPath().replaceAll("\\%20", " ")));
		  classpath.append(System.getProperty("path.separator"));
		}
		return classpath.toString();
	}
	
	private String getFullyQualifiedExectuable() {
		String javaHome = System.getenv("JAVA_HOME");
		if (Strings.isNullOrEmpty(javaHome)) {
			return "java";
		}
		return new File(javaHome, "bin/java").toString();
	}
}
