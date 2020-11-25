package InvManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Product is like the Part class in that it is the other main class of inventory objects; however, Product is not
 * abstract. Product has no subclasses, and includes an array list of associated parts.
 * <p>
 * The array list of associated parts works similarly to the lists of inventory - it can be retrieved with an accessor
 * method but cannot be set by a mutator. Associated parts have to be added or deleted one at a time.
 * <p>
 * Validation for a new product object is done within the ProductFormController view. A product object should NOT be
 * created outside of that view unless it is accompanied by the validation function. Otherwise, bad data could be passed
 * and errors will occur.
 */
public class Product {
    /**
     * Array list of associated parts from main inventory.
     */
    private ObservableList<Part> associatedParts = FXCollections.observableArrayList();
    /**
     * Unique identifier for product objects.
     */
    private int id;
    /**
     * Product name; required.
     */
    private String name;
    /**
     * Product price; required; must be a Double; displayed in form "#.##".
     */
    private double price;
    /**
     * Product stock; required; must be an Integer; must be between/equal to min and max.
     */
    private int stock;
    /**
     * Product minimum stock; required; must be less than max.
     */
    private int min;
    /**
     * Product maximum stock; required; must be greater than min.
     */
    private int max;
    public Product(int id, String name, double price, int stock, int min, int max) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.min = min;
        this.max = max;
    }
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * @return the stock
     */
    public int getStock() {
        return stock;
    }

    /**
     * @param stock the stock to set
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * @return the min
     */
    public int getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public int getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(int max) {
        this.max = max;
    }
    /**
     * @param part the part that will be added to the associatedParts array
     */
    public void addAssociatedPart(Part part) { this.associatedParts.add(part); }
    /**
     * @param selectedAssociatedPart the part that will be deleted from the associatedParts array
     * @return whether or not the removal was a success
     */
    public boolean deleteAssociatedPart(Part selectedAssociatedPart) {
        try {
            this.associatedParts.remove(selectedAssociatedPart);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    /**
     * @return a list of all associated parts
     */
    public ObservableList<Part> getAllAssociatedParts() {
        return associatedParts;
    }
}
