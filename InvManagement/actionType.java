package InvManagement;

/**
 * actionType is an enum class to avoid making decisions based on magic variable text such as "add" or "update".
 * This also means a switch-case can be used when making decisions based on adding or updating.
 */
public enum actionType {
    ADD,
    UPDATE
}
