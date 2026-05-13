public class Validator {
    public String validateFormat(String data) {
        Metrics.callCount++;
        if (data == null || data.isEmpty()) {
            System.out.println("[Validator] Validation result: Invalid");
            return "Invalid";
        }
        System.out.println("[Validator] Validation result: Valid");
        return "Valid";

    }
}
