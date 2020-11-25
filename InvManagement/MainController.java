package InvManagement;

import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

/**
 * MainController is responsible for the main menu of the inventory management program.
 * It has two table views for parts and products, and each table view can be filtered with a search bar.
 * Parts and products can be added, modified, or deleted from this view by pressing their respective buttons.
 * @author Lee Rhodes
 */
public class MainController {
    /**
     * Format for price double in table view; displayed as "$#.##".
     */
    NumberFormat priceFormat = NumberFormat.getCurrencyInstance(Locale.US);

    /**
     * Main inventory object; all parts and products are in an array list in this object.
     */
    private Inventory inventory;

    /**
     * Waits for when user wants to exit main menu and end program.
     */
    @FXML
    private Button exitBtn;

    /**
     * Shows any errors that occur when selecting parts and products.
     */
    @FXML
    private Label errorLabel;

    /**
     * Holds user input when searching for parts; each character typed calls method to filter parts.
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
     * Holds user input when searching for products; each character typed calls method to filter products.
     */
    @FXML
    private TextField productSearchTextField;
    /**
     * Table view to show all products from inventory (filtered based on search field).
     */
    @FXML
    private TableView<Product> productTableView;
    /**
     * Table column for product ID.
     */
    @FXML
    private TableColumn<Product, Integer> productIDCol;
    /**
     * Table column for product name.
     */
    @FXML
    private TableColumn<Product, String> productNameCol;
    /**
     * Table columns for product stock.
     */
    @FXML
    private TableColumn<Product, Integer> productStockCol;
    /**
     * Table column for product price; displayed as "$#.##".
     */
    @FXML
    private TableColumn<Product, Double> productPriceCol;

    /**
     * initialize() is responsible for making sure the inventory is instantiated whenever the program starts.
     * Afterwards, it populates the parts and products table views with the arrays in the view's inventory object.
     * <p>
     * Question: "What's a detailed description of a logical or runtime error that you corrected in the code and a
     * detailed description of how you corrected it above the line of code you are referring to?"
     * <p>
     * Answer: When I was just setting up my main view, I kept running into a NullPointerException error whenever I
     * tried to initialize my table views. After research and looking over the books again, I figured out that it
     * was hitting that error because the table views on the GUI were running too early before everything was compiled.
     * I fixed it by adding in the Platform.runLater(() block, which told my program to run the table view GUI
     * methods after everything was compiled. Threading is fun!
     */
    public void initialize() {
        if (this.inventory == null) {
            this.inventory = new Inventory();
        }

        Platform.runLater(() -> {
            fillPartTableView();    // Populates PartTableView
            fillProductTableView(); // Populates ProductTableView
        });
    }

