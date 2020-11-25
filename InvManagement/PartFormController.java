package InvManagement;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * PartFormController is responsible for the part form pane of the inventory management program.
 * <p>
 * The fields for the part form include:
 * <ul>
 * <li>Radio buttons to toggle whether the part is In-House or Outsourced; determines value of conditional field</li>
 * <li>Integer ID: the part ID</li>
 * <li>String Name: the part name</li>
 * <li>Integer Stock: the part stock - how many is currently in inventory; must be within Min and Max range</li>
 * <li>Double Price: the part price, displayed in form "$#.##" for table views</li>
 * <li>Integer Min: the minimum number of parts in stock allowed</li>
 * <li>Integer Max: the maximum number of parts in stock allowed</li>
 * <li>Conditional field: Integer Machine ID when part is In-House and String Company Name when part is Outsourced</li>
 * </ul>
 * <p>
 * Part data will be validated and change MainController's inventory upon pressing the "Save" button.
 * @author Lee Rhodes
 */
public class PartFormController {
    /**
     * Controller from which the part form view was called; holds inventory data.
     */
    private MainController mainController;

    /**
     * Determines whether or not adding or updating a part.
     */
    private actionType action;
    /**
     * Used for updates -- part passed from MainController that we are updating.
     */
    private Part existingPart;

    /**
     * Used to generate ID for new parts.
     */
    private static final AtomicInteger generateID = new AtomicInteger(1);

    /**
     * Shows any errors that occur when validating input.
     */
    @FXML
    private Label errorLabel;

    /**
     * Waits for when user wants to exit the part form and return to main menu.
     */
    @FXML
    private Button cancelBtn;

    /**
     * Changes depending on if updating or adding a part.
     */
    @FXML
    private Label partFormLabel;
    /**
     * Outsourced parts have Company Name field; InHouse parts have Machine ID field.
     */
    @FXML
    private Label conditionalLabel;

    /**
     * Toggle group for user to select whether part is In-House or Outsourced.
     */
    @FXML
    private ToggleGroup partFormRadioBtns;
    /**
     * Radio button selected when part is In-House; Only one radio selected at a time.
     */
    @FXML
    private RadioButton inHouseRadioBtn;
    /**
     * Radio button selected when part is Outsourced; Only one radio selected at a time.
     */
    @FXML
    private RadioButton outSourcedRadioBtn;

    /**
     * Disabled; required; equal to existing part's ID for updates or generated for additions.
     */
    @FXML
    private TextField idTextField;
    /**
     * Holds user input for part name; required.
     */
    @FXML
    private TextField nameTextField;
    /**
     * Holds user input for part stock; required; must be Integer between/equal to min and max.
     */
    @FXML
    private TextField stockTextField;
    /**
     * Holds user input for part price; required; must be a Double.
     */
    @FXML
    private TextField priceTextField;
    /**
     * Holds user input for part minimum; required; must be Integer and less than max.
     */
    @FXML
    private TextField minTextField;
    /**
     * Holds user input for part maximum; required; must be Integer and greater than min.
     */
    @FXML
    private TextField maxTextField;
    /**
     * Holds user input for Machine ID (for In-House parts) or Company Name (for Outsourced parts).
     */
    @FXML
    private TextField conditionalTextField;

    /**
     * initialize() is responsible for making sure the part form pane matches the action selected from the main menu.
     * <p>
     * If updating an existing part, that part's data is filled into the text fields within fillExistingPart().
     * If adding a new part, the radio button will toggle In-House.
     */
    public void initialize() {
        Platform.runLater(() -> {
            switch (action) {
                case ADD: partFormLabel.setText("Add Part");
                    break;
                case UPDATE: partFormLabel.setText("Update Part");
                    fillExistingPart();
                    break;
                default: Stage stage = (Stage) cancelBtn.getScene().getWindow();
                    stage.close();
            }

            toggleRadioBtns();
        });
    }

