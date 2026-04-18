import com.sun.net.httpserver.*;

public class LogoutHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {

        try {
            exchange.getResponseHeaders().add(
                "Set-Cookie",
                "session=; Path=/; Max-Age=0; HttpOnly"
            );

            exchange.getResponseHeaders().add("Location", "/");
            exchange.sendResponseHeaders(302, -1);

            exchange.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}