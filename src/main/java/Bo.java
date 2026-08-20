import java.util.Scanner;

/**
 * The main entry point for the Bo chatbot.
 */
public class Bo {
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        String banner = " ____        \n"
                + "| __ )  ___  \n"
                + "|  _ \\ / _ \\ \n"
                + "| |_) | (_) |\n"
                + "|____/ \\___/\n";
        String separator = "____________________________________________________________";

        System.out.println(separator);
        System.out.print(banner);
        System.out.println("Hello! I'm Bo.");
        System.out.println("What can I do for you?");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            if (command.equals("bye")) {
                System.out.println(separator);
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(separator);
                break;
            }

            System.out.println(separator);

            if (command.equals("list")) {
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + "." + tasks[i]);
                }
            } else if (command.equals("mark") || command.startsWith("mark ")) {
                markTask(command, tasks, taskCount);
            } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                unmarkTask(command, tasks, taskCount);
            } else {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println(" added: " + command);
            }

            System.out.println(separator);
        }
    }

    /**
     * Marks the task at the index specified by a {@code mark} command as done.
     *
     * @param command the complete command entered by the user
     * @param tasks the tasks in the list
     * @param taskCount the number of tasks currently stored
     */
    private static void markTask(String command, Task[] tasks, int taskCount) {
        String[] commandParts = command.split("\\s+");
        if (commandParts.length != 2) {
            System.out.println(" Invalid mark command. Please specify a task number.");
            return;
        }

        try {
            int taskIndex = Integer.parseInt(commandParts[1]) - 1;
            if (taskIndex < 0 || taskIndex >= taskCount) {
                System.out.println(" Invalid task number.");
                return;
            }

            tasks[taskIndex].markAsDone();
            System.out.println(" Nice! I've marked this task as done:");
            System.out.println("   " + tasks[taskIndex]);
        } catch (NumberFormatException exception) {
            System.out.println(" Invalid task number.");
        }
    }

    /**
     * Marks the task at the index specified by an {@code unmark} command as not done.
     *
     * @param command the complete command entered by the user
     * @param tasks the tasks in the list
     * @param taskCount the number of tasks currently stored
     */
    private static void unmarkTask(String command, Task[] tasks, int taskCount) {
        String[] commandParts = command.split("\\s+");
        if (commandParts.length != 2) {
            System.out.println(" Invalid unmark command. Please specify a task number.");
            return;
        }

        try {
            int taskIndex = Integer.parseInt(commandParts[1]) - 1;
            if (taskIndex < 0 || taskIndex >= taskCount) {
                System.out.println(" Invalid task number.");
                return;
            }

            tasks[taskIndex].unmarkAsDone();
            System.out.println(" OK, I've marked this task as not done yet:");
            System.out.println("   " + tasks[taskIndex]);
        } catch (NumberFormatException exception) {
            System.out.println(" Invalid task number.");
        }
    }
}
