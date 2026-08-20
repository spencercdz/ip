import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point of the Jaku chatbot.
 * <p>
 * Jaku greets the user, remembers each item the user adds, shows those items
 * on request via the {@code list} command, and says goodbye when the user
 * enters the {@code bye} command.
 */
public class Jaku {
    /** Name the chatbot introduces itself with. */
    private static final String NAME = "Jaku";

    /** Command the user types to end the conversation. */
    private static final String BYE_COMMAND = "bye";

    /** Command the user types to see everything added so far. */
    private static final String LIST_COMMAND = "list";

    /** Horizontal line used to separate the chatbot's replies from the user's input. */
    private static final String DIVIDER = "____________________________________________________________";

    /** ASCII-art banner spelling out the chatbot's name. */
    private static final String BANNER = "     _     _     _  __ _   _ \n"
            + "    | |   / \\   | |/ /| | | |\n"
            + " _  | |  / _ \\  | ' / | | | |\n"
            + "| |_| | / ___ \\ | . \\ | |_| |\n"
            + " \\___/ /_/   \\_\\|_|\\_\\ \\___/ ";

    /** Items the user has added, in the order they were added. */
    private final List<String> tasks = new ArrayList<>();

    public static void main(String[] args) {
        new Jaku().run();
    }

    /**
     * Runs one chat session, from greeting to farewell.
     */
    private void run() {
        greet();
        readCommandsUntilBye();
        exit();
    }

    /**
     * Prints the welcome message shown when Jaku starts up.
     */
    private void greet() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Hello there! I'm " + NAME + ".");
        System.out.println("How can I help you today?");
        System.out.println(DIVIDER);
    }

    /**
     * Reads the user's input line by line and carries out each command.
     * <p>
     * Stops when the user enters the {@code bye} command, or when the input
     * runs out (for example when input is piped in from a file). Blank lines
     * are ignored, so that pressing Enter alone does not produce an empty
     * reply.
     */
    private void readCommandsUntilBye() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase(BYE_COMMAND)) {
                    return;
                }
                if (input.isEmpty()) {
                    continue;
                }
                handleCommand(input);
            }
        }
    }

    /**
     * Carries out a single command from the user. Any input other than
     * {@code list} is taken to be a new item to remember.
     *
     * @param input the trimmed, non-empty line the user entered
     */
    private void handleCommand(String input) {
        if (input.equalsIgnoreCase(LIST_COMMAND)) {
            showTasks();
        } else {
            addTask(input);
        }
    }

    /**
     * Stores a new item and confirms it to the user.
     *
     * @param description the text of the item to remember
     */
    private void addTask(String description) {
        tasks.add(description);
        reply("added: " + description);
    }

    /**
     * Shows every stored item, numbered from one.
     */
    private void showTasks() {
        if (tasks.isEmpty()) {
            reply("Your list is empty for now.");
            return;
        }
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            lines.add((i + 1) + ". " + tasks.get(i));
        }
        reply(lines);
    }

    /**
     * Prints a single-line reply from Jaku.
     *
     * @param line the text to show to the user
     */
    private void reply(String line) {
        reply(List.of(line));
    }

    /**
     * Prints a reply from Jaku, framed by divider lines.
     *
     * @param lines the lines to show to the user, in order
     */
    private void reply(List<String> lines) {
        System.out.println(DIVIDER);
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println(DIVIDER);
    }

    /**
     * Prints the farewell message shown before Jaku shuts down.
     */
    private void exit() {
        System.out.println("Bye for now. Hope to see you again soon!");
        System.out.println(DIVIDER);
    }
}
