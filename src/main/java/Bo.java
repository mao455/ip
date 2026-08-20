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
        String[] tasks = new String[MAX_TASKS];
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
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + ". " + tasks[i]);
                }
            } else {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println(" added: " + command);
            }

            System.out.println(separator);
        }
    }
}
