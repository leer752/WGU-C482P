package InvManagement;

import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ProductFormController is responsible for the product form pane of the inventory management program.
 * <p>
 * The fields for the product form include:
 * <ul>
 * <li>Integer ID: the product ID</li>
 * <li>String Name: the product name</li>
 * <li>Integer Stock: the product stock - how many is currently in inventory; must be within Min and Max range</li>
 * <li>Double Price: the product price, displayed in form "$#.##" for table views</li>
 * <li>Integer Min: the minimum number of product in stock allowed</li>
 * <li>Integer Max: the maximum number of product in stock allowed</li>
 * <li>Array Associated Parts: parts from main inventory to associate with current product</li>
 * </ul>
 * <p>
 * Form pane also has two table views for all parts from main inventory and associated parts with current product.
 * User can select parts in the partTableView and add them to the associatedPartsTableView by selecting the "Add" button.
 * User can also remove parts from the associatedPartsTableView by selecting the "Remove associated part" button.
 * Product data will be validated and change MainController's inventory upon pressing the "Save" button.
 * @author Lee Rhodes
 */
public class ProductFormController {
    /**
     * Format for price double in table view; displayed as "$#.##".
     */
    NumberFormat priceFormat = NumberFormat.getCurrencyInstance(Locale.US);

    /**
     * Controller from which the product form view was called; holds inventory data.
     */
    private MainController mainController;

    /**
     * Determines whether or not adding or updating a product.
     */
    private actionType action;

    /**
     * Used for updates -- product passed from MainController that we are updating.
     */
    private Product existingProduct;

    /**
     * Used to generate ID for new products.
     */
    private static final AtomicInteger generateID = new AtomicInteger(1);

    /**
     * Shows any errors that occur when validating input.
     */
    @FXML
    private Label errorLabel;

    /**
     * Waits for when user wants to exit the product form and return to main menu.
     */
    @FXML
    private Button cancelBtn;

    /**
     * Changes depending on if updating or adding a product.
     */
    @FXML
    private Label productFormLabel;


    /**
     * Disabled; required; equal to existing product's ID for updates or generated for additions.
     */
    @FXML
    private TextField idTextField;
    /**
     * Holds user input for product name; required.
     */
    @FXML
    private TextField nameTextField;
    /**
     * Holds user input for product stock; required; must be Integer between/equal to min and max.
     */
    @FXML
    private TextField stockTextField;
    /**
     * Holds user input for product price; required; must be a Double.
     */
    @FXML
    private TextField priceTextField;
    /**
     * Holds user input for product minimum; required; must be Integer and less than max.
     */
    @FXML
    private TextField minTextField;
    /**
     * Holds user input for product maximum; required; must be Integer and greater than min.
     */
    @FXML
    private TextField maxTextField;

    /**
     * Holds user input when searching for part to add to associated parts; each character typed calls method to filter parts.
     */
    @FXML
    private TextField partSearchTextField;
    /**
     * Table view to show all parts from inventory (filtered based on search field).
     */
    @FXML
    private TableView<Part> partTableView;
    /**
     * Table column for part ID.
     */
    @FXML
    private TableColumn<Part, Integer> partIDCol;
    /**
     * Table column for part name.
     */
    @FXML
    private TableColumn<Part, String> partNameCol;
    /**
     * Table columns for part stock.
     */
    @FXML
    private TableColumn<Part, Integer> partStockCol;
    /**
     * Table column for part price; displayed as "$#.##".
     */
    @FXML
    private TableColumn<Part, Double> partPriceCol;

    /**
     * Table view to show all associated parts for product that is being added/updated.
     */
    @FXML
    private TableView<Part> associatedPartsTableView;
    /**
     * Table column for associated part ID.
     */
    @FXML
    private TableColumn<Part, Integer> associatedPartIDCol;
    /**
     * Table column for associated part name.
     */
    @FXML
    private TableColumn<Part, String> associatedPartNameCol;
    /**
     * Table column for associated part stock.
     */
    @FXML
    private TableColumn<Part, Integer> associatedPartStockCol;
    /**
     * Table column for associated part price.
     */
    @FXML
    private TableColumn<Part, Double> associatedPartPriceCol;

