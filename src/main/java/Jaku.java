import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
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

    /** Saves and loads Jaku's tasks. */
    private final Storage storage;

    /**
     * Creates Jaku with its default relative data-file location.
     */
    public Jaku() {
        this(new Storage(Path.of(System.getProperty("jaku.dataFile", "data/jaku.txt"))));
    }

    /**
     * Creates Jaku using the supplied storage mechanism.
     *
     * @param storage persistence mechanism for tasks
     */
    public Jaku(Storage storage) {
        this.storage = storage;
    }

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
        loadTasks();
        greet();
        readCommandsUntilBye();
        exit();
    }

    /**
     * Loads saved tasks before accepting user commands.
     */
    private void loadTasks() {
        try {
            tasks.addAll(storage.load());
        } catch (JakuException exception) {
            reply(exception.getMessage());
        }
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
                if (input.isEmpty()) {
                    continue;
                }
                Command command = Command.fromInput(input);
                if (command == Command.BYE && command.getArguments(input).isEmpty()) {
                    return;
                }
                try {
                    handleCommand(command, input);
                } catch (JakuException exception) {
                    reply(exception.getMessage());
                }
            }
        }
    }

    /**
     * Carries out a single command from the user.
     *
     * @param command the recognized command type
     * @param input the trimmed, non-empty line the user entered
     * @throws JakuException if the command is not recognized or its arguments are invalid
     */
    private void handleCommand(Command command, String input) throws JakuException {
        switch (command) {
        case LIST:
            if (!command.getArguments(input).isEmpty()) {
                throw unknownCommandException();
            }
            showTasks();
            break;
        case MARK:
            markTask(input);
            break;
        case UNMARK:
            unmarkTask(input);
            break;
        case TODO:
            addTodo(input);
            break;
        case DEADLINE:
            addDeadline(input);
            break;
        case EVENT:
            addEvent(input);
            break;
        case DELETE:
            deleteTask(input);
            break;
        case BYE:
        case UNKNOWN:
            throw unknownCommandException();
        default:
            throw new AssertionError("Unhandled command: " + command);
        }
    }

    /**
     * Creates the error shown when no supported command matches the user's input.
     *
     * @return an exception containing the supported-command guidance
     */
    private JakuException unknownCommandException() {
        return new JakuException(
                "I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, or bye."
        );
    }

    /**
     * Creates a todo from its command and adds it to the task list.
     *
     * @param input a todo command followed by a description
     * @throws JakuException if the todo description is empty
     */
    private void addTodo(String input) throws JakuException {
        String description = Command.TODO.getArguments(input);
        if (description.isEmpty()) {
            throw new JakuException("I need a description after \"todo\".");
        }
        addTask(new Todo(description));
    }

    /**
     * Creates a deadline from its command and adds it to the task list.
     *
     * @param input a deadline command containing a description and {@code /by} value
     * @throws JakuException if the description, separator, or due date is invalid
     */
    private void addDeadline(String input) throws JakuException {
        String arguments = Command.DEADLINE.getArguments(input);
        int separatorIndex = arguments.indexOf(BY_SEPARATOR);
        if (separatorIndex < 0) {
            throw new JakuException("Use: deadline <description> /by <date or time>.");
        }
        String description = arguments.substring(0, separatorIndex).trim();
        String by = arguments.substring(separatorIndex + BY_SEPARATOR.length()).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new JakuException("Use: deadline <description> /by <date or time>.");
        }
        try {
            addTask(new Deadline(description, LocalDate.parse(by)));
        } catch (DateTimeParseException exception) {
            throw new JakuException("Use: deadline <description> /by yyyy-MM-dd.");
        }
    }

    /**
     * Creates an event from its command and adds it to the task list.
     *
     * @param input an event command containing a description, {@code /from}, and {@code /to} values
     * @throws JakuException if the description, separators, start text, or end text is missing
     */
    private void addEvent(String input) throws JakuException {
        String arguments = Command.EVENT.getArguments(input);
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
    private void addTask(Task task) throws JakuException {
        tasks.add(task);
        try {
            saveTasks();
        } catch (JakuException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
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
        int taskIndex = parseTaskIndex(input, Command.MARK);
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        task.markAsDone();
        try {
            saveTasks();
        } catch (JakuException exception) {
            restoreTaskStatus(task, wasDone);
            throw exception;
        }
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
        int taskIndex = parseTaskIndex(input, Command.UNMARK);
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        task.markAsNotDone();
        try {
            saveTasks();
        } catch (JakuException exception) {
            restoreTaskStatus(task, wasDone);
            throw exception;
        }
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
        int taskIndex = parseTaskIndex(input, Command.DELETE);
        Task removedTask = tasks.remove(taskIndex);
        try {
            saveTasks();
        } catch (JakuException exception) {
            tasks.add(taskIndex, removedTask);
            throw exception;
        }
        reply(List.of(
                "Noted. I've removed this task:",
                "  " + removedTask,
                "Now you have " + tasks.size() + " tasks in the list."
        ));
    }

    /**
     * Writes the current task list to persistent storage.
     *
     * @throws JakuException if the task list cannot be saved
     */
    private void saveTasks() throws JakuException {
        storage.save(tasks);
    }

    /**
     * Restores a task's completion status after an unsuccessful save.
     *
     * @param task task whose status should be restored
     * @param wasDone completion status before the attempted mutation
     */
    private void restoreTaskStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }

    /**
     * Parses and validates the one-based task number supplied to a task command.
     *
     * @param input the complete user input
     * @param command the command whose task number should be parsed
     * @return the corresponding zero-based task-list index
     * @throws JakuException if the number is missing, nonnumeric, or outside the list
     */
    private int parseTaskIndex(String input, Command command) throws JakuException {
        String taskNumberText = command.getArguments(input);
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new JakuException("Use: " + command.getKeyword() + " <task number>.");
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
