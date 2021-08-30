package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;

public class RequestHandler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = new HttpRequest(inputStream);
            final HttpResponse httpResponse = new HttpResponse(outputStream);
            final String uri = getDefaultPath(httpRequest.getUri());

            final Map<String, String> requestBody = httpRequest.getParams();

            if (uri.startsWith("/login")) {
                if (requestBody.size() > 0) {
                    String account = httpRequest.getParameter("account");
                    String password = httpRequest.getParameter("password");

                    User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
                    if (user.checkPassword(password)) {
                        httpResponse.redirect("/index.html");
                    } else {
                        httpResponse.redirect("/401.html");
                    }
                }
                httpResponse.forward("/login.html");
            }

            if (uri.startsWith("/register")) {
                if (requestBody.size() > 0) {
                    String account = httpRequest.getParameter("account");
                    String email = httpRequest.getParameter("email");
                    String password = httpRequest.getParameter("password");
                    InMemoryUserRepository.save(new User(2L, account, password, email));
                    httpResponse.redirect("/login.html");
                }
                httpResponse.forward("/register.html");
            }
            httpResponse.forward(uri);
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String getDefaultPath(String uri) {
        if ("/".equals(uri)) {
            return "/index.html";
        }
        return uri;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
