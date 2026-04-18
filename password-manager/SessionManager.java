
import java.util.HashMap;
import java.util.UUID;

public class SessionManager {

    private static HashMap<String, String> sessions = new HashMap<>();

    public static String createSession(String email) {
        String id = UUID.randomUUID().toString();
        sessions.put(id, email);
        return id;
    }

    public static String getUser(String id) {
        return sessions.get(id);
    }
}