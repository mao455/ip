package bo;

import java.io.IOException;

import bo.model.Task;
import bo.model.TaskList;
import bo.parser.Parser;
import bo.storage.Storage;
import bo.ui.Ui;

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
        Parser parser = new Parser();
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
                processCommand(command, taskList, ui, parser);
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
    private static void processCommand(String command, TaskList taskList, Ui ui, Parser parser)
            throws BoException {
        Parser.Command parsedCommand = parser.parse(command);
        switch (parsedCommand.type()) {
        case LIST:
            ui.showTaskList(taskList);
            return;
        case DELETE:
            deleteTask(parsedCommand.taskIndex(), taskList, ui);
            return;
        case MARK:
            markTask(parsedCommand.taskIndex(), taskList, ui);
            return;
        case UNMARK:
            unmarkTask(parsedCommand.taskIndex(), taskList, ui);
            return;
        case FIND:
            ui.showMatchingTasks(taskList.find(parsedCommand.keyword()));
            return;
        case ADD:
            Task task = parsedCommand.task();
            if (!taskList.add(task)) {
                ui.showTaskListFull();
                return;
            }
            saveTasks(taskList, ui);
            ui.showTaskAdded(task, taskList.size());
            return;
        default:
            throw new BoException("I'm sorry, but I don't know what that means :-(");
        }
    }

    /**
     * Deletes the task at the index specified by a {@code delete} command.
     * Later tasks are shifted left so that task numbers remain consecutive.
     *
     * @param taskIndex the zero-based task index
     * @param taskList the tasks in the list
     * @param ui the UI used to show the result
     * @throws BoException if the task index is outside the list
     */
    private static void deleteTask(int taskIndex, TaskList taskList, Ui ui) throws BoException {
        if (taskIndex < 0 || taskIndex >= taskList.size()) {
            throw new BoException("I couldn't find a task with that number.");
        }

        Task deletedTask = taskList.remove(taskIndex);
        saveTasks(taskList, ui);
        ui.showTaskDeleted(deletedTask, taskList.size());
    }

    /**
     * Marks the task at the index specified by a {@code mark} command as done.
     *
     * @param taskIndex the zero-based task index
     * @param taskList the tasks in the list
     * @param ui the UI used to show the result
     */
    private static void markTask(int taskIndex, TaskList taskList, Ui ui) throws BoException {
        if (taskIndex < 0 || taskIndex >= taskList.size()) {
            throw new BoException("I couldn't find a task with that number.");
        }

        taskList.get(taskIndex).markAsDone();
        saveTasks(taskList, ui);
        ui.showTaskMarked(taskList.get(taskIndex));
    }

    /**
     * Marks the task at the index specified by an {@code unmark} command as not done.
     *
     * @param taskIndex the zero-based task index
     * @param taskList the tasks in the list
     * @param ui the UI used to show the result
     */
    private static void unmarkTask(int taskIndex, TaskList taskList, Ui ui) throws BoException {
        if (taskIndex < 0 || taskIndex >= taskList.size()) {
            throw new BoException("I couldn't find a task with that number.");
        }

        taskList.get(taskIndex).unmarkAsDone();
        saveTasks(taskList, ui);
        ui.showTaskUnmarked(taskList.get(taskIndex));
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
