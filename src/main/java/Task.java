/**
 * Represents a task tracked by Jaku, including its description and completion status.
 */
public class Task {
    /** Description supplied by the user when the task is created. */
    private final String description;

    /** Single-letter icon identifying the kind of task. */
    private final String typeIcon;

    /** Optional date or time information appended when the task is displayed. */
    private final String timingDetails;

    /** Whether the task has been completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task with its display type and optional timing details.
     *
     * @param description the text describing the task
     * @param typeIcon the single-letter icon identifying the task type
     * @param timingDetails formatted timing details, or an empty string when absent
     */
    public Task(String description, String typeIcon, String timingDetails) {
        this.description = description;
        this.typeIcon = typeIcon;
        this.timingDetails = timingDetails;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} when done, or a space when not done
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task in the format shown in Jaku's task list.
     *
     * @return the status icon followed by the task description
     */
    @Override
    public String toString() {
        return "[" + typeIcon + "][" + getStatusIcon() + "] " + description + timingDetails;
    }
}
