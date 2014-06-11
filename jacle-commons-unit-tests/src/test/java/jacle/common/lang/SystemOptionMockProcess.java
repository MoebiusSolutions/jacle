package jacle.common.lang;

import jacle.common.lang.SystemOption;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import com.google.common.io.Files;

public class SystemOptionMockProcess {

	public static void main(String[] args) throws Exception {
		String key = args[0];
		String defaultValue = args[1];
		System.out.print(key+":"+SystemOption.I.getString(key, defaultValue));
	}

	public static String readValueOutput(File file, String key) throws IOException {
		Pattern pattern = Pattern.compile("([^:]*):([^:]*)");
		Matcher matcher = pattern.matcher(Files.toString(file, StandardCharsets.UTF_8));
		Assert.assertTrue(matcher.matches());
		Assert.assertEquals(key, matcher.group(1));
		return matcher.group(2);
	}
}
