package jacle.common.exec;

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
        case "exit-with":
            System.out.printf("(stdout) exit-with: %s", args[1]);
            System.err.printf("(stderr) exit-with: %s", args[1]);
            System.exit(Integer.parseInt(args[1]));
            break;
        }
    }
}
