package service;

public class ValidationUtil {

    public static boolean isValidEmail(String email) {
        if (email == null)
            return false;
        int atIndex = -1;
        int dotIndex = -1;

        for (int i = 0; i < email.length(); i++) {
            char c = email.charAt(i);
            if (c == '@') {
                if (atIndex != -1)
                    return false;
                atIndex = i;
            } else if (c == '.') {
                dotIndex = i;
            }
        }

        if (atIndex > 0 && dotIndex > atIndex + 1 && dotIndex < email.length() - 1) {
            return true;
        }
        return false;
    }

    public static boolean isValidUsername(String username) {
        if (username == null)
            return false;
        int len = username.length();
        if (len < 3 || len > 20)
            return false;

        for (int i = 0; i < len; i++) {
            char c = username.charAt(i);
            boolean isLower = (c >= 'a' && c <= 'z');
            boolean isUpper = (c >= 'A' && c <= 'Z');
            boolean isDigit = (c >= '0' && c <= '9');
            boolean isUnderscore = (c == '_');

            if (!isLower && !isUpper && !isDigit && !isUnderscore) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidPassword(String password) {
        if (password == null)
            return false;
        if (password.length() < 8)
            return false;

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        String specialChars = "@$!%*?&";

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (c >= 'a' && c <= 'z')
                hasLower = true;
            else if (c >= 'A' && c <= 'Z')
                hasUpper = true;
            else if (c >= '0' && c <= '9')
                hasDigit = true;
            else {
                // Check if it's one of the allowed special chars
                boolean isSpec = false;
                for (int j = 0; j < specialChars.length(); j++) {
                    if (c == specialChars.charAt(j)) {
                        isSpec = true;
                        break;
                    }
                }
                if (isSpec)
                    hasSpecial = true;
            }
        }

        if (hasUpper && hasLower && hasDigit && hasSpecial) {
            return true;
        }
        return false;
    }

    public static boolean isValidFullName(String fullName) {
        if (fullName == null)
            return false;
        String trimmed = fullName.trim();
        if (trimmed.isEmpty())
            return false;
        if (fullName.length() < 3 || fullName.length() > 50)
            return false;
        return true;
    }

    public static String getPasswordRequirements() {
        return "Password must be at least 8 characters with:\n- Uppercase letter\n- Lowercase letter\n- Number\n- Special character (@$!%*?&)";
    }
}
