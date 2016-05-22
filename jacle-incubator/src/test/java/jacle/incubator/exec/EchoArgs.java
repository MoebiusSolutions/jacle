package jacle.incubator.exec;

public class EchoArgs {

    public static void main(String[] args) throws Exception {
        for (int i=0; i<args.length; i++) {
            System.out.printf("[%s]: %s%n", i+1, args[i]);
        }
    }
}