    /**
     * fillExistingPart() inserts the existing part's data into the text fields and toggles radio button.
     * This existing part is whatever part was passed from MainController for updates.
     */
    public void fillExistingPart() {
        if (existingPart.getClass().getSimpleName().matches("InHouse")) {
            inHouseRadioBtn.setSelected(true);
            outSourcedRadioBtn.setSelected(false);
            conditionalLabel.setText("Machine ID");
            conditionalTextField.setText(String.valueOf(((InHouse)existingPart).getMachineID()));
            conditionalTextField.setPromptText("Enter machine ID");
        } else {
            inHouseRadioBtn.setSelected(false);
            outSourcedRadioBtn.setSelected(true);
            conditionalLabel.setText("Company Name");
            conditionalTextField.setText(((Outsourced)existingPart).getCompanyName());
            conditionalTextField.setPromptText("Enter company name");
        }

        idTextField.setText(String.valueOf(existingPart.getId()));
        nameTextField.setText(existingPart.getName());
        stockTextField.setText(String.valueOf(existingPart.getStock()));
        priceTextField.setText(String.valueOf(existingPart.getPrice()));
        minTextField.setText(String.valueOf(existingPart.getMin()));
        maxTextField.setText(String.valueOf(existingPart.getMax()));
    }

    /**
     * toggleRadioBtns() listens for changes to the radio button group.
     * <p>
     * If In-House radio is selected, the conditional field changes to display Machine ID.
     * If Outsourced radio is selected, the conditional field changes to display Company Name.
     */
    public void toggleRadioBtns() {
        partFormRadioBtns.selectedToggleProperty().addListener((observable, t1, t2) -> {
            RadioButton label = (RadioButton) t2.getToggleGroup().getSelectedToggle();
            /*  Check which radio button is selected & change conditional field label to match selection;
                If it's In-House, user needs to input a machine ID; otherwise, needs to input company name;
                also changes how to validate conditional field (int or string, respectively) */
            if (label.getText().matches("In-House")) {
                conditionalLabel.setText("Machine ID");
                conditionalTextField.setPromptText("Enter machine ID");
            } else {
                conditionalLabel.setText("Company Name");
                conditionalTextField.setPromptText("Enter company name");
            }
        });
    }

    /**
     * addUpdatePart() is called whenever the "Save" button is pressed on the part form pane.
     * <p>
     * First, it uses isInputValid() to make sure all fields fit business rules and expected data types.
     * Next, it calls either addPart() or updatePart() depending on which process was selected from MainController.
     * Finally, it exits the part form pane and returns to main inventory menu, which will show data updates.
     */
    public void addUpdatePart() {
        if (isInputValid()) {
            switch (action) {
                case ADD: addPart(fillPartData());
                    break;
                case UPDATE: updatePart(fillPartData());
                    break;
                default: Stage stage = (Stage) cancelBtn.getScene().getWindow();
                    stage.close();
            }

            exitForm();
        }
    }

    /**
     * isInputValid() checks all input in the part form's text fields to ensure they are valid according to business
     * rules and expected data types.
     * These requirements include:
     * <ul>
     * <li>No field can be empty</li>
     * <li>Stock, Min, and Max must be an integer.</li>
     * <li>Price must be a double.</li>
     * <li>Stock must be greater than or equal to Min.</li>
     * <li>Stock must be less than or equal to Max.</li>
     * <li>Min must be less than or equal to Max.</li>
     * <li>If In-House, Machine ID must be an integer.</li>
     * </ul>
     * If any errors are found, they are collected and displayed to the user in a label below the fields.
     *
     * @return whether or not the input is valid according to business rules and expected data types.
     */
    public boolean isInputValid() {
        String errors = "";
        boolean valid = true;

        if ( nameTextField.getText().isEmpty() || stockTextField.getText().isEmpty()
                || priceTextField.getText().isEmpty() || maxTextField.getText().isEmpty()
                || minTextField.getText().isEmpty() || conditionalTextField.getText().isEmpty()) {
            valid = false;
            errors += "All fields are required.";
        }

        try {
            Integer.parseInt(stockTextField.getText());
            Integer.parseInt(minTextField.getText());
            Integer.parseInt(maxTextField.getText());
            Double.parseDouble(priceTextField.getText());

            if ( Integer.parseInt(minTextField.getText()) > Integer.parseInt(maxTextField.getText()) ) {
                valid = false;
                if (!errors.isEmpty()) { errors += "\n"; }
                errors += "Min cannot be greater than max.";
            } else if ( Integer.parseInt(stockTextField.getText()) > Integer.parseInt(maxTextField.getText()) ) {
                valid = false;
                if (!errors.isEmpty()) { errors += "\n"; }
                errors += "Inventory cannot be greater than max.";
            } else if ( Integer.parseInt(stockTextField.getText()) < Integer.parseInt(minTextField.getText()) ) {
                valid = false;
                if (!errors.isEmpty()) { errors += "\n"; }
                errors += "Inventory cannot be less than min.";
            }

        } catch ( NumberFormatException e) {
            valid = false;
            if (!errors.isEmpty()) { errors += "\n"; }
            errors += "Inventory fields (inv, min, max) and price must be a digit.";
        }

        if ( conditionalLabel.getText().matches("Machine ID")) {
            try {
                Integer.parseInt(conditionalTextField.getText());
            } catch ( NumberFormatException e ) {
                valid = false;
                if (!errors.isEmpty()) { errors += "\n"; }
                errors += "Machine ID must be a digit.";
            }
        }

        errorLabel.setText(errors);
        return valid;
    }

