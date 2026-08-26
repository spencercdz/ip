/**
 * Identifies a command that Jaku can carry out.
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
     * Returns the text used to invoke this command.
     *
     * @return the command keyword
     */
    public String getKeyword() {
        return keyword;
    }

}
