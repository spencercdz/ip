import java.util.Locale;

/**
 * Identifies the commands that Jaku can recognize from user input.
 */
public enum Command {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    DELETE("delete"),
    UNKNOWN("");

    /** Text entered by the user to invoke this command. */
    private final String keyword;

    /**
     * Creates a command with its user-facing keyword.
     *
     * @param keyword text used to invoke the command
     */
    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds the command represented by the first word of an input line.
     *
     * @param input the trimmed, non-empty user input
     * @return the matching command, or {@link #UNKNOWN} when no command matches
     */
    public static Command fromInput(String input) {
        String commandWord = input.split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
        for (Command command : values()) {
            if (!command.keyword.isEmpty() && command.keyword.equals(commandWord)) {
                return command;
            }
        }
        return UNKNOWN;
    }

    /**
     * Returns the text used to invoke this command.
     *
     * @return the command keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the portion of an input line after this command's keyword.
     *
     * @param input a user input line beginning with this command
     * @return the trimmed command arguments, or an empty string when absent
     */
    public String getArguments(String input) {
        return input.substring(keyword.length()).trim();
    }
}
