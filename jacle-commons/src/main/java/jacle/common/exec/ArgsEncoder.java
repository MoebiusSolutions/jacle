package jacle.common.exec;


public class ArgsEncoder {

    /**
     * Static accessor
     */
    public static final ArgsEncoder I = new ArgsEncoder();
    
    /**
     * <p>
     * Encodes a set of program arguments for a new process so that the child
     * process receives them without any mangling of special characters. This
     * method applies in this case:
     * </p>
     * 
     * <ul>
     * <li>OS: Windows</li>
     * <li>Args encoded for: Java ({@link ProcessBuilder})</li>
     * <li>Child Process: Java</li>
     * </ul>
     * 
     * <p>
     * Note that the vast majority of child process types (other than Java) will
     * probably work with this, but it's always possible for programs to define
     * their own command line parsing rules.
     * </p>
     */
    public String[] encodeWinJavaToJava(String... args) {
        String[] encodedArgs = new String[args.length]; 
        for (int i=0; i<args.length; i++) {
            encodedArgs[i] = encodeWinJavaToJava(args[i]);
        }
        return encodedArgs;
    }
    
    /**
     * <p>
     * Encodes a set of program arguments for a new process so that the child
     * process receives them without any mangling of special characters. This
     * method applies in this case:
     * </p>
     * 
     * <ul>
     * <li>OS: Windows</li>
     * <li>Args encoded for: Java ({@link ProcessBuilder})</li>
     * <li>Child Process: Java</li>
     * </ul>
     * 
     * <p>
     * Note that the vast majority of child process types (other than Java) will
     * probably work with this, but it's always possible for programs to define
     * their own command line parsing rules.
     * </p>
     */
    public String encodeWinJavaToJava(String arg) {
        // Applying an approximation of the rules in one of qntm's posts in:
        // http://stackoverflow.com/questions/6427732/how-can-i-escape-an-arbitrary-string-for-use-as-a-command-line-argument-in-windo

        // Sequence of backslashes followed by a double quote:
        // double up all the backslashes and escape the double quote
        arg = arg.replaceAll("(\\\\*)\"$", "$1$1\\\"");

        // Sequence of backslashes followed by the end of the arg,
        // which will become a double quote later:
        // double up all the backslashes
        arg = arg.replaceAll("(\\\\*)$", "$1$1");
        
        // Escape double-quotes
        arg = arg.replaceAll("([\"])", "\\\\$1");
        
        // Double-quote the whole thing
        // Interestingly, java does not treat these as part of the argument.
        arg = "\""+arg+"\"";

        // Interseting notes... 
        // 1) Java seems to automatically wrap arguments with quotes that need 'em, but if we provide quotes ourselves, it just uses those.
        // 2) Java seems to automatically escape special characters (()%!^"<>&|;, ]), so we don't do it explicitly here.
        // 3) We're escaping double-quotes differently than what qntm suggested
        
        return arg;
    }
}