    /**
     * initialize() is responsible for making sure the new product object is instantiated whenever the view starts.
     * <p>
     * If it's not instantiated, errors will occur when attempting to access associated parts.
     * <p>
     * If updating an existing product, that product's data is filled into the text fields within fillExistingProduct().
     * <p>
     * If adding a new product, all fields will still be null.
     */
    public void initialize() {
        if ( this.existingProduct == null ) {
            this.existingProduct = new Product(0, "", 0, 0, 0, 0);
        }

        Platform.runLater(() -> {
            switch (action) {
                case ADD: productFormLabel.setText("Add Product");
                          break;
                case UPDATE: productFormLabel.setText("Update Product");
                             fillExistingProduct();
                             break;
                default: Stage stage = (Stage) cancelBtn.getScene().getWindow();
                         stage.close();
            }

            fillPartTableView();
            fillAssociatedPartsTableView();
        });
    }

    /**
     * fillExistingProduct() inserts the existing product's data into the text fields.
     * This existing product is whatever product was passed from MainController for updates.
     */
    public void fillExistingProduct() {
        idTextField.setText(String.valueOf(existingProduct.getId()));
        nameTextField.setText(existingProduct.getName());
        stockTextField.setText(String.valueOf(existingProduct.getStock()));
        priceTextField.setText(String.valueOf(existingProduct.getPrice()));
        minTextField.setText(String.valueOf(existingProduct.getMin()));
        maxTextField.setText(String.valueOf(existingProduct.getMax()));
    }

