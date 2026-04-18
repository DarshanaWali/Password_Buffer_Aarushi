import java.util.*;

public class PasswordStrength {

    private static final Set<String> commonPasswords = new HashSet<>(Arrays.asList(
        "password", "123456", "12345678", "qwerty", "abc123", "admin", "letmein"
    ));

    public static int calculateScore(String password) {

        int score = 0;

        // 🔹 1. LENGTH
        int len = password.length();
        if (len >= 12) score += 30;
        else if (len >= 8) score += 20;
        else if (len >= 6) score += 10;

        // 🔹 2. CHARACTER SET SIZE (for entropy)
        int charset = 0;
        if (password.matches(".*[a-z].*")) charset += 26;
        if (password.matches(".*[A-Z].*")) charset += 26;
        if (password.matches(".*[0-9].*")) charset += 10;
        if (password.matches(".*[^a-zA-Z0-9].*")) charset += 32;

        // 🔹 3. ENTROPY (log₂)
        if (charset > 0) {
            double entropy = len * (Math.log(charset) / Math.log(2));
            score += (int)(entropy / 4); // normalize
        }

        // 🔹 4. UNIQUE CHARACTERS
        Set<Character> unique = new HashSet<>();
        for (char c : password.toCharArray()) {
            unique.add(c);
        }
        if (unique.size() > len / 2) score += 10;

        // 🔹 5. COMMON PASSWORD CHECK
        if (commonPasswords.contains(password.toLowerCase())) {
            return 0; // instant weak
        }

        // 🔹 6. REPETITION PENALTY
        if (hasRepeats(password)) score -= 15;

        // 🔹 7. SEQUENTIAL PATTERN
        if (hasSequence(password)) score -= 10;

        // 🔹 CLAMP
        if (score > 100) score = 100;
        if (score < 0) score = 0;

        return score;
    }

    public static String getStrength(String password) {
        int score = calculateScore(password);

        if (score < 40) return "Weak";
        else if (score < 70) return "Medium";
        else return "Strong";
    }

    // 🔁 REPETITION CHECK (DSA: sliding window idea)
    private static boolean hasRepeats(String s) {
        for (int i = 0; i < s.length() - 2; i++) {
            if (s.charAt(i) == s.charAt(i+1) && s.charAt(i) == s.charAt(i+2)) {
                return true;
            }
        }
        return false;
    }

    // 🔁 SEQUENCE CHECK (DSA: pattern detection)
    private static boolean hasSequence(String s) {
        for (int i = 0; i < s.length() - 2; i++) {
            char a = s.charAt(i);
            char b = s.charAt(i+1);
            char c = s.charAt(i+2);

            if (b == a + 1 && c == b + 1) return true; // abc, 123
        }
        return false;
    }
}