    /**
     * fillPartTableView() tells the partTableView what type of data to expect, how to display the price field, and
     * sets the data in the table according to a filtered list that is pulled from filterParts().
     */
    private void fillPartTableView() {
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
     * Digit-only input will search for parts with a matching ID; otherwise, it searches for parts with a name that
     * begins with or equals the input.
     *
     * @return the list of parts that have a match with the input in the search text field
     */
    private FilteredList<Part> filterParts() {
        // Filtered list expands off of all parts in inventory
        FilteredList<Part> filteredParts = new FilteredList<>(inventory.getAllParts());

        /* Adding a listener to watch whenever the search text field input is changed & react
           NOTE: Could simplify if statement to just return boolean value of matches, but decided to separate it
                 out into a long if-statement for readability */
        partSearchTextField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredParts.setPredicate(part -> {
            if( newValue == null || newValue.isEmpty()) { // If search text is empty, show all parts
                return true;
            } else if ( newValue.matches("[0-9]*") // If search text is an integer, can search for matches by ID
                    && part == inventory.lookupPart(Integer.parseInt(newValue)) ) {
                return true;
            } else if ( inventory.lookupPart(newValue) != null && inventory.lookupPart(newValue).contains(part) ) {
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
     * fillProductTableView() tells the productTableView what type of data to expect, how to display the price field,
     * and sets the data in the table according to a filtered list that is pulled from filterProducts().
     */
    private void fillProductTableView() {
        // Telling each productTableView column what type of data to expect
        productIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        productStockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Uses priceFormat declared previously to format price double values; displays as "$#.##"
        productPriceCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if ( empty ) { setText(null); }
                else { setText(priceFormat.format(price)); }
            }
        });

        /* Set items in productTableView equal to a filtered list of products in inventory;
           Filtered list only returns matches for the product search bar which is in filterProducts() below */
        productTableView.setItems(filterProducts());
    }

    /**
     * filterProducts() listens for when the search text field input changes and filters the list of products based on
     * what matches the input.
     * <p>
     * Digit-only input will search for products with a matching ID; otherwise, it searches for products with a name
     * that begins with or equals the input.
     *
     * @return the list of products that have a match with the input in the search text field
     */
    private FilteredList<Product> filterProducts() {
        // Filtered list expands off of all products in inventory
        FilteredList<Product> filteredProducts = new FilteredList<>(inventory.getAllProducts());

        /* Adding a listener to watch whenever the search text field input is changed & react
           NOTE: Could simplify if statement to just return boolean value of matches, but decided to separate it
                 out into a long if-statement for readability */
        productSearchTextField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredProducts.setPredicate(product -> {
            if( newValue == null || newValue.isEmpty()) { // If search text is empty, show all products
                return true;
            } else if ( newValue.matches("[0-9]*") // If search text is an integer, can search for matches by ID
                    && product == inventory.lookupProduct(Integer.parseInt(newValue)) ) {
                return true;
            } else if ( inventory.lookupProduct(newValue) != null
                    && inventory.lookupProduct(newValue).contains(product) ) {
                /* For all search text, find matches for all products that begin with and/or equal search text;
                   NOTE: Assumption made that no products have a name of only numbers */
                return true;
            }
            return false; // Not a match, leave product out of filter.
        }));

        // Sending back the filtered list so it can populate the productTableView
        return filteredProducts;
    }

    /**
     * getInventory() is used primarily in child views for the part form and product form when they need to pull from
     * the main inventory.
     * <p>
     * An alternative would be to add a separate inventory object in each child view and set it equal to main inventory
     * before calling the child views.
     *
     * @return the inventory
     */
    public Inventory getInventory() { return this.inventory; }

    /**
     * addPart() calls child view PartFormController which is the form to add or modify parts.
     * This method can only be called by pressing the "Add" button on the main form's UI.
     *
     * @throws IOException if issue opening PartForm.fxml
     */
    @FXML
    public void addPart() throws IOException {
        errorLabel.setText("");

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PartForm.fxml"));
        Parent parent = loader.load();
        PartFormController controller = loader.getController();
        controller.setAction(actionType.ADD);
        controller.setMainController(this); /* Pass main controller into part form view so that main's inventory
                                                   can be manipulated from part form controller */

        Scene scene = new Scene(parent);

        stage.setTitle("Add Part"); // Magic variable; opportunity for constant
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * updatePart() calls child view PartFormController which is the form to add or modify parts.
     * This method can only be called by pressing the "Modify" button on the main form's UI.
     *
     * @throws IOException if issue opening PartForm.fxml
     */
    @FXML
    public void updatePart() throws IOException {
        String errors = "";
        Part existingPart = partTableView.getSelectionModel().getSelectedItem();

        // Can only update part if selecting a row in partTableView
        if ( existingPart != null ) {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PartForm.fxml"));
            Parent parent = loader.load();
            PartFormController controller = loader.getController();
            controller.setExistingPart(existingPart); // Passing the part we're modifying to the part form view
            controller.setAction(actionType.UPDATE);
            controller.setMainController(this); /* Pass main controller into part form view so that main's inventory
                                                   can be manipulated from part form controller */

            Scene scene = new Scene(parent);

            stage.setTitle("Update Part"); // Magic variable; opportunity for constant
            stage.setScene(scene);
            stage.showAndWait();
        } else {
            errors = "A part must be selected for modification.";
        }
        errorLabel.setText(errors);
    }

    /**
     * deletePart() deletes the selected part from the main inventory object.
     * <p>
     * An alert appears to receive user confirmation first before deleting the part.
     * <p>
     * If no part is selected or the user declines confirmation, nothing happens and method is exited.
     */
    public void deletePart() {
        String errors = "";
        Part existingPart = partTableView.getSelectionModel().getSelectedItem();

        // Can only delete part if selecting a row in partTableView
        if ( existingPart != null ) {
            // Must confirm that user wants to delete before calling method & making changes
            Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDelete.setTitle("Confirmation Dialog");
            confirmDelete.setHeaderText("Deleting the part will remove it from the inventory and any associated products.");
            confirmDelete.setContentText("Are you sure you want to delete this part?");

            // Using buttons "YES" and "NO" instead of the default buttons for user-friendliness
            confirmDelete.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

            Optional<ButtonType> result = confirmDelete.showAndWait();
            if (result.get() == ButtonType.YES) {
                this.inventory.deletePart(existingPart);
            }
        } else {
            errors = "A part must be selected for deletion.";
        }
        errorLabel.setText(errors);
    }

    /**
     * addProduct() calls child view ProductFormController which is the form to add or modify products.
     * This method can only be called by pressing the "Add" button on the main form's UI.
     *
     * @throws IOException if issue opening ProductForm.fxml
     */
    @FXML
    public void addProduct() throws IOException {
        errorLabel.setText("");

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductForm.fxml"));
        Parent parent = loader.load();
        ProductFormController controller = loader.getController();
        controller.setAction(actionType.ADD);
        controller.setMainController(this); /* Pass main controller into product form view so that main's inventory
                                               can be manipulated from product form controller */

        Scene scene = new Scene(parent);

        stage.setTitle("Add Product"); // Magic variable; opportunity for constant
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * updateProduct() calls child view ProductFormController which is the form to add or modify products.
     * This method can only be called by pressing the "Modify" button on the main form's UI.
     *
     * @throws IOException if issue opening ProductForm.fxml
     */
    @FXML
    public void updateProduct() throws IOException {
        String errors = "";
        Product existingProduct = productTableView.getSelectionModel().getSelectedItem();

        if ( existingProduct != null ) {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductForm.fxml"));
            Parent parent = loader.load();
            ProductFormController controller = loader.getController();
            controller.setExistingProduct(existingProduct);
            controller.setAction(actionType.UPDATE);
            controller.setMainController(this); /* Pass main controller into product form view so that main's inventory
                                                   can be manipulated from product form controller */

            Scene scene = new Scene(parent);

            stage.setTitle("Update Product"); // Magic variable; opportunity for constant
            stage.setScene(scene);
            stage.showAndWait();
        } else {
            errors = "A product must be selected for modification.";
        }
        errorLabel.setText(errors);

    }

    /**
     * deleteProduct() deletes the selected product from the main inventory object.
     * <p>
     * An alert appears to receive user confirmation first before deleting the product.
     * <p>
     * If no product is selected or the user declines confirmation, nothing happens and method is exited.
     * <p>
     * If the selected product has associated parts, the user will receive a warning that it cannot be deleted, then
     * the method is exited.
     */
    public void deleteProduct() {
        String errors = "";
        Product existingProduct = productTableView.getSelectionModel().getSelectedItem();

        // Can only delete product if selecting a row in productTableView AND that product has no associated parts
        if ( existingProduct != null && existingProduct.getAllAssociatedParts().isEmpty()) {
            // Must confirm that user wants to delete before calling method & making changes
            Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDelete.setTitle("Confirmation Dialog");
            confirmDelete.setHeaderText("Deleting the product will remove it from the inventory.");
            confirmDelete.setContentText("Are you sure you want to delete this product?");

            // Using buttons "YES" and "NO" instead of the default buttons for user-friendliness
            confirmDelete.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

            Optional<ButtonType> result = confirmDelete.showAndWait();
            if (result.get() == ButtonType.YES) {
                this.inventory.deleteProduct(existingProduct);
            }
        } else if (existingProduct != null && !existingProduct.getAllAssociatedParts().isEmpty()) {
            // Let user know that product cannot be deleted due to associated parts & exit func without deleting
            Alert deleteError = new Alert(Alert.AlertType.ERROR);
            deleteError.setTitle("Error Dialog");
            deleteError.setHeaderText("Product has associated parts.");
            deleteError.setContentText("Product with associated parts cannot be deleted.");
            deleteError.showAndWait();
        } else {
            errors = "A product must be selected for deletion.";
        }
        errorLabel.setText(errors);
    }

    /**
     * exitBtnListener() listens for when the "Exit" button is pressed. Once it is, it closes the window which will
     * end the program.
     */
    public void exitBtnListener() {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }
}
