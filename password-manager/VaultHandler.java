import com.sun.net.httpserver.*;
import java.io.*;
import java.util.*;

public class VaultHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            String cookie = exchange.getRequestHeaders().getFirst("Cookie");

            if (cookie == null || !cookie.contains("session=")) {
                exchange.getResponseHeaders().add("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();
                return;
            }

            String sessionId = cookie.split("=")[1];
            String email = SessionManager.getUser(sessionId);

            if (email == null) {
                exchange.getResponseHeaders().add("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();
                return;
            }

            List<PasswordEntry> list = PasswordManager.getPasswords(email);

            StringBuilder rows = new StringBuilder();

            for (PasswordEntry p : list) {
                rows.append("<tr>")
                    .append("<td>").append(p.getWebsite()).append("</td>")
                    .append("<td>").append(p.getUsername()).append("</td>")
                    .append("<td>").append(p.getStrength()).append("</td>")
                    .append("<td><button onclick=\"reveal('")
                    .append(p.getEncryptedPassword())
                    .append("')\">Reveal</button></td>")
                    .append("</tr>");
            }

            String html = new String(
                java.nio.file.Files.readAllBytes(
                    java.nio.file.Paths.get("web/vault.html")
                )
            );

            html = html.replace("{{ROWS}}", rows.toString());

            // ✅ IMPORTANT FIXES
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            byte[] responseBytes = html.getBytes();

            exchange.sendResponseHeaders(200, responseBytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();

        } catch (Exception e) {
            e.printStackTrace();

            String error = "Internal Server Error";
            exchange.sendResponseHeaders(500, error.length());
            exchange.getResponseBody().write(error.getBytes());
            exchange.close();
        }
    }
}