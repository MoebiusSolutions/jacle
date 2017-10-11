package jacle.common.lang;

import java.lang.reflect.Array;
import java.util.Arrays;

import javax.annotation.Nonnull;

/**
 * By apply this interface to {@link Enum} classes, you enable lookup of enum
 * values by an alternative name (via {@link #get(Class, String)}). This is
 * generally useful when interacting with enums with string values that don't
 * follow the all-caps-and-underscores convention of java.
 */
public interface HasEnumAltName {

	/**
	 * Defines an alternative name for the enum value. This must be unique
	 * across all values of a single enum class. May not be <code>null</code>.
	 */
	@Nonnull
	String getAltName();
	
	/**
	 * Retrieves enum value by alternative name.
	 * 
	 * @throws IllegalArgumentException
	 *             If the alt-name does not map to an enum value.
	 * @throws IllegalArgumentException
	 *             If the provided enum class maps any alt-name to more than one
	 *             enum value.
	 * @throws IllegalArgumentException
	 *             If the provided enum class does not the
	 *             {@link HasEnumAltName} interface.
	 */
	@Nonnull
	public static <T extends Enum<T>> T get(Class<T> enumType, String altName) {
		return AltNameEnums.get(enumType, altName);
	}

	/**
	 * Retrieves the enum values for an array of alternative names.
	 * 
	 * @throws IllegalArgumentException
	 *             If any alt-name does not map to an enum value.
	 * @throws IllegalArgumentException
	 *             If the provided enum class maps any alt-name to more than one
	 *             enum value.
	 * @throws IllegalArgumentException
	 *             If the provided enum class does not the
	 *             {@link HasEnumAltName} interface.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T[] getArray(Class<T> enumType, String[] altNames) {
		return Arrays.stream(altNames).map((name) -> get(enumType, name)).toArray(size -> (T[]) Array.newInstance(enumType, size));
	}
}
