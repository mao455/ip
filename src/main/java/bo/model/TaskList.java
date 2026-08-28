package bo.model;

import java.util.Arrays;

/**
 * Owns the tasks currently tracked by Bo and operations on that collection.
 */
public final class TaskList {
    /** Maximum number of tasks supported by the current application. */
    public static final int DEFAULT_CAPACITY = 100;

    /** The backing array containing the tasks in list order. */
    private final Task[] tasks;

    /** Number of occupied positions in {@link #tasks}. */
    private int taskCount;

    /** Creates an empty task list with the default capacity. */
    public TaskList() {
        this.tasks = new Task[DEFAULT_CAPACITY];
        this.taskCount = 0;
    }

    /**
     * Creates a task list initialized from tasks loaded by storage.
     *
     * @param initialTasks the array containing loaded tasks
     * @param initialTaskCount number of valid tasks in {@code initialTasks}
     * @throws IllegalArgumentException if the initial array or count is invalid
     */
    public TaskList(Task[] initialTasks, int initialTaskCount) {
        if (initialTasks == null || initialTaskCount < 0
                || initialTaskCount > initialTasks.length) {
            throw new IllegalArgumentException("The initial tasks are invalid.");
        }
        this.tasks = Arrays.copyOf(initialTasks, initialTasks.length);
        this.taskCount = initialTaskCount;
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the number of tasks
     */
    public int size() {
        return taskCount;
    }

    /**
     * Returns whether this list cannot accept another task.
     *
     * @return {@code true} when the list has reached its capacity
     */
    public boolean isFull() {
        return taskCount == tasks.length;
    }

    /**
     * Adds a task when capacity is available.
     *
     * @param task the task to add
     * @return {@code true} when the task was added, or {@code false} when full
     */
    public boolean add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("A task cannot be null.");
        }
        if (isFull()) {
            return false;
        }
        tasks[taskCount] = task;
        taskCount++;
        return true;
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index the zero-based task index
     * @return the task at that index
     * @throws IndexOutOfBoundsException if the index is outside this list
     */
    public Task get(int index) {
        checkIndex(index);
        return tasks[index];
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index the zero-based task index
     * @return the removed task
     * @throws IndexOutOfBoundsException if the index is outside this list
     */
    public Task remove(int index) {
        checkIndex(index);
        Task removedTask = tasks[index];
        int tasksToShift = taskCount - index - 1;
        if (tasksToShift > 0) {
            System.arraycopy(tasks, index + 1, tasks, index, tasksToShift);
        }
        taskCount--;
        tasks[taskCount] = null;
        return removedTask;
    }

    /**
     * Returns a copy containing the current tasks for storage to serialize.
     *
     * @return an array containing exactly the current tasks
     */
    public Task[] toArray() {
        return Arrays.copyOf(tasks, taskCount);
    }

    /** Checks an index before accessing the backing array. */
    private void checkIndex(int index) {
        if (index < 0 || index >= taskCount) {
            throw new IndexOutOfBoundsException("Task index: " + index);
        }
    }
}
