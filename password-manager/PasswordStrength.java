public class PasswordStrength {

    public static String check(String password) {

        int score = 0;

        if (password.length() >= 8) score++;

        boolean hasUpper = false, hasDigit = false, hasSymbol = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSymbol = true;
        }

        if (hasUpper) score++;
        if (hasDigit) score++;
        if (hasSymbol) score++;

        if (score <= 2) return "Weak";
        else if (score <= 4) return "Medium";
        else return "Strong";
    }
}