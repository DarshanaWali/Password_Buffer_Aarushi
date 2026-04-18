import com.sun.net.httpserver.*;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class RevealHandler implements HttpHandler {

    @Override
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

            // ✅ Get correct key from session
            String key = SessionManager.getKey(session);

            if (key == null) {
                exchange.getResponseHeaders().add("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();
                return;
            }

            // 📥 Read encrypted data from URL
            String query = exchange.getRequestURI().getQuery();

            if (query == null || !query.startsWith("data=")) {
                String err = "Invalid request";
                exchange.sendResponseHeaders(400, err.length());
                exchange.getResponseBody().write(err.getBytes());
                exchange.close();
                return;
            }

            String encoded = query.substring(5);
            String encrypted = URLDecoder.decode(encoded, StandardCharsets.UTF_8);

            // 🔓 Decrypt using correct key
            String decrypted = EncryptionUtil.decrypt(encrypted, key);

            // 📤 Send response
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, decrypted.length());

            OutputStream os = exchange.getResponseBody();
            os.write(decrypted.getBytes());
            os.close();

        } catch (Exception e) {
            e.printStackTrace();

            String error = "Decryption failed";
            exchange.sendResponseHeaders(500, error.length());
            exchange.getResponseBody().write(error.getBytes());
            exchange.close();
        }
    }
}