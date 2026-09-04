package bo.gui;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/** Verifies that the FXML views needed by the JavaFX application are packaged. */
class GuiResourceTest {
    /** Verifies that the main and dialog FXML files are available at runtime. */
    @Test
    void guiResources_arePackaged() {
        assertNotNull(Main.class.getResource("/view/MainWindow.fxml"));
        assertNotNull(Main.class.getResource("/view/DialogBox.fxml"));
        assertNotNull(Main.class.getResource("/view/main.css"));
    }
}
