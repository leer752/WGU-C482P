package InvManagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Performance Assessment: Software I - QKM1
 * <p>
 * Course: Software I, C482
 * <p>
 * Student ID: #001443578
 * <p>
 * This is the Main class -- basically, where everything starts! When the program is ran, it starts from here and branches
 * into each view as they are called. When everything is done, it returns to Main and closes the program.
 * <p>
 * Question: "What's a compatible feature suitable to your application that would extend functionality to the next
 * version if you were to update the application?"
 * <p>
 * Answer: I would add a "Quantity" column to the associated parts table view on the product form. Since you can associate
 * the same part more than once, it would be cleaner to condense copies of a part into one row. Additionally, I would
 * include the option when adding an associated part to add multiple at a time. For instance, there could be a new
 * button that says "Add Multiple" and user would input a quantity of that part to associate. That way, the user wouldn't
 * have to click "Add" 20 times if they wanted to associate 20 of a certain part.
 *
 * @author Lee Rhodes
 */
public class Main extends Application {

    /**
     * @param stage the window that will open for the user first; populated with MainForm.fxml
     * @throws Exception catches any issues that may arise when compiling the fxml and popping up the window.
     */
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainForm.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        stage.setTitle("Inventory Management System");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args any command-line arguments that may have been passed when starting the program
     */
    public static void main(String[] args) {
        launch(args);
    }
}
