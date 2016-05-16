package jacle.common.exec;

import jacle.common.io.CloseablesExt;

import java.io.ByteArrayOutputStream;

import org.apache.commons.io.IOUtils;

public class DemoProcess {

    public static void main(String[] args) throws Exception {
        switch (args[0]) {
        case "echo-args":
            for (int i=1; i<args.length; i++) {
                System.out.printf("[%s]: %s%n", i, args[i]);
            }
            break;
        case "echo-stdout":
            System.out.printf("echo-stdout: %s", args[1]);
            break;
        case "echo-stderr":
            System.err.printf("echo-stderr: %s", args[1]);
            break;
        case "echo-stdin":
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try {
                System.out.printf("echo-stdin: ", buffer.toString());
                IOUtils.copy(System.in, System.out);
            } finally {
                CloseablesExt.closeQuietly(buffer);
            }
            break;
        case "exit-with":
            System.out.printf("(stdout) exit-with: %s", args[1]);
            System.err.printf("(stderr) exit-with: %s", args[1]);
            System.exit(Integer.parseInt(args[1]));
            break;
        default:
            throw new RuntimeException("Missing arguments");
        }
    }
}
