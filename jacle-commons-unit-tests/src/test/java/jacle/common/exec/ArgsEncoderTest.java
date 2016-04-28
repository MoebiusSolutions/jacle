package jacle.common.exec;

import jacle.common.exec.ProcessLauncher.Result;
import jacle.common.io.FilesExt;
import jacle.common.lang.JavaUtil;
import jacle.commontest.JUnitFiles;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Joiner;

public class ArgsEncoderTest {

    static final JUnitFiles FILES = new JUnitFiles(JavaUtil.I.getClassName());
    private static final String NL = System.lineSeparator();

    /**
     * Enable this during debugging for more output 
     */
    static final boolean SHOW_DEBUG = false;

    private Boolean isWindows;

    @Before
    public void setUp() throws Exception {
        FILES.before();
    }

    /**
     * Verifies that arguments encoded by
     * {@link ArgsEncoder#encodeWinJavaToJava(String)} are properly read by a
     * child java process when including a wide variety of special characters.
     */
    @Test
    public void testEncodeWinJavaToJava() throws Exception {
        ensureWindows();
        
        // Use ArgsEncoder.I.encodeWinJavaToJava(value) to encode arguments
        ValueEncoder encoder = new ValueEncoder() { public String encode(String value) {
            return ArgsEncoder.I.encodeWinJavaToJava(value);
        }};
        
        // Launch EchoArgs.java as a child process, with the given args
        ArgsProcessExecutor executor = new ArgsProcessExecutor() {
            @Override
            public String run(String[] args) {
                JavaArgsBuilder javaArgs = new JavaArgsBuilder(EchoArgs.class);
                javaArgs.setArgs(args);
                Result result = newProcessLauncher().setEchoOutput(SHOW_DEBUG).runToCompletion(
                        new ProcessBuilder(javaArgs.build()));
                return new String(result.getStdout());
            }
        };

        // Launch the test
        doTestArgs(encoder, executor);
    }
    
    /**
	 * Attempts to verify that we encode arguments before writing a batch script
	 * that will call another batch script. So far, a solution has been elusive.
	 * Use this test method for any future experimentation.
	 */
    @Ignore // No solution yet
    @Test
    public void testEncodeWinBatchToBatch() throws Exception {

        ValueEncoder encoder = new ValueEncoder() { public String encode(String value) {
            
            // So far, I can't figure out a consistent escaping rule set when passing args from a batch script.
            // Issues include:
            // * I see no consistent way to escape double-quotes
            // * I see no way to escape a trailing backslash
            // * I see no way to escape % if it's inside a quoted argument
            // A starting point for these rules is here (but only a starting point): http://ss64.com/nt/syntax-esc.html
            
            return value;
        }};

        ArgsProcessExecutor executor = new ArgsProcessExecutor() {
            @Override
            public String run(String[] args) {
                // Write a batch script that calls another batch script with the given args
                File echoScript = FILES.getFile("echo.bat");
                File argsScript = FILES.getFile("test.bat");
                writeEchoArgsScript(echoScript, args.length);
                writeEchoArgsScriptCaller(argsScript, echoScript, args);
                return runScript(argsScript);
            }
        };
        
        doTestArgs(encoder, executor);
    }
    
    /**
	 * Runs through a series of arguments that should be passed to a child
	 * process and then back by the process. The provided {@link ValueEncoder}
	 * affects the arguments before they are passed to the child process. The
	 * provided {@link ArgsProcessExecutor} is used to execute the child process
	 * and collect it's output for parsing.
	 */
    void doTestArgs(ValueEncoder encoder, ArgsProcessExecutor executor) throws Exception {
        TestCases testCases = new TestCases();
        // Test cases from qntm's post:
        // http://stackoverflow.com/questions/6427732/how-can-i-escape-an-arbitrary-string-for-use-as-a-command-line-argument-in-windo
        testCases.add(new TestCase("yes"));
        testCases.add(new TestCase("no"));
        testCases.add(new TestCase("child.exe"));
        testCases.add(new TestCase("argument 1"));
        testCases.add(new TestCase("Hello, world"));
        testCases.add(new TestCase("Hello\"world"));
        testCases.add(new TestCase("\\some\\path with\\spaces"));
        testCases.add(new TestCase("C:\\Program Files\\"));
        testCases.add(new TestCase("she said, \"you had me at hello\""));
        testCases.add(new TestCase("argument\"2"));
        testCases.add(new TestCase("\\some\\directory with\\spaces\\"));
        testCases.add(new TestCase("\""));
        testCases.add(new TestCase("\\"));
        testCases.add(new TestCase("\\\\"));
        testCases.add(new TestCase("\\\\\\"));
        testCases.add(new TestCase("\\\\\\\\"));
        testCases.add(new TestCase("\\\\\\\\\\"));
        testCases.add(new TestCase("\"\\"));
        testCases.add(new TestCase("\"\\T"));
        testCases.add(new TestCase("\"\\\\T"));
        testCases.add(new TestCase("!1"));
        testCases.add(new TestCase("!A"));
        testCases.add(new TestCase("\"!\\/'\""));
        testCases.add(new TestCase("\"Jeff's!\""));
        testCases.add(new TestCase("$PATH"));
        testCases.add(new TestCase("%PATH%"));
        testCases.add(new TestCase("&"));
        testCases.add(new TestCase("<>|&^"));
        testCases.add(new TestCase("()%!^\"<>&|"));
        testCases.add(new TestCase(">\\\\.\\nul"));
        testCases.add(new TestCase("malicious argument\"&whoami"));
        testCases.add(new TestCase("*@$$A$@#?-_"));
        // Quote-related test cases 
        testCases.add(new TestCase("args", "with single \" quote"));
        testCases.add(new TestCase("args", "with double \"\" quote"));
        testCases.add(new TestCase("args", "with triple \"\"\" quote"));
        testCases.add(new TestCase("args", "with", "isolated", "single", "\"", "quote"));
        testCases.add(new TestCase("args", "with", "isolated", "double", "\"\"\"", "quote"));
        testCases.add(new TestCase("args", "with", "isolated", "triple", "\"\"\"", "quote"));
        testCases.add(new TestCase("args", "unmatched", "trailing single quote\""));
        testCases.add(new TestCase("args", "unmatched", "trailing double quote\"\""));
        testCases.add(new TestCase("args", "unmatched", "trailing triple quote\"\"\""));
        testCases.add(new TestCase("args", "unmatched", "\"leading single quote"));
        testCases.add(new TestCase("args", "unmatched", "\"\"leading double quote"));
        testCases.add(new TestCase("args", "unmatched", "\"\"\"leading triple quote"));
        // Special character related test cases 
        String specialChars = "~`!@#$%^&*()_+-={}[]|\\:;'\"<>,.?/";
        final int MAX_DUPLICATES = 4;
        for (char c : specialChars.toCharArray()) {
            for (int i=0; i<MAX_DUPLICATES; i++) {
                testCases.add(new TestCase("args", "with", ""+(i+1)+" "+concatN(c, (i+1))+" char"));
                testCases.add(new TestCase("args", "with", ""+(i+1), concatN(c, (i+1))+" leading char"));
                testCases.add(new TestCase("args", "with", ""+(i+1), "trailing", "char "+concatN(c, (i+1))));
            }
        }
        
        for (TestCase testCase : testCases.testCases) {
            if (SHOW_DEBUG) {
                System.out.println("RUNNING: ["+Joiner.on("] [").join(testCase.args)+"]");
            }
            String[] encodedArgs = new String[testCase.args.length];
            for (int i = 0; i < testCase.args.length; i++) {
                encodedArgs[i] = encoder.encode(testCase.args[i]);
            }
            String output = executor.run(encodedArgs);
            String[] actualValues = parseArgsOutput(output);
            String name = "["+Joiner.on("] [").join(testCase.args)+"]";
            Assert.assertArrayEquals("Test case failed "+name+": ", testCase.args, actualValues);
        }
    }

