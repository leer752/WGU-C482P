package InvManagement;

/**
 * Outsourced is a subclass of abstract class Part. The only difference is that it has one additional field, companyName.
 * <p>
 * companyName is a String that has a mutator and accessor method. Every part in the main inventory is either Outsourced
 * or InHouse (another subclass of Part).
 * <p>
 * Validation for a new part object is done within the PartFormController view. A part object should NOT be
 * created outside of that view unless it is accompanied by the validation function. Otherwise, bad data could be passed
 * and errors will occur.
 */
public class Outsourced extends Part {
    /**
     * Identifier for Outsourced parts.
     */
    private String companyName;
    public Outsourced(int id, String name, double price, int stock, int min, int max, String companyName) {
        super(id, name, price, stock, min, max);
        this.companyName = companyName;
    }
    /**
     * @return the company name
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * @param companyName the company name to set
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
