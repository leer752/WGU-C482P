package InvManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Inventory is the class that corresponds to our most important object, the inventory of parts and products!
 * <p>
 * It has two array lists - all parts and all products. Whenever parts or products are added or searched for, it looks
 * at this class and its methods.
 * <p>
 * A part or product can be added, updated, deleted, or looked up from inventory. Additionally, the array list of all
 * parts and products can be retrieved using an accessor method. The array lists cannot be set using a mutator method;
 * parts and products must be added one at a time. Multiple additions, updates, or deletions at a time could be added
 * in a future version.
 */
public class Inventory {
    /**
     * Used to hold all parts in the main inventory.
     */
    private ObservableList<Part> allParts = FXCollections.observableArrayList();
    /**
     * Used to hold all products in the main inventory.
     */
    private ObservableList<Product> allProducts = FXCollections.observableArrayList();

    /**
     * @param newPart the part to add to inventory
     */
    public void addPart(Part newPart) {
        this.allParts.add(newPart);
    }

    /**
     * @param newProduct the product to add to inventory
     */
    public void addProduct(Product newProduct) {
        this.allProducts.add(newProduct);
    }

    /**
     * @param partID the id to search for part matches with
     * @return matching part for partID param
     */
    public Part lookupPart(int partID) {
        for ( Part part : allParts ) {
            if ( part.getId() == partID ) {
                return part;
            }
        }
        return null;
    }

    /**
     * @param productID the id to search for product matches with
     * @return matching product for productID param
     */
    public Product lookupProduct(int productID) {
        for ( Product product : allProducts ) {
            if ( product.getId() == productID ) {
                return product;
            }
        }
        return null;
    }

    /**
     * @param partName the name to search for part matches with
     * @return all matches that either start with or equal partName param
     */
    public ObservableList<Part> lookupPart(String partName) {
        ObservableList<Part> parts = FXCollections.observableArrayList();
        for ( Part part : allParts ) {
            if ( part.getName().toLowerCase().replace(" ", "").startsWith(partName.toLowerCase().replace(" ", "")) ) {
                parts.add(part);
            }
        }
        return parts;
    }

    /**
     * @param productName the name to search for product matches with
     * @return all matches that either start with or equal productName param
     */

    public ObservableList<Product> lookupProduct(String productName) {
        ObservableList<Product> products = FXCollections.observableArrayList();
        for ( Product product : allProducts ) {
            if ( product.getName().toLowerCase().replace(" ", "").startsWith(productName.toLowerCase().replace(" ", "")) ) {
                products.add(product);
            }
        }
        return products;
    }

    /**
     * @param index the index at which the desired part is located in the inventory allParts array
     * @param selectedPart the part with the data changes desired for update
     */
    public void updatePart(int index, Part selectedPart) { this.allParts.set(index, selectedPart); }

    /**
     * @param index the index at which the desired product is located in the inventory allProducts array
     * @param newProduct the product with the data changes desired for update
     */
    public void updateProduct(int index, Product newProduct) { this.allProducts.set(index, newProduct); }

    /**
     * @param selectedPart the part that will be deleted from the inventory allParts array
     * @return boolean: whether or not the part was successfully deleted
     */
    public boolean deletePart(Part selectedPart) {
        try {
            this.allParts.remove(selectedPart);
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }

    /**
     * @param selectedProduct the product that will be deleted from the inventory allProducts array
     * @return boolean: whether or not the product was successfully deleted
     */
    public boolean deleteProduct(Product selectedProduct) {
        try {
            this.allProducts.remove(selectedProduct);
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }

    /**
     * @return all parts in the inventory
     */
    public ObservableList<Part> getAllParts() { return allParts; }

    /**
     * @return all products in the inventory
     */
    public ObservableList<Product> getAllProducts() { return allProducts; }
}
