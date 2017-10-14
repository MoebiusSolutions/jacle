package jacle.common.lang;

import java.util.Arrays;
import java.util.HashMap;

class AltNameEnums {

	/**
	 * Map of enum types (classes) to their respective maps of alt-names to values. 
	 */
	private static HashMap<
		Class<? extends Enum<?>>,
		HashMap<String,? extends Enum<?>>> INDEX = new HashMap<>();

	/**
	 * Retrieves an enum value by alt-name.
	 * 
	 * @param enumType
	 *            The type of enum to return
	 * @param altName
	 *            The alt-name of the enum value
	 * @return Returns the enum value. Never null.
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
	static <T extends Enum<T>> T get(Class<T> enumType, String altName) {
		if (!HasEnumAltName.class.isAssignableFrom(enumType)) {
			throw new IllegalArgumentException("The enum type must implement "+HasEnumAltName.class.getName());
		}
		INDEX.computeIfAbsent(enumType, (x) -> {
			HashMap<String, Enum<T>> map = new HashMap<>();
			Arrays.asList(enumType.getEnumConstants()).forEach((t) -> {
				String aName = ((HasEnumAltName) ((Object) t)).getAltName();
				if (map.containsKey(aName)) {
					throw new IllegalArgumentException("The enum type has duplicate alt-name entries for ["+aName+"]");
				}
				map.put(aName, t); 
			});
			return map;
		});
		HashMap<String, ? extends Enum<?>> index = INDEX.get(enumType);
		@SuppressWarnings("unchecked")
		T value = (T) index.get(altName);
		if (value == null) {
			throw new IllegalArgumentException("No "+enumType.getName()+" with alt-name of ["+altName+"]");
		}
		return value;
	}
}
