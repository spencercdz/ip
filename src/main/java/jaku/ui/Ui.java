package jaku.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jaku.task.TaskList;

/**
 * Handles Jaku's console input and all text displayed to the user.
 */
public class Ui {
    /** Name the chatbot introduces itself with. */
    private static final String NAME = "Jaku";

    /** Horizontal line used to separate the chatbot's replies from user input. */
    private static final String DIVIDER = "____________________________________________________________";

    /** ASCII-art banner spelling out the chatbot's name. */
    private static final String BANNER = "     _     _     _  __ _   _ \n"
            + "    | |   / \\   | |/ /| | | |\n"
            + " _  | |  / _ \\  | ' / | | | |\n"
            + "| |_| | / ___ \\ | . \\ | |_| |\n"
            + " \\___/ /_/   \\_\\|_|\\_\\ \\___/ ";

    /** Source of commands typed into Jaku's console. */
    private final Scanner scanner;

    /** Creates a user interface that reads commands from the standard input stream. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another input line is available.
     *
     * @return true when a command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command entered by the user.
     *
     * @return the next input line without surrounding whitespace
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Prints Jaku's welcome message. */
    public void showWelcome() {
        showLine();
        System.out.println(BANNER);
        System.out.println("Hello there! I'm " + NAME + ".");
        System.out.println("How can I help you today?");
        showLine();
    }

    /**
     * Shows a one-line framed reply.
     *
     * @param line text to show to the user
     */
    public void showResponse(String line) {
        showResponse(List.of(line));
    }

    /**
     * Shows a framed multi-line reply.
     *
     * @param lines lines to show to the user, in order
     */
    public void showResponse(List<String> lines) {
        showLine();
        for (String line : lines) {
            System.out.println(line);
        }
        showLine();
    }

    /**
     * Shows every task in a list, numbered from one.
     *
     * @param tasks tasks to show
     */
    public void showTaskList(TaskList tasks) {
        if (tasks.isEmpty()) {
            showResponse("Your list is empty for now.");
            return;
        }
        List<String> lines = new ArrayList<>();
        lines.add("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            lines.add((i + 1) + "." + tasks.get(i));
        }
        showResponse(lines);
    }

    /** Prints the farewell message shown before Jaku shuts down. */
    public void showGoodbye() {
        System.out.println("Bye for now. Hope to see you again soon!");
        showLine();
    }

    /** Prints the standard horizontal divider. */
    public void showLine() {
        System.out.println(DIVIDER);
    }
}
