import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Entry point of the Jaku chatbot.
 * <p>
 * Jaku greets the user, tracks todos, deadlines, and events, shows those tasks
 * on request via the {@code list} command, and says goodbye when the user enters
 * the {@code bye} command.
 */
public class Jaku {
    /** Name the chatbot introduces itself with. */
    private static final String NAME = "Jaku";

    /** Command the user types to end the conversation. */
    private static final String BYE_COMMAND = "bye";

    /** Command the user types to see everything added so far. */
    private static final String LIST_COMMAND = "list";

    /** Command the user types to mark a task as done. */
    private static final String MARK_COMMAND = "mark";

    /** Command the user types to mark a task as not done. */
    private static final String UNMARK_COMMAND = "unmark";

    /** Command the user types to add a task without an attached date or time. */
    private static final String TODO_COMMAND = "todo";

    /** Command the user types to add a task that must be completed by a given time. */
    private static final String DEADLINE_COMMAND = "deadline";

    /** Command the user types to add a task with a start and end time. */
    private static final String EVENT_COMMAND = "event";

    /** Command the user types to remove a task from the list. */
    private static final String DELETE_COMMAND = "delete";

    /** Marker between a deadline's description and its due date or time. */
    private static final String BY_SEPARATOR = "/by";

    /** Marker between an event's description and its start date or time. */
    private static final String FROM_SEPARATOR = "/from";

    /** Marker between an event's start and end dates or times. */
    private static final String TO_SEPARATOR = "/to";

    /** Horizontal line used to separate the chatbot's replies from the user's input. */
    private static final String DIVIDER = "____________________________________________________________";

    /** ASCII-art banner spelling out the chatbot's name. */
    private static final String BANNER = "     _     _     _  __ _   _ \n"
            + "    | |   / \\   | |/ /| | | |\n"
            + " _  | |  / _ \\  | ' / | | | |\n"
            + "| |_| | / ___ \\ | . \\ | |_| |\n"
            + " \\___/ /_/   \\_\\|_|\\_\\ \\___/ ";

    /** Tasks the user has added, in the order they were added. */
    private final List<Task> tasks = new ArrayList<>();

