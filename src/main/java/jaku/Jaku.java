package jaku;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import jaku.parser.Command;
import jaku.parser.Parser;
import jaku.storage.Storage;
import jaku.task.Deadline;
import jaku.task.Event;
import jaku.task.Task;
import jaku.task.TaskList;
import jaku.task.Todo;
import jaku.ui.Ui;

/**
 * Coordinates Jaku's parser, task list, storage, and console user interface.
 * <p>
 * A chat session loads saved tasks, accepts commands until {@code bye}, and
 * delegates persistence and presentation to the appropriate collaborators.
 */
public class Jaku {
    /** Marker between a deadline's description and its due date. */
    private static final String BY_SEPARATOR = "/by";

    /** Marker between an event's description and its start date or time. */
    private static final String FROM_SEPARATOR = "/from";

    /** Marker between an event's start and end dates or times. */
    private static final String TO_SEPARATOR = "/to";

    /** Saves and loads Jaku's tasks. */
    private final Storage storage;

    /** Stores the tasks in the current chat session. */
    private final TaskList tasks;

    /** Reads user commands and displays Jaku's replies. */
    private final Ui ui;

    /** Creates Jaku with its default relative data-file location. */
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
        this.ui = new Ui();
        this.tasks = loadTasks();
    }

    /**
     * Starts a new Jaku chat session.
     *
     * @param args command-line arguments, which Jaku does not use
     */
    public static void main(String[] args) {
        new Jaku().run();
    }

    /** Runs one chat session, from greeting to farewell. */
    private void run() {
        ui.showWelcome();
        readCommandsUntilBye();
        ui.showGoodbye();
    }

    /**
     * Loads saved tasks before accepting user commands.
     *
     * @return loaded tasks, or an empty list when loading fails
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.load());
        } catch (JakuException exception) {
            ui.showResponse(exception.getMessage());
            return new TaskList();
        }
    }

    /** Reads user commands line by line until the user enters {@code bye}. */
    private void readCommandsUntilBye() {
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            if (input.isEmpty()) {
                continue;
            }
            Command command = Parser.parseCommand(input);
            if (command == Command.BYE && Parser.getArguments(input, command).isEmpty()) {
                return;
            }
            try {
                handleCommand(command, input);
            } catch (JakuException exception) {
                ui.showResponse(exception.getMessage());
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
            if (!Parser.getArguments(input, command).isEmpty()) {
                throw unknownCommandException();
            }
            ui.showTaskList(tasks);
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
        case FIND:
            findTasks(input);
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
                "I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, find, or bye."
        );
    }

    /**
     * Shows tasks whose descriptions contain the keyword supplied by the user.
     *
     * @param input a find command followed by a keyword
     * @throws JakuException if the find keyword is empty
     */
    private void findTasks(String input) throws JakuException {
        String keyword = Parser.getArguments(input, Command.FIND);
        if (keyword.isEmpty()) {
            throw new JakuException("I need a keyword after \"find\".");
        }
        ui.showMatchingTasks(tasks.find(keyword));
    }

    /**
     * Creates a todo from its command and adds it to the task list.
     *
     * @param input a todo command followed by a description
     * @throws JakuException if the todo description is empty
     */
    private void addTodo(String input) throws JakuException {
        String description = Parser.getArguments(input, Command.TODO);
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
        String arguments = Parser.getArguments(input, Command.DEADLINE);
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
        String arguments = Parser.getArguments(input, Command.EVENT);
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
     * @throws JakuException if the task list cannot be saved
     */
    private void addTask(Task task) throws JakuException {
        tasks.add(task);
        try {
            saveTasks();
        } catch (JakuException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        ui.showResponse(List.of(
                "Got it. I've added this task:",
                "  " + task,
                "Now you have " + tasks.size() + " tasks in the list."
        ));
    }

    /**
     * Marks the task selected by a one-based task number as done.
     *
     * @param input a mark command followed by a task number
     * @throws JakuException if the task number is invalid or outside the list
     */
    private void markTask(String input) throws JakuException {
        int taskIndex = tasks.getIndex(Parser.parseTaskNumber(input, Command.MARK));
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        task.markAsDone();
        try {
            saveTasks();
        } catch (JakuException exception) {
            restoreTaskStatus(task, wasDone);
            throw exception;
        }
        ui.showResponse(List.of("Nice! I've marked this task as done:", "  " + task));
    }

    /**
     * Marks the task selected by a one-based task number as not done.
     *
     * @param input an unmark command followed by a task number
     * @throws JakuException if the task number is invalid or outside the list
     */
    private void unmarkTask(String input) throws JakuException {
        int taskIndex = tasks.getIndex(Parser.parseTaskNumber(input, Command.UNMARK));
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        task.markAsNotDone();
        try {
            saveTasks();
        } catch (JakuException exception) {
            restoreTaskStatus(task, wasDone);
            throw exception;
        }
        ui.showResponse(List.of("OK, I've marked this task as not done yet:", "  " + task));
    }

    /**
     * Removes the task selected by a one-based task number.
     *
     * @param input a delete command followed by a task number
     * @throws JakuException if the task number is invalid or outside the list
     */
    private void deleteTask(String input) throws JakuException {
        int taskIndex = tasks.getIndex(Parser.parseTaskNumber(input, Command.DELETE));
        Task removedTask = tasks.remove(taskIndex);
        try {
            saveTasks();
        } catch (JakuException exception) {
            tasks.add(taskIndex, removedTask);
            throw exception;
        }
        ui.showResponse(List.of(
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
        storage.save(tasks.asList());
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
}
