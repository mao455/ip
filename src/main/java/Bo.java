import java.util.Scanner;

/**
 * The main entry point for the Bo chatbot.
 */
public class Bo {
    /** Maximum number of tasks Bo can keep in memory for this increment. */
    private static final int MAX_TASKS = 100;

    /** Line used to separate Bo's responses. */
    private static final String SEPARATOR = "____________________________________________________________";

    /**
     * Starts Bo and processes commands from standard input until {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String banner = " ____        \n"
                + "| __ )  ___  \n"
                + "|  _ \\ / _ \\ \n"
                + "| |_) | (_) |\n"
                + "|____/ \\___/\n";
        System.out.println(SEPARATOR);
        System.out.print(banner);
        System.out.println("Hello! I'm Bo.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().strip();

            if (command.equals("bye")) {
                System.out.println(SEPARATOR);
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR);
                break;
            }

            System.out.println(SEPARATOR);
            try {
                taskCount = processCommand(command, tasks, taskCount);
            } catch (BoException exception) {
                System.out.println(" OOPS!!! " + exception.getMessage());
            }
            System.out.println(SEPARATOR);
        }
    }

    /**
     * Processes one command and returns the resulting number of tasks.
     *
     * @param command the complete command entered by the user
     * @param tasks the tasks in the list
     * @param taskCount the number of tasks currently stored
     * @return the updated number of tasks
     * @throws BoException if the command is invalid
     */
    private static int processCommand(String command, Task[] tasks, int taskCount) throws BoException {
        if (command.isEmpty()) {
            throw new BoException("Please enter a command instead of an empty line.");
        }

        String commandName = command.split("\\s+", 2)[0];
        if (command.equals("list")) {
            listTasks(tasks, taskCount);
            return taskCount;
        }
        if (commandName.equals("mark")) {
            markTask(command, tasks, taskCount);
            return taskCount;
        }
        if (commandName.equals("unmark")) {
            unmarkTask(command, tasks, taskCount);
            return taskCount;
        }
        if (commandName.equals("todo") || commandName.equals("deadline")
                || commandName.equals("event")) {
            Task task = createTask(command);
            if (taskCount >= MAX_TASKS) {
                System.out.println(" The task list is full.");
                return taskCount;
            }

            tasks[taskCount] = task;
            taskCount++;
            System.out.println(" Got it. I've added this task:");
            System.out.println("   " + task);
            System.out.println(" Now you have " + taskCount + " tasks in the list.");
            return taskCount;
        }

        throw new BoException("I'm sorry, but I don't know what that means :-(");
    }

    /**
     * Prints all tasks currently stored by Bo.
     *
     * @param tasks the tasks in the list
     * @param taskCount the number of tasks currently stored
     */
    private static void listTasks(Task[] tasks, int taskCount) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println(" " + (i + 1) + "." + tasks[i]);
        }
    }

    /**
     * Creates the appropriate task subtype from an add-task command.
     * Dates and times are deliberately kept as strings for this increment.
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
     * @param tasks the tasks in the list
     * @param taskCount the number of tasks currently stored
     */
    private static void markTask(String command, Task[] tasks, int taskCount) throws BoException {
        String[] commandParts = command.split("\\s+");
        if (commandParts.length != 2) {
            throw new BoException("Please use mark followed by one task number, e.g. mark 1.");
        }

        try {
            int taskIndex = Integer.parseInt(commandParts[1]) - 1;
            if (taskIndex < 0 || taskIndex >= taskCount) {
                throw new BoException("I couldn't find a task with that number.");
            }

            tasks[taskIndex].markAsDone();
            System.out.println(" Nice! I've marked this task as done:");
            System.out.println("   " + tasks[taskIndex]);
        } catch (NumberFormatException exception) {
            throw new BoException("The task number must be a whole number.");
        }
    }

    /**
     * Marks the task at the index specified by an {@code unmark} command as not done.
     *
     * @param command the complete command entered by the user
     * @param tasks the tasks in the list
     * @param taskCount the number of tasks currently stored
     */
    private static void unmarkTask(String command, Task[] tasks, int taskCount) throws BoException {
        String[] commandParts = command.split("\\s+");
        if (commandParts.length != 2) {
            throw new BoException("Please use unmark followed by one task number, e.g. unmark 1.");
        }

        try {
            int taskIndex = Integer.parseInt(commandParts[1]) - 1;
            if (taskIndex < 0 || taskIndex >= taskCount) {
                throw new BoException("I couldn't find a task with that number.");
            }

            tasks[taskIndex].unmarkAsDone();
            System.out.println(" OK, I've marked this task as not done yet:");
            System.out.println("   " + tasks[taskIndex]);
        } catch (NumberFormatException exception) {
            throw new BoException("The task number must be a whole number.");
        }
    }
}
