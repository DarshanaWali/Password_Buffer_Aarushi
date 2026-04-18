import java.util.HashMap;
import java.util.UUID;

public class SessionManager {

    private static HashMap<String, String> sessions = new HashMap<>();
    private static HashMap<String, String> userKeys = new HashMap<>();

    public static String createSession(String email, String key) {
        String id = UUID.randomUUID().toString();
        sessions.put(id, email);
        userKeys.put(id, key);
        return id;
    }

    public static String getUser(String id) {
        return sessions.get(id);
    }

    public static String getKey(String id) {
        return userKeys.get(id);
    }
}