    /**
     * fillPartData() moves all form field input into a Part object that will be inserted into the main inventory
     * as a new part or as an update to an existing part.
     * This function is private so that it can only be called from addUpdatePart within this class. That way,
     * it will ALWAYS be called after a call to isInputValid().
     *
     * @return part object created from form inputs
     */
    private Part fillPartData() {
        Part newPart;

        if ( conditionalLabel.getText().matches("Machine ID")) {
            newPart = new InHouse(0, nameTextField.getText(), Double.parseDouble(priceTextField.getText()),
                    Integer.parseInt(stockTextField.getText()), Integer.parseInt(minTextField.getText()),
                    Integer.parseInt(maxTextField.getText()), Integer.parseInt(conditionalTextField.getText()));
        } else {
            newPart = new Outsourced(0, nameTextField.getText(), Double.parseDouble(priceTextField.getText()),
                    Integer.parseInt(stockTextField.getText()), Integer.parseInt(minTextField.getText()),
                    Integer.parseInt(maxTextField.getText()), conditionalTextField.getText());
        }

        return newPart;
    }

    /**
     * updatePart() makes changes to an existing part in the main inventory.
     * This function is private so that it can only be called from addUpdatePart within this class. That way,
     * it will ALWAYS be called after a call to isInputValid().
     * Additionally, products in the inventory are also scanned to find any products that may contain the associated
     * part that is being updated. If it has that part, it is deleted and the new updated part is added. This is a key
     * spot for optimization work.
     *
     * @param newPart part with changes
     */
    private void updatePart(Part newPart) {
        newPart.setId(existingPart.getId());

        mainController.getInventory().updatePart(mainController.getInventory().getAllParts().indexOf(existingPart), newPart);

        for (Product product : mainController.getInventory().getAllProducts() ) {
            while ( product.getAllAssociatedParts().contains(existingPart) ) {
                product.deleteAssociatedPart(existingPart);
                product.addAssociatedPart(newPart);
            }
        }
    }

    /**
     * addPart() adds a new part to the main inventory.
     * This function is private so that it can only be called from addUpdatePart within this class. That way,
     * it will ALWAYS be called after a call to isInputValid().
     *
     * @param newPart part to be added
     */
    private void addPart(Part newPart) {
        int newID = generateID.getAndIncrement();
        while (mainController.getInventory().lookupPart(newID) != null) { newID = generateID.getAndIncrement(); }
        newPart.setId(newID);

        mainController.getInventory().addPart(newPart);
    }

    /**
     * Setter for MainController mainController.
     * Used to allow access to main inventory from this view.
     *
     * @param mainController the main controller to set
     */
    public void setMainController(MainController mainController) { this.mainController = mainController; }

    /**
     * Setter for Part existingPart.
     * Used to allow MainController to pass in part that user wants to make changes to.
     *
     * @param existingPart the part to set
     */
    public void setExistingPart(Part existingPart) { this.existingPart = existingPart; }

    /**
     * Setter for actionType action.
     * Used to allow MainController to tell view whether or not user is adding or updating a part.
     *
     * @param action the actionType to set
     */
    public void setAction(actionType action) { this.action = action; }

    /**
     * exitForm() is used to close current window/view and return to MainController view (main menu).
     */
    public void exitForm() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}