    /**
     * fillPartTableView() tells the partTableView what type of data to expect, how to display the price field, and
     * sets the data in the table according to a filtered list that is pulled from filterParts().
     */
    public void fillPartTableView() {
        // Telling each partTableView column what type of data to expect
        partIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        partStockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Uses priceFormat declared previously to format price double values; displays as "$#.##"
        partPriceCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if ( empty ) { setText(null); }
                else { setText(priceFormat.format(price)); }
            }
        });

        /* Set items in partTableView equal to a filtered list of parts in inventory;
           Filtered list only returns matches for the part search bar which is in filterParts() below */
        partTableView.setItems(filterParts());
    }

    /**
     * filterParts() listens for when the search text field input changes and filters the list of parts based on what
     * matches the input.
     * <p>
     * Digit-only input will search for parts with a matching ID; otherwise, it searches for parts with a name that
     * begins with or equals the input.
     *
     * @return the list of parts that have a match with the input in the search text field
     */
    public FilteredList<Part> filterParts() {
        // Filtered list expands off of all parts in inventory
        FilteredList<Part> filteredParts = new FilteredList<>(mainController.getInventory().getAllParts());

        /* Adding a listener to watch whenever the search text field input is changed & react
           NOTE: Could simplify if statement to just return boolean value of matches, but decided to separate it
                 out into a long if-statement for readability */
        partSearchTextField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredParts.setPredicate(part -> {
                    if( newValue == null || newValue.isEmpty()) { // If search text is empty, show all parts
                        return true;
                    } else if ( newValue.matches("[0-9]*") // If search text is an integer, can search for matches by ID
                            && part == mainController.getInventory().lookupPart(Integer.parseInt(newValue)) ) {
                        return true;
                    } else if ( mainController.getInventory().lookupPart(newValue) != null
                            && mainController.getInventory().lookupPart(newValue).contains(part) ) {
                /* For all search text, find matches for all parts that begin with and/or equal search text;
                   NOTE: Assumption made that no parts have a name of only numbers */
                        return true;
                    }
                    return false; // Not a match, leave part out of filter.
                }));

        // Sending back the filtered list so it can populate the partTableView
        return filteredParts;
    }

    /**
     * fillAssociatedPartsTableView() tells the associatedPartsTableView what type of data to expect,
     * how to display the price field, and sets the data in the table according to the associated parts array stored
     * in the current product object.
     */
    public void fillAssociatedPartsTableView() {
        associatedPartIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        associatedPartNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        associatedPartStockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        associatedPartPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        associatedPartPriceCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if ( empty ) { setText(null); }
                else { setText(priceFormat.format(price)); }
            }
        });

        associatedPartsTableView.setItems(existingProduct.getAllAssociatedParts());
    }

    /**
     * Copy the selected part from partTableView into the associated parts array for the current product object.
     * <p>
     * New associated part will be updated on the associatedPartsTableView.
     * <p>
     * NOTE: If user cancels changes after adding an associated part, product will not be changed in main inventory and
     * associated part additions won't be reflected.
     */
    public void addAssociatedPart() {
        String errors = "";
        Part selectedPart = partTableView.getSelectionModel().getSelectedItem();

        if ( selectedPart != null ) {
            this.existingProduct.addAssociatedPart(selectedPart);
        } else {
            errors = "A part must be selected to add it to associated parts.";
        }
        errorLabel.setText(errors);
    }

    /**
     * deleteAssociatedPart() deletes the selected part from the associated parts array in the current product object.
     * <p>
     * An alert appears to receive user confirmation first before deleting the associated part.
     * <p>
     * If no part is selected or the user declines confirmation, nothing happens and method is exited.
     * <p>
     * Deleting associated part does not remove it from the main inventory.
     */
    public void deleteAssociatedPart() {
        String errors = "";
        Part selectedPart = associatedPartsTableView.getSelectionModel().getSelectedItem();

        if ( selectedPart != null ) {
            Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDelete.setTitle("Confirmation Dialog");
            confirmDelete.setHeaderText("Deleting the part will de-associate from this product. It will still be in"
                                        + " the inventory.");
            confirmDelete.setContentText("Are you sure you want to remove this part?");


            confirmDelete.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

            Optional<ButtonType> result = confirmDelete.showAndWait();
            if (result.get() == ButtonType.YES) {
                this.existingProduct.deleteAssociatedPart(selectedPart);
            }
        } else {
            errors = "An associated part must be selected to remove it.";
        }
        errorLabel.setText(errors);
    }

    /**
     * addUpdateProduct() is called whenever the "Save" button is pressed on the product form pane.
     * <p>
     * First, it uses isInputValid() to make sure all fields fit business rules and expected data types.
     * Next, it calls either addProduct() or updateProduct() depending on which process was selected from MainController.
     * Finally, it exits the product form pane and returns to main inventory menu, which will show data updates.
     */
    public void addUpdateProduct() {
        if (isInputValid()) {
            this.existingProduct.setName(nameTextField.getText());
            this.existingProduct.setPrice(Double.parseDouble(priceTextField.getText()));
            this.existingProduct.setStock(Integer.parseInt(stockTextField.getText()));
            this.existingProduct.setMin(Integer.parseInt(minTextField.getText()));
            this.existingProduct.setMax(Integer.parseInt(maxTextField.getText()));

            switch ( action ) {
                case ADD: addProduct();
                    break;
                case UPDATE: updateProduct();
                    break;
                default: Stage stage = (Stage) cancelBtn.getScene().getWindow();
                    stage.close();
            }

            exitForm();
        }
    }

    /**
     * isInputValid() checks all input in the product form's text fields to ensure they are valid according to business
     * rules and expected data types.
     * <p>
     * These requirements include:
     * <ul>
     * <li>No field can be empty</li>
     * <li>Stock, Min, and Max must be an integer.</li>
     * <li>Price must be a double.</li>
     * <li>Stock must be greater than or equal to Min.</li>
     * <li>Stock must be less than or equal to Max.</li>
     * <li>Min must be less than or equal to Max.</li>
     * </ul>
     * <p>
     * If any errors are found, they are collected and displayed to the user in a label below the fields.
     *
     * @return whether or not the input is valid according to business rules and expected data types.
     */
    public boolean isInputValid() {
        String errors = "";
        boolean valid = true;

        if ( nameTextField.getText().isEmpty() || stockTextField.getText().isEmpty()
                || priceTextField.getText().isEmpty() || maxTextField.getText().isEmpty()
                || minTextField.getText().isEmpty()) {
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

        errorLabel.setText(errors);
        return valid;
    }

    /**
     * updateProduct() makes changes to an existing product in the main inventory.
     * This function is private so that it can only be called from addUpdateProduct within this class. That way,
     * it will ALWAYS be called after a call to isInputValid().
     */
    private void updateProduct() {
        int index = mainController.getInventory().getAllProducts().indexOf(existingProduct);
        mainController.getInventory().updateProduct(index, existingProduct);
    }

    /**
     * addProduct() adds a new product to the main inventory.
     * This function is private so that it can only be called from addUpdateProduct within this class. That way,
     * it will ALWAYS be called after a call to isInputValid().
     */
    private void addProduct() {
        int newID = generateID.getAndIncrement();
        while (mainController.getInventory().lookupProduct(newID) != null) { newID = generateID.getAndIncrement(); }
        this.existingProduct.setId(newID);
        mainController.getInventory().addProduct(existingProduct);
    }

    /**
     * Setter for MainController mainController.
     * Used to allow access to main inventory from this view.
     *
     * @param mainController the main controller to set
     */
    public void setMainController(MainController mainController) { this.mainController = mainController; }

    /**
     * Setter for Product existingProduct.
     * Used to allow MainController to pass in product that user wants to make changes to.
     *
     * @param existingProduct the product to set
     */
    public void setExistingProduct(Product existingProduct) { this.existingProduct = existingProduct; }

    /**
     * Setter for actionType action.
     * Used to allow MainController to tell view whether or not user is adding or updating a product.
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
