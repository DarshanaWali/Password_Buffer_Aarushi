import com.sun.net.httpserver.*;
import java.io.*;
import java.util.*;

public class DashboardHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            // 🔐 Session check
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

            // 📊 Get data
            List<PasswordEntry> list = PasswordManager.getPasswords(email);

            int total = list.size();
            int weak = 0;

            for (PasswordEntry p : list) {
                if (p.getStrength().equalsIgnoreCase("Weak")) {
                    weak++;
                }
            }

            // 📄 Load HTML
            String html = new String(
                java.nio.file.Files.readAllBytes(
                    java.nio.file.Paths.get("web/dashboard.html")
                )
            );

            html = html.replace("{{TOTAL}}", String.valueOf(total));
            html = html.replace("{{WEAK}}", String.valueOf(weak));

            // 🚫 NO CACHE (VERY IMPORTANT)
            exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
            exchange.getResponseHeaders().set("Pragma", "no-cache");
            exchange.getResponseHeaders().set("Expires", "0");

            exchange.getResponseHeaders().set("Content-Type", "text/html");

            byte[] response = html.getBytes();

            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();

        } catch (Exception e) {
            e.printStackTrace();

            String err = "Internal Server Error";
            exchange.sendResponseHeaders(500, err.length());
            exchange.getResponseBody().write(err.getBytes());
            exchange.close();
        }
    }
}