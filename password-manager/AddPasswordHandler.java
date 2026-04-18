import com.sun.net.httpserver.*;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class AddPasswordHandler implements HttpHandler {

    public void handle(HttpExchange exchange) throws IOException {

        try {
            // 🔐 Get session cookie
            String cookie = exchange.getRequestHeaders().getFirst("Cookie");

            if (cookie == null || !cookie.contains("session=")) {
                exchange.getResponseHeaders().add("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();
                return;
            }

            String session = cookie.split("=")[1];

            // ✅ Get user + key from session
            String email = SessionManager.getUser(session);
            String key = SessionManager.getKey(session);

            if (email == null || key == null) {
                exchange.getResponseHeaders().add("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();
                return;
            }

            // 📥 Read form data
            String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );

            String website = "", username = "", password = "";

            for (String pair : body.split("&")) {
                String[] kv = pair.split("=");
                String val = URLDecoder.decode(kv[1], "UTF-8");

                if (kv[0].equals("website")) website = val;
                if (kv[0].equals("username")) username = val;
                if (kv[0].equals("password")) password = val;
            }

            // 🔐 Save using correct key
            PasswordManager.savePassword(email, website, username, password, key);

            // 🔁 Redirect back to vault
            exchange.getResponseHeaders().add("Location", "/vault");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();

        } catch (Exception e) {
            e.printStackTrace();

            String error = "Failed to save password";
            exchange.sendResponseHeaders(500, error.length());
            exchange.getResponseBody().write(error.getBytes());
            exchange.close();
        }
    }
}