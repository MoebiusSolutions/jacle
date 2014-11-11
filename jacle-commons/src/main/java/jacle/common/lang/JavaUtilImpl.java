package jacle.common.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;

/**
 * Default implementation of {@link JavaUtil}
 * 
 * @author rkenney
 */
public class JavaUtilImpl implements JavaUtil {

	private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("(([a-z][a-z0-9_]*\\.)*)([A-Z][a-zA-Z0-9\\$_]*)");

	@Override
	public String getClassName() {
		return Thread.currentThread().getStackTrace()[2].getClassName();
	}

	@Override
	public String getClassName(int nestedDepth) {
		return Thread.currentThread().getStackTrace()[2+nestedDepth].getClassName();
	}

	@Override
	public String getSimpleClassName() {
		return getSimpleClassName(getClassName(1));
	}

	@Override
	public String getSimpleClassName(int nestedDepth) {
		return getSimpleClassName(getClassName(1+nestedDepth));
	}

	@Override
	public String getSimpleClassName(String fullyQualifiedName) {
		Matcher matcher = CLASS_NAME_PATTERN.matcher(fullyQualifiedName);
		if (!matcher.matches()) {
			return fullyQualifiedName;
		}
		return matcher.group(3);
	}

	@Override
	public String getMethodName() {
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}

	@Override
	public String getMethodName(int nestedDepth) {
		return Thread.currentThread().getStackTrace()[2+nestedDepth].getMethodName();
	}

	@Override
	public String getPackageName() {
		return getPackageName(Thread.currentThread().getStackTrace()[2].getClassName());
	}

	@Override
	public String getPackageName(int nestedDepth) {
		return getPackageName(Thread.currentThread().getStackTrace()[2+nestedDepth].getClassName());
	}

	@Override
	public String getPackageName(String fullyQualifiedName) {
		Matcher matcher = CLASS_NAME_PATTERN.matcher(fullyQualifiedName);
		if (!matcher.matches()) {
			return fullyQualifiedName;
		}
		String packag = matcher.group(1);
		return CharMatcher.is('.').trimTrailingFrom(packag);
	}

	@Override
	public String getSimpleFullName() {
		return getSimpleFullName(getSimpleClassName(1), getMethodName(1));
	}

	@Override
	public String getSimpleFullName(int nestedDepth) {
		return getSimpleFullName(getSimpleClassName(nestedDepth+1), getMethodName(nestedDepth+1));
	}
	
	private String getSimpleFullName(String className, String methodName) {
		return String.format("%s.%s()", className, methodName);
	}
}
