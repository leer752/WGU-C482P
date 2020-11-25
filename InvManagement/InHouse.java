package InvManagement;

/**
 * InHouse is a subclass of abstract class Part. The only difference is that it has one additional field, machineID.
 * <p>
 * machineID is an Integer that has a mutator and accessor method. Every part in the main inventory is either InHouse
 * or Outsourced (another subclass of Part).
 * <p>
 * Validation for a new part object is done within the PartFormController view. A part object should NOT be
 * created outside of that view unless it is accompanied by the validation function. Otherwise, bad data could be passed
 * and errors will occur.
 */
public class InHouse extends Part {
    /**
     * Identifier for In-House parts; must be verified as an Integer.
     */
    private int machineID;
    public InHouse(int id, String name, double price, int stock, int min, int max, int machineID) {
        super(id, name, price, stock, min, max);
        this.machineID = machineID;
    }
    /**
     * @return the machine id
     */
    public int getMachineID() {
        return machineID;
    }

    /**
     * @param machineID the machine id to set
     */
    public void setMachineID(int machineID) {
        this.machineID = machineID;
    }
}
