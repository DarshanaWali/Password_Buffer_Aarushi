import com.sun.net.httpserver.*;
import java.io.*;
import java.util.*;

public class DashboardHandler implements HttpHandler {

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

            int total = list.size();
            int weak = 0;

            for (PasswordEntry p : list) {
                if (p.getStrength().equals("Weak")) weak++;
            }

            String html = new String(
                java.nio.file.Files.readAllBytes(
                    java.nio.file.Paths.get("web/dashboard.html")
                )
            );

            html = html.replace("{{TOTAL}}", String.valueOf(total));
            html = html.replace("{{WEAK}}", String.valueOf(weak));

            // 🔥 IMPORTANT FIX
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, html.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(html.getBytes());
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