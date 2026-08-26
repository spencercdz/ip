package jaku.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import jaku.JakuException;

/**
 * Verifies Jaku's one-based task numbering and task-list boundaries.
 */
class TaskListTest {
    /** Verifies that the first and last displayed task numbers map to list indexes. */
    @Test
    void getIndex_convertsOneBasedTaskNumbers() throws JakuException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));

        assertEquals(0, tasks.getIndex(1));
        assertEquals(2, tasks.getIndex(3));
    }

    /** Verifies that zero, negative, and too-large task numbers are rejected. */
    @Test
    void getIndex_rejectsTaskNumbersOutsideTheList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only task"));

        assertInvalidTaskNumber(tasks, 0);
        assertInvalidTaskNumber(tasks, -1);
        assertInvalidTaskNumber(tasks, 2);
    }

    /** Verifies that every task number is invalid when the list is empty. */
    @Test
    void getIndex_rejectsTaskNumberForEmptyList() {
        assertInvalidTaskNumber(new TaskList(), 1);
    }

    /**
     * Verifies the standard error message for an invalid task number.
     *
     * @param tasks list against which to validate the number
     * @param taskNumber invalid one-based task number
     */
    private void assertInvalidTaskNumber(TaskList tasks, int taskNumber) {
        JakuException exception = assertThrows(JakuException.class, () -> tasks.getIndex(taskNumber));
        assertEquals("Task number " + taskNumber + " is not in the list.", exception.getMessage());
    }
}