    static class TestCases {
        ArrayList<TestCase> testCases = new ArrayList<>();
        
        void add(TestCase testCase) {
            testCases.add(testCase);
        }
    }

    static class TestCase {
        String[] args;
        
        public TestCase(String... args) {
            this.args = args;
        }
    }

    static interface ValueEncoder {
        String encode(String value);
    }

    static interface ArgsProcessExecutor {
        String run(String[] args);
    }

    /**
     * Writes a batch script that echos the provided arguments, one line at a
     * time.
     * 
     * @param file
     *            The file to write the batch script to.
     * @param argCount
     *            The number of arguments to echo.
     */
    private static void writeEchoArgsScript(File file, int argCount) {
        final String NL = System.lineSeparator();
        StringBuilder script = new StringBuilder();
        script.append("@echo off"+NL);
        for (int i = 0;  i < argCount; i++) {
            script.append("echo.["+(i+1)+"]: %~"+(i+1)+""+NL);
        }
        FilesExt.write(script, file, StandardCharsets.UTF_8);
    }

    /**
	 * Writes a batch script that will call another batch script and provide all
	 * of the specified arguments.
	 * 
	 * @param newScript
	 *            The calling script to write
	 * @param echoScript
	 *            The called script (presumed to exist)
	 * @param args
	 *            The args to pass to the called script
	 */
    private static void writeEchoArgsScriptCaller(File newScript, File echoScript, String... args) {
        StringBuilder script = new StringBuilder();
        script.append("@echo off"+NL);
        script.append("call "+echoScript.getAbsolutePath());
        for (String arg : args) {
            script.append(" ").append(arg);
        }
        script.append(NL);
        FilesExt.write(script.toString(), newScript, StandardCharsets.UTF_8);
    }

    private static String runScript(File testScript) {
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", testScript.getName());
        builder.directory(testScript.getParentFile());
        Result result = newProcessLauncher().setEchoOutput(SHOW_DEBUG).runToCompletion(builder);
        return new String(result.getStdout());
    }

    /**
     * Parses/returns the name/value output of arguments from the tty of a child
     * process.
     */
    String[] parseArgsOutput(String output) {
        ArrayList<String> values = new ArrayList<>();
        Pattern OUTPUT_LINE_PATTERN = Pattern.compile("\\[([^\\]]+)\\]: (.+)");
        String[] lines = output.split("\r?\n");
        for (String line : lines) {
            Matcher matcher = OUTPUT_LINE_PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new RuntimeException("Unrecognized line of output: "+line);
            }
            values.add(matcher.group(2));
        }
        return values.toArray(new String[values.size()]);
    }

    /**
     * Concatonates <code>n</code> copies of the provided character into a string
     */
    private static String concatN(char c, int n) {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<n; i++) {
            builder.append(c);
        }
        return builder.toString();
    }

    void ensureWindows() {
        if (isWindows == null) {
            isWindows = System.getProperty("os.name").startsWith("Windows");
        }
        if (!isWindows) {
            throw new RuntimeException("This test will only function on Windows");
        }
    }
    
    /**
	 * Just a wrapper method to avoid the @Deprecated flag on the constructor.
	 */
    @SuppressWarnings("deprecation")
	static ProcessLauncher newProcessLauncher() {
    	return new ProcessLauncher();
    }
}
