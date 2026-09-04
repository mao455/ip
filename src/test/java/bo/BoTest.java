package bo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests command execution through Bo's interface-independent application logic. */
class BoTest {
    /** The storage file used by the application when tests run from the project root. */
    private final Path taskFile = Path.of("data", "duke.txt").toAbsolutePath().normalize();

    /** The original storage file contents, restored after each test. */
    private byte[] originalContents;

    /** Whether the storage file existed before the test started. */
    private boolean fileExisted;

    /** Preserves the user's task data and starts each test without a storage file. */
    @BeforeEach
    void preserveStorageFile() throws IOException {
        fileExisted = Files.exists(taskFile);
        originalContents = fileExisted ? Files.readAllBytes(taskFile) : null;
        Files.deleteIfExists(taskFile);
    }

    /** Restores the user's task data after the test has completed. */
    @AfterEach
    void restoreStorageFile() throws IOException {
        if (fileExisted) {
            Files.write(taskFile, originalContents);
        } else {
            Files.deleteIfExists(taskFile);
        }
    }

    /** Verifies that GUI-style command execution mutates and displays task data. */
    @Test
    void executeCommand_taskLifecycle_updatesTaskResponses() throws BoException {
        Bo bo = new Bo();

        String addResponse = bo.executeCommand("todo read book");
        String markResponse = bo.executeCommand("mark 1");
        String listResponse = bo.executeCommand("list");
        String deleteResponse = bo.executeCommand("delete 1");

        assertAll(
                () -> assertTrue(addResponse.contains("I've added this task")),
                () -> assertTrue(markResponse.contains("marked this task as done")),
                () -> assertTrue(listResponse.contains("[T][X] read book")),
                () -> assertTrue(deleteResponse.contains("I've removed this task")),
                () -> assertTrue(bo.executeCommand("list").endsWith("list:")));
    }
}
