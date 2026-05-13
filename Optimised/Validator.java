// No optimisation needed for Validator, other then return boolean instead of String, which is more efficient and easier to work with.
public class Validator {
    public boolean validateFormat(String data) {
        Metrics.callCount++;
        boolean valid = data != null && !data.isEmpty();
        System.out.println("[Validator] Validation result: " + (valid ? "Valid" : "Invalid"));
        return valid;
    }
}
