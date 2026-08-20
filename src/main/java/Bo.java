import java.util.Scanner;

/**
 * The main entry point for the Bo chatbot.
 */
public class Bo {
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
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            if (command.equals("bye")) {
                System.out.println(separator);
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(separator);
                break;
            }

            System.out.println(separator);
            System.out.println(" " + command);
            System.out.println(separator);
        }
    }
}
