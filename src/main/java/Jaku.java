import java.util.Scanner;

/**
 * Entry point of the Jaku chatbot.
 * <p>
 * Jaku greets the user, echoes back every line the user types, and says
 * goodbye when the user enters the {@code bye} command.
 */
public class Jaku {
    /** Name the chatbot introduces itself with. */
    private static final String NAME = "Jaku";

    /** Command the user types to end the conversation. */
    private static final String BYE_COMMAND = "bye";

    /** Horizontal line used to separate the chatbot's replies from the user's input. */
    private static final String DIVIDER = "____________________________________________________________";

    /** ASCII-art banner spelling out the chatbot's name. */
    private static final String BANNER = "     _     _     _  __ _   _ \n"
            + "    | |   / \\   | |/ /| | | |\n"
            + " _  | |  / _ \\  | ' / | | | |\n"
            + "| |_| | / ___ \\ | . \\ | |_| |\n"
            + " \\___/ /_/   \\_\\|_|\\_\\ \\___/ ";

    public static void main(String[] args) {
        greet();
        echoUntilBye();
        exit();
    }

    /**
     * Prints the welcome message shown when Jaku starts up.
     */
    private static void greet() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Hello there! I'm " + NAME + ".");
        System.out.println("How can I help you today?");
        System.out.println(DIVIDER);
    }

    /**
     * Reads the user's input line by line and echoes each line back.
     * <p>
     * Stops when the user enters the {@code bye} command, or when the input
     * runs out (for example when input is piped in from a file). Blank lines
     * are ignored, so that pressing Enter alone does not produce an empty
     * reply.
     */
    private static void echoUntilBye() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase(BYE_COMMAND)) {
                    return;
                }
                if (input.isEmpty()) {
                    continue;
                }
                echo(input);
            }
        }
    }

    /**
     * Prints a single reply from Jaku, framed by divider lines.
     *
     * @param message the text to show to the user
     */
    private static void echo(String message) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println(DIVIDER);
    }

    /**
     * Prints the farewell message shown before Jaku shuts down.
     */
    private static void exit() {
        System.out.println("Bye for now. Hope to see you again soon!");
        System.out.println(DIVIDER);
    }
}
