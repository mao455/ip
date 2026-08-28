import java.io.IOException;

/**
 * The main entry point for the Bo chatbot.
 */
public class Bo {
    /**
     * Starts Bo and processes commands from standard input until {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        Task[] loadedTasks = new Task[TaskList.DEFAULT_CAPACITY];
        TaskList taskList = new TaskList(loadedTasks, loadTasks(loadedTasks, ui));

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();

            if (command.equals("bye")) {
                ui.showGoodbye();
                break;
            }

            ui.showSeparator();
            try {
                processCommand(command, taskList, ui);
            } catch (BoException exception) {
                ui.showError(exception.getMessage());
            }
            ui.showSeparator();
        }
    }

    /**
     * Loads the tasks saved by an earlier Bo session.
     *
     * <p>Bo starts with an empty list when no file exists or when the file
     * cannot be read. In the latter case, a warning is shown so the user knows
     * that the saved data was not available.
     *
     * @param tasks the array into which loaded tasks are placed
     * @param ui the UI used to show loading warnings
     * @return the number of tasks loaded
     */
    private static int loadTasks(Task[] tasks, Ui ui) {
        try {
            Storage.LoadResult result = Storage.loadWithReport(tasks);
            ui.showLoadingWarnings(result);
            return result.getTaskCount();
        } catch (IOException | IllegalArgumentException | SecurityException exception) {
            ui.showLoadingError();
            return 0;
        }
    }

    /**
     * Processes one command using the supplied task list.
     *
     * @param command the complete command entered by the user
     * @param taskList the tasks in the list
     * @throws BoException if the command is invalid
     */
    private static void processCommand(String command, TaskList taskList, Ui ui) throws BoException {
        if (command.isEmpty()) {
            throw new BoException("Please enter a command instead of an empty line.");
        }

        String commandName = command.split("\\s+", 2)[0];
        if (command.equals("list")) {
            ui.showTaskList(taskList);
            return;
        }
        if (commandName.equals("delete")) {
            deleteTask(command, taskList, ui);
            return;
        }
        if (commandName.equals("mark")) {
            markTask(command, taskList, ui);
            return;
        }
        if (commandName.equals("unmark")) {
            unmarkTask(command, taskList, ui);
            return;
        }
        if (commandName.equals("todo") || commandName.equals("deadline")
                || commandName.equals("event")) {
            Task task = createTask(command);
            if (!taskList.add(task)) {
                ui.showTaskListFull();
                return;
            }
            saveTasks(taskList, ui);
            ui.showTaskAdded(task, taskList.size());
            return;
        }

        throw new BoException("I'm sorry, but I don't know what that means :-(");
    }

    /**
     * Deletes the task at the index specified by a {@code delete} command.
     * Later tasks are shifted left so that task numbers remain consecutive.
     *
     * @param command the complete command entered by the user
     * @param taskList the tasks in the list
     * @param ui the UI used to show the result
     * @throws BoException if the command does not contain a valid task number
     */
    private static void deleteTask(String command, TaskList taskList, Ui ui) throws BoException {
        String[] commandParts = command.split("\\s+");
        if (commandParts.length != 2) {
            throw new BoException("Please use delete followed by one task number, e.g. delete 1.");
        }

        final int taskIndex;
        try {
            taskIndex = Integer.parseInt(commandParts[1]) - 1;
        } catch (NumberFormatException exception) {
            throw new BoException("The task number must be a whole number.");
        }

        if (taskIndex < 0 || taskIndex >= taskList.size()) {
            throw new BoException("I couldn't find a task with that number.");
        }

        Task deletedTask = taskList.remove(taskIndex);
        saveTasks(taskList, ui);
        ui.showTaskDeleted(deletedTask, taskList.size());
    }

