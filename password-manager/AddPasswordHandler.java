import com.sun.net.httpserver.*;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class AddPasswordHandler implements HttpHandler {

    public void handle(HttpExchange exchange) throws IOException {

        String cookie = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookie == null) return;

        String session = cookie.split("=")[1];
        String email = SessionManager.getUser(session);

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        String website = "", username = "", password = "";

        for (String pair : body.split("&")) {
            String[] kv = pair.split("=");
            String val = URLDecoder.decode(kv[1], "UTF-8");

            if (kv[0].equals("website")) website = val;
            if (kv[0].equals("username")) username = val;
            if (kv[0].equals("password")) password = val;
        }

        try {
            String key = "1234567890123456";
            PasswordManager.savePassword(email, website, username, password, key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        exchange.getResponseHeaders().add("Location", "/vault");
        exchange.sendResponseHeaders(302, -1);
    }
}