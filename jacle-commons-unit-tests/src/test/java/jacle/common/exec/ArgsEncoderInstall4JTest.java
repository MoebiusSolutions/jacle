package jacle.common.exec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import argvquote.EchoArgs;
import jacle.common.exec.ArgsEncoderTest.ArgsProcessExecutor;
import jacle.common.exec.ArgsEncoderTest.ValueEncoder;
import jacle.common.exec.ProcessLauncher.Result;
import jacle.common.io.RuntimeIOException;

/**
 * This class simulates the way that Install4j launches child process and
 * demonstrates that the existing {@link ArgsEncoder} will not solve the
 * problem. (In truth, since Install4j uses a batch script calling another
 * command, I don't think it's even possible to encode all possible argument
 * values.)
 */
public class ArgsEncoderInstall4JTest {
	
	private ArgsEncoderTest encoderTest;

	@Before
	public void setUp() throws Exception {
		this.encoderTest = new ArgsEncoderTest();
		encoderTest.setUp();
	}

	@Ignore // This doesn't work. It's just included for demonstration purposes.
    @Test
    public void testEncodeForInstall4j() throws Exception {
        encoderTest.ensureWindows();
        
        ValueEncoder encoder = new ValueEncoder() { public String encode(String value) {
        	// This doesn't do the job
            value = ArgsEncoder.I.encodeWinJavaToJava(value);
            return value;
        }};

        ArgsProcessExecutor executor = new ArgsProcessExecutor() {
            @Override
            public String run(String[] args) {
                
                // Build the target java process command that will be receiving/echoing our arguments 
                String[] javaEchoCommand = new JavaArgsBuilder(EchoArgs.class).setArgs(args).build();
                
                // Separate executable from other args
                // (This is how install4j recieves the values)
                String javaEchoExe = javaEchoCommand[0];
                LinkedList<String> javaEchoParams = new LinkedList<String>(Arrays.asList(javaEchoCommand));
                javaEchoParams.removeFirst();
                
                // Build a batch script and cmd.exe process between our target process
                // and the current process in the same manner as Install4j
                File script = ArgsEncoderTest.FILES.getFile("test.bat");
                ProcessBuilder builder;
                { // ... begin LaunchHelper.launchOnWindows() 
                    ArrayList<String> cmd = new ArrayList<>();
                    cmd.add("cmd.exe");
                    cmd.add("/c");
                    
                    { // ... begin LaunchHelper.createWindowsStartScript()
                        PrintWriter pw; 
                        try {
                            pw = new PrintWriter(new FileOutputStream(script));
                        } catch (IOException e) {
                            throw new RuntimeIOException(e);
                        }
                        pw.println("@ECHO OFF");
                        pw.print("\"" + javaEchoExe + "\"");
                        for (String arg : javaEchoParams) {
                            arg = arg.replaceAll("%", "%%");
                            if (needsQuotes(arg, false)) {
                                pw.print(" \"" + arg + "\"");
                            } else {
                                pw.print(" " + arg);
                            }
                        }
                        pw.println();
                        pw.close();
                    } // ... end LaunchHelper.createWindowsStartScript()
                    cmd.add(script.getAbsolutePath());
                    { // ... begin LaunchHelper.createProcessBuilder()
                        builder = new ProcessBuilder(cmd).directory(script.getParentFile() /* don't care */);
                    } // ... end LaunchHelper.createProcessBuilder()
                } // ... end LaunchHelper.launchOnWindows()
                
                // Execute the top level cmd using our own mechanism
                Result result = ArgsEncoderTest.newProcessLauncher().
                		setEchoOutput(ArgsEncoderTest.SHOW_DEBUG).
                		runToCompletion(builder);
                return new String(result.getStdout());
            }
        };

        encoderTest.doTestArgs(encoder, executor);
    }

    // An approximation of Install4J's LaunchHelper.needsQuotes()
    private static boolean needsQuotes(String argument, boolean isExe) {
        final char[] SPECIAL_BATCH_FILE_CHARACTERS = { ' ', '&', '(', ')', '[', ']', '{', '}', '^', '=', ';', '!', '+', '`', '�', '~', '<', '>', '@', '|' };
        final char[] SPECIAL_ARGUMENTS_CHARACTERS = { ' ', '&', '^', '`', '�', '<', '>', '@', '|' };
        if (argument.startsWith("\"")) {
            return false;
        }
        int charCount = argument.length();
        for (int i = 0; i < charCount; i++) {
            char c = argument.charAt(i);
            if ((c >= ' ') && (c <= 'z')) {
                // OH JEEEZ. Looks like they may have a bug here. It doesn't do anything if special characters are detected!
                if (Arrays.binarySearch(isExe ? SPECIAL_ARGUMENTS_CHARACTERS
                        : SPECIAL_BATCH_FILE_CHARACTERS, c) < 0) {}
                // Here's maybe what they intended...
//                else {
//                    return true;
//                }
            } else {
                return true;
            }
        }
        return false;
    }

}