    /**
     * Creates the appropriate task subtype from an add-task command.
     * Recognized dates and times are parsed by the task constructors and stored
     * as {@code LocalDateTime} values.
     *
     * @param command the complete command entered by the user
     * @return the task represented by the command
     */
    private static Task createTask(String command) throws BoException {
        String[] commandParts = command.split("\\s+", 2);
        String commandName = commandParts[0];
        String taskDetails = commandParts.length == 2 ? commandParts[1].strip() : "";

        if (commandName.equals("todo")) {
            if (taskDetails.isEmpty()) {
                throw new BoException("The description of a todo cannot be empty.");
            }
            return new Todo(taskDetails);
        }

        if (commandName.equals("deadline")) {
            if (taskDetails.isEmpty()) {
                throw new BoException("A deadline needs a description and a /by date.");
            }

            int byMarker = taskDetails.indexOf(" /by ");
            if (byMarker < 0) {
                throw new BoException("A deadline must include a /by date, e.g. deadline return book /by Friday.");
            }
            String description = taskDetails.substring(0, byMarker).strip();
            String by = taskDetails.substring(byMarker + " /by ".length()).strip();
            if (description.isEmpty()) {
                throw new BoException("The description of a deadline cannot be empty.");
            }
            if (by.isEmpty()) {
                throw new BoException("The /by date of a deadline cannot be empty.");
            }
            return new Deadline(description, by);
        }

        if (commandName.equals("event")) {
            if (taskDetails.isEmpty()) {
                throw new BoException("An event needs a description, a /from time, and a /to time.");
            }

            int fromMarker = taskDetails.indexOf(" /from ");
            int toMarker = taskDetails.indexOf(" /to ");
            if (fromMarker < 0) {
                throw new BoException("An event must include a /from time.");
            }
            if (toMarker < 0) {
                throw new BoException("An event must include a /to time.");
            }
            if (toMarker < fromMarker) {
                throw new BoException("Please put the /from time before the /to time.");
            }

            String description = taskDetails.substring(0, fromMarker).strip();
            String from = taskDetails.substring(fromMarker + " /from ".length(), toMarker).strip();
            String to = taskDetails.substring(toMarker + " /to ".length()).strip();
            if (description.isEmpty()) {
                throw new BoException("The description of an event cannot be empty.");
            }
            if (from.isEmpty()) {
                throw new BoException("The /from time of an event cannot be empty.");
            }
            if (to.isEmpty()) {
                throw new BoException("The /to time of an event cannot be empty.");
            }
            return new Event(description, from, to);
        }

        throw new BoException("I'm sorry, but I don't know what that means :-(");
    }

    /**
     * Marks the task at the index specified by a {@code mark} command as done.
     *
     * @param command the complete command entered by the user
     * @param taskList the tasks in the list
     * @param ui the UI used to show the result
     */
    private static void markTask(String command, TaskList taskList, Ui ui) throws BoException {
        String[] commandParts = command.split("\\s+");
        if (commandParts.length != 2) {
            throw new BoException("Please use mark followed by one task number, e.g. mark 1.");
        }

        try {
            int taskIndex = Integer.parseInt(commandParts[1]) - 1;
            if (taskIndex < 0 || taskIndex >= taskList.size()) {
                throw new BoException("I couldn't find a task with that number.");
            }

            taskList.get(taskIndex).markAsDone();
            saveTasks(taskList, ui);
            ui.showTaskMarked(taskList.get(taskIndex));
        } catch (NumberFormatException exception) {
            throw new BoException("The task number must be a whole number.");
        }
    }

    /**
     * Marks the task at the index specified by an {@code unmark} command as not done.
     *
     * @param command the complete command entered by the user
     * @param taskList the tasks in the list
     * @param ui the UI used to show the result
     */
    private static void unmarkTask(String command, TaskList taskList, Ui ui) throws BoException {
        String[] commandParts = command.split("\\s+");
        if (commandParts.length != 2) {
            throw new BoException("Please use unmark followed by one task number, e.g. unmark 1.");
        }

        try {
            int taskIndex = Integer.parseInt(commandParts[1]) - 1;
            if (taskIndex < 0 || taskIndex >= taskList.size()) {
                throw new BoException("I couldn't find a task with that number.");
            }

            taskList.get(taskIndex).unmarkAsDone();
            saveTasks(taskList, ui);
            ui.showTaskUnmarked(taskList.get(taskIndex));
        } catch (NumberFormatException exception) {
            throw new BoException("The task number must be a whole number.");
        }
    }

    /**
     * Saves the current task list after a successful mutation.
     *
     * <p>The in-memory operation remains successful if the file system is
     * unavailable, but Bo reports the persistence problem to the user.
     *
     * @param taskList the tasks in the list
     * @param ui the UI used to show save warnings
     */
    private static void saveTasks(TaskList taskList, Ui ui) {
        try {
            Task[] tasks = taskList.toArray();
            Storage.save(tasks, tasks.length);
        } catch (IOException | IllegalArgumentException | SecurityException exception) {
            ui.showSavingError();
        }
    }
}