    /**
     * Starts a new Jaku chat session.
     *
     * @param args command-line arguments, which Jaku does not use
     */
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
                try {
                    handleCommand(input);
                } catch (JakuException exception) {
                    reply(exception.getMessage());
                }
            }
        }
    }

    /**
     * Carries out a single command from the user.
     *
     * @param input the trimmed, non-empty line the user entered
     * @throws JakuException if the command is not recognized or its arguments are invalid
     */
    private void handleCommand(String input) throws JakuException {
        if (input.equalsIgnoreCase(LIST_COMMAND)) {
            showTasks();
        } else if (matchesCommand(input, MARK_COMMAND)) {
            markTask(input);
        } else if (matchesCommand(input, UNMARK_COMMAND)) {
            unmarkTask(input);
        } else if (matchesCommand(input, TODO_COMMAND)) {
            addTodo(input);
        } else if (matchesCommand(input, DEADLINE_COMMAND)) {
            addDeadline(input);
        } else if (matchesCommand(input, EVENT_COMMAND)) {
            addEvent(input);
        } else if (matchesCommand(input, DELETE_COMMAND)) {
            deleteTask(input);
        } else {
            throw new JakuException(
                    "I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, or bye."
            );
        }
    }

    /**
     * Returns whether the input is the command itself or begins with the command and a space.
     *
     * @param input the complete user input
     * @param command the command word to match
     * @return true when the command matches, ignoring letter case
     */
    private boolean matchesCommand(String input, String command) {
        return input.equalsIgnoreCase(command) || startsWithCommand(input, command);
    }

    /**
     * Returns whether the input begins with the given command and a separating space.
     *
     * @param input the complete user input
     * @param command the command word to match
     * @return true when the input starts with the command, ignoring letter case
     */
    private boolean startsWithCommand(String input, String command) {
        return input.toLowerCase(Locale.ROOT).startsWith(command + " ");
    }

    /**
     * Creates a todo from its command and adds it to the task list.
     *
     * @param input a todo command followed by a description
     * @throws JakuException if the todo description is empty
     */
    private void addTodo(String input) throws JakuException {
        String description = input.substring(TODO_COMMAND.length()).trim();
        if (description.isEmpty()) {
            throw new JakuException("I need a description after \"todo\".");
        }
        addTask(new Todo(description));
    }

    /**
     * Creates a deadline from its command and adds it to the task list.
     *
     * @param input a deadline command containing a description and {@code /by} value
     * @throws JakuException if the description, separator, or due text is missing
     */
    private void addDeadline(String input) throws JakuException {
        String arguments = input.substring(DEADLINE_COMMAND.length()).trim();
        int separatorIndex = arguments.indexOf(BY_SEPARATOR);
        if (separatorIndex < 0) {
            throw new JakuException("Use: deadline <description> /by <date or time>.");
        }
        String description = arguments.substring(0, separatorIndex).trim();
        String by = arguments.substring(separatorIndex + BY_SEPARATOR.length()).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new JakuException("Use: deadline <description> /by <date or time>.");
        }
        addTask(new Deadline(description, by));
    }

    /**
     * Creates an event from its command and adds it to the task list.
     *
     * @param input an event command containing a description, {@code /from}, and {@code /to} values
     * @throws JakuException if the description, separators, start text, or end text is missing
     */
    private void addEvent(String input) throws JakuException {
        String arguments = input.substring(EVENT_COMMAND.length()).trim();
        int fromIndex = arguments.indexOf(FROM_SEPARATOR);
        if (fromIndex < 0) {
            throw new JakuException("Use: event <description> /from <start> /to <end>.");
        }
        int toIndex = arguments.indexOf(TO_SEPARATOR, fromIndex + FROM_SEPARATOR.length());
        if (toIndex < 0) {
            throw new JakuException("Use: event <description> /from <start> /to <end>.");
        }
        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + FROM_SEPARATOR.length(), toIndex).trim();
        String to = arguments.substring(toIndex + TO_SEPARATOR.length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new JakuException("Use: event <description> /from <start> /to <end>.");
        }
        addTask(new Event(description, from, to));
    }

    /**
     * Stores a new task and confirms its addition and the updated task count.
     *
     * @param task the task to remember
     */
    private void addTask(Task task) {
        tasks.add(task);
        reply(List.of(
                "Got it. I've added this task:",
                "  " + task,
                "Now you have " + tasks.size() + " tasks in the list."
        ));
    }

    /**
     * Marks the task selected by a one-based task number as done.
     *
     * @param input a mark command followed by a task number
     * @throws JakuException if the task number is missing, invalid, or outside the list
     */
    private void markTask(String input) throws JakuException {
        int taskIndex = parseTaskIndex(input, MARK_COMMAND);
        Task task = tasks.get(taskIndex);
        task.markAsDone();
        reply(List.of(
                "Nice! I've marked this task as done:",
                "  " + task
        ));
    }

    /**
     * Marks the task selected by a one-based task number as not done.
     *
     * @param input an unmark command followed by a task number
     * @throws JakuException if the task number is missing, invalid, or outside the list
     */
    private void unmarkTask(String input) throws JakuException {
        int taskIndex = parseTaskIndex(input, UNMARK_COMMAND);
        Task task = tasks.get(taskIndex);
        task.markAsNotDone();
        reply(List.of(
                "OK, I've marked this task as not done yet:",
                "  " + task
        ));
    }

    /**
     * Removes the task selected by a one-based task number.
     *
     * @param input a delete command followed by a task number
     * @throws JakuException if the task number is missing, invalid, or outside the list
     */
    private void deleteTask(String input) throws JakuException {
        int taskIndex = parseTaskIndex(input, DELETE_COMMAND);
        Task removedTask = tasks.remove(taskIndex);
        reply(List.of(
                "Noted. I've removed this task:",
                "  " + removedTask,
                "Now you have " + tasks.size() + " tasks in the list."
        ));
    }

    /**
     * Parses and validates the one-based task number supplied to a task command.
     *
     * @param input the complete user input
     * @param command the command whose task number should be parsed
     * @return the corresponding zero-based task-list index
     * @throws JakuException if the number is missing, nonnumeric, or outside the list
     */
    private int parseTaskIndex(String input, String command) throws JakuException {
        String taskNumberText = input.substring(command.length()).trim();
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new JakuException("Use: " + command + " <task number>.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new JakuException("Task number " + taskNumber + " is not in the list.");
        }
        return taskNumber - 1;
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
        lines.add("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            lines.add((i + 1) + "." + tasks.get(i));
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
