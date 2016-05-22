package jacle.incubator.exec;

import static org.junit.Assert.assertEquals;
import jacle.common.exec.JavaArgsBuilder;
import jacle.incubator.exec.ProcessLauncher.Result;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

public class ProcessLauncherTest {

    /**
     * Verifies that stdout is collected in the {@link Result} when calling
     * {@link ProcessLauncher#runToCompletion(ProcessBuilder)}.
     */
    @Test
    public void testRunToCompletion_Stdout() throws Exception {
        // Setup
        String[] args = new JavaArgsBuilder(DemoProcess.class).setArgs("echo-stdout", "blah blah blah").build();
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        
        // Execute
        ProcessLauncher launcher = new ProcessLauncher();
        Result result = launcher.runToCompletion(processBuilder);
        
        // Verify
        assertEquals(0, result.getExitCode());
        assertEquals("echo-stdout: blah blah blah", new String(result.getStdout()));
        assertEquals("", new String(result.getStderr()));
    }

    /**
     * Verifies that stderr is collected in the {@link Result} when calling
     * {@link ProcessLauncher#runToCompletion(ProcessBuilder)}.
     */
    @Test
    public void testRunToCompletion_Stderr() throws Exception {
        // Setup
        String[] args = new JavaArgsBuilder(DemoProcess.class).setArgs("echo-stderr", "blah blah blah").build();
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        
        // Execute
        ProcessLauncher launcher = new ProcessLauncher();
        Result result = launcher.runToCompletion(processBuilder);
        
        // Verify
        assertEquals(0, result.getExitCode());
        assertEquals("", new String(result.getStdout()));
        assertEquals("echo-stderr: blah blah blah", new String(result.getStderr()));
    }

    /**
     * Verifies that the exit code is collected in the {@link Result} when
     * calling {@link ProcessLauncher#runToCompletion(ProcessBuilder)}.
     */
    @Test
    public void testRunToCompletion_ExitCode0() throws Exception {
        // Setup
        String[] args = new JavaArgsBuilder(DemoProcess.class).setArgs("exit-with", "0").build();
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        
        // Execute
        ProcessLauncher launcher = new ProcessLauncher();
        Result result = launcher.runToCompletion(processBuilder);
        
        // Verify
        assertEquals(0, result.getExitCode());
    }

    /**
     * For {@link ProcessLauncher#runToCompletion(ProcessBuilder)}, verifies
     * that a non-zero exit code results in a {@link ProcessLauncherException},
     * which contains the exit code and the outputs of both streams.
     */
    @Test
    public void testRunToCompletion_ExitCode88() throws Exception {
        // Setup
        String[] args = new JavaArgsBuilder(DemoProcess.class).setArgs("exit-with", "88").build();
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        
        // Execute
        ProcessLauncher launcher = new ProcessLauncher();
        
        // Verify
        try {
            launcher.runToCompletion(processBuilder);
            Assert.fail("Expected exception");
        } catch (ProcessLauncherException e) {
            assertEquals(88, e.getExitCode());
            assertEquals("(stdout) exit-with: 88", new String(e.getStdout()));
            assertEquals("(stderr) exit-with: 88", new String(e.getStderr()));
        }
    }

    /**
     * For {@link ProcessLauncher#runToCompletion(ProcessBuilder)}, with
     * {@link ProcessLauncher#setThrowExceptionOnExit(boolean)} set to
     * <code>true</code>, verifies that a non-zero exit code results in a
     * {@link ProcessLauncherException}, which contains the exit code and the
     * outputs of both streams.
     */
    @Test
    public void testRunToCompletion_setThrowExceptionOnExit_True() throws Exception {
        // Setup
        String[] args = new JavaArgsBuilder(DemoProcess.class).setArgs("exit-with", "88").build();
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        
        // Execute
        ProcessLauncher launcher = new ProcessLauncher();
        launcher.setThrowExceptionOnExit(true);
        
        // Verify
        try {
            launcher.runToCompletion(processBuilder);
            Assert.fail("Expected exception");
        } catch (ProcessLauncherException e) {
            assertEquals(88, e.getExitCode());
            assertEquals("(stdout) exit-with: 88", new String(e.getStdout()));
            assertEquals("(stderr) exit-with: 88", new String(e.getStderr()));
        }
    }

    /**
     * For {@link ProcessLauncher#runToCompletion(ProcessBuilder)}, with
     * {@link ProcessLauncher#setThrowExceptionOnExit(boolean)} set to
     * <code>false</code>, verifies that a non-zero exit code results in no
     * exception, and that the returned {@link Result} contains the exit code
     * and the outputs of both streams.
     */
    @Test
    public void testRunToCompletion_setThrowExceptionOnExit_False() throws Exception {
        // Setup
        String[] args = new JavaArgsBuilder(DemoProcess.class).setArgs("exit-with", "88").build();
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        
        // Execute
        ProcessLauncher launcher = new ProcessLauncher();
        launcher.setThrowExceptionOnExit(false);
        Result result = launcher.runToCompletion(processBuilder);
        
        // Verify
        assertEquals(88, result.getExitCode());
        assertEquals("(stdout) exit-with: 88", new String(result.getStdout()));
        assertEquals("(stderr) exit-with: 88", new String(result.getStderr()));
    }

    /**
	 * For {@link ProcessLauncher#runToCompletion(ProcessBuilder)}, with
	 * {@link ProcessLauncher#setStdin(byte[])} set, verifies the bytes are
	 * provided to the child process via stdin.
	 */
    @Test
    public void testRunToCompletion_setStdinBytes() throws Exception {
        // Setup
        String[] args = new JavaArgsBuilder(DemoProcess.class).setArgs("echo-stdin").build();
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        
        // Execute
        ProcessLauncher launcher = new ProcessLauncher();
        launcher.setStdin("first line\nsecondline\nthird line".getBytes(StandardCharsets.UTF_8));
        Result result = launcher.runToCompletion(processBuilder);
        
        // Verify
        assertEquals(0, result.getExitCode());
        assertEquals("echo-stdin: first line\nsecondline\nthird line", new String(result.getStdout()));
    }
}
