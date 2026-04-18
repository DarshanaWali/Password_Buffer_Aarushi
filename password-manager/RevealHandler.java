import com.sun.net.httpserver.*;
import java.io.*;

public class RevealHandler implements HttpHandler {

    public void handle(HttpExchange exchange) throws IOException {

        String query = exchange.getRequestURI().getQuery();
        String enc = query.split("=")[1];

        try {
            String key = "1234567890123456";
            String dec = EncryptionUtil.decrypt(enc, key);

            exchange.sendResponseHeaders(200, dec.length());
            exchange.getResponseBody().write(dec.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}