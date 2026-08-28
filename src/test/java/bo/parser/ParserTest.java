package bo.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import bo.BoException;
import bo.model.Deadline;
import bo.model.Event;
import bo.model.Task;
import bo.model.Todo;

/** Tests the command parsing and validation rules used by Bo. */
class ParserTest {
    /** The stateless parser shared by these tests. */
    private final Parser parser = new Parser();

    /** Verifies that a list command produces a list command with no task data. */
    @Test
    void parse_listCommand_returnsListCommand() throws BoException {
        Parser.Command command = parser.parse("list");

        assertAll(
                () -> assertEquals(Parser.Type.LIST, command.type()),
                () -> assertEquals(-1, command.taskIndex()),
                () -> assertNull(command.task()));
    }

    /** Verifies that task mutation commands convert one-based input to zero-based indexes. */
    @Test
    void parse_taskMutationCommands_returnsZeroBasedIndex() throws BoException {
        assertAll(
                () -> assertMutation("delete 1", Parser.Type.DELETE, 0),
                () -> assertMutation("mark 2", Parser.Type.MARK, 1),
                () -> assertMutation("unmark 3", Parser.Type.UNMARK, 2));
    }

    /** Verifies that each add command creates the correct task type and fields. */
    @Test
    void parse_addCommands_createsExpectedTasks() throws BoException {
        Parser.Command todoCommand = parser.parse("todo read book");
        Parser.Command deadlineCommand = parser.parse("deadline return book /by 2019-10-15");
        Parser.Command eventCommand = parser.parse(
                "event project meeting /from 2019-10-15 /to 2019-10-16 0905");

        Todo todo = assertInstanceOf(Todo.class, todoCommand.task());
        Deadline deadline = assertInstanceOf(Deadline.class, deadlineCommand.task());
        Event event = assertInstanceOf(Event.class, eventCommand.task());
        assertAll(
                () -> assertEquals(Parser.Type.ADD, todoCommand.type()),
                () -> assertEquals("read book", todo.getDescription()),
                () -> assertEquals("return book", deadline.getDescription()),
                () -> assertEquals("Oct 15 2019", deadline.getDisplayBy()),
                () -> assertEquals("project meeting", event.getDescription()),
                () -> assertEquals("Oct 15 2019", event.getDisplayFrom()),
                () -> assertEquals("Oct 16 2019 09:05", event.getDisplayTo()));
    }

    /** Verifies that malformed commands return the documented user-facing errors. */
    @Test
    void parse_invalidCommands_throwsExpectedErrors() {
        assertAll(
                () -> assertParsingFails("", "Please enter a command instead of an empty line."),
                () -> assertParsingFails("blah", "I'm sorry, but I don't know what that means :-("),
                () -> assertParsingFails("delete", "Please use delete followed by one task number, e.g. delete 1."),
                () -> assertParsingFails("mark abc", "The task number must be a whole number."),
                () -> assertParsingFails("todo", "The description of a todo cannot be empty."),
                () -> assertParsingFails("deadline return book",
                        "A deadline must include a /by date, e.g. deadline return book /by Friday."),
                () -> assertParsingFails("deadline /by Friday",
                        "A deadline must include a /by date, e.g. deadline return book /by Friday."),
                () -> assertParsingFails("event project meeting /from Monday",
                        "An event must include a /to time."),
                () -> assertParsingFails("event project meeting /to Tuesday /from Monday",
                        "Please put the /from time before the /to time."));
    }

    /** Asserts the command type and zero-based index of a mutation command. */
    private void assertMutation(String input, Parser.Type expectedType, int expectedIndex)
            throws BoException {
        Parser.Command command = parser.parse(input);

        assertEquals(expectedType, command.type());
        assertEquals(expectedIndex, command.taskIndex());
        assertNull(command.task());
    }

    /** Asserts that parsing fails with a particular user-facing message. */
    private void assertParsingFails(String input, String expectedMessage) {
        BoException exception = assertThrows(BoException.class, () -> parser.parse(input));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
