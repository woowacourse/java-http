package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
            HttpRequest httpRequest = new HttpRequest(inputStream);
            String method = httpRequest.getMethod();
            String path = httpRequest.getPath();
            String response = "";

            if (path.equals("/")) {
                HttpResponse httpResponse = new HttpResponse(HttpStatus.OK_200, "Hello world!", path);
                response = httpResponse.createResponse();
            }

            if (path.contains(".")) {
                HttpResponse httpResponse = new HttpResponse(HttpStatus.OK_200, path, path);
                response = httpResponse.createResponse();
            }

            if (method.equals("GET") && path.equals("/register")) {
                HttpResponse httpResponse = new HttpResponse(HttpStatus.OK_200, "/register.html", path);
                response = httpResponse.createResponse();
            }

            if (method.equals("GET") && path.equals("/login")) {
                HttpResponse httpResponse = new HttpResponse(HttpStatus.OK_200, "/login.html", path);
                response = httpResponse.createResponse();
            }

            if (method.equals("POST") && path.equals("/login")) {
                Map<String, String> params = httpRequest.getParams();
                Optional<User> account = InMemoryUserRepository.findByAccount(params.get("account"));

                if (account.isPresent() && account.get().checkPassword(params.get("password"))) {
                    HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND_302, null, "/index.html");
                    response = httpResponse.createRedirectResponse();
                } else {
                    HttpResponse httpResponse = new HttpResponse(HttpStatus.UNAUTHORIZED_401, "/401.html", path);
                    response = httpResponse.createResponse();
                }
            }

            if (method.equals("POST") && path.equals("/register")) {
                Map<String, String> params = httpRequest.getParams();
                User user = new User(InMemoryUserRepository.size() + 1L,
                        params.get("account"),
                        params.get("password"),
                        params.get("email"));

                Optional<User> account = InMemoryUserRepository.findByAccount(params.get("account"));
                if (account.isPresent()) {
                    HttpResponse httpResponse = new HttpResponse(HttpStatus.UNAUTHORIZED_401, "/401.html", path);
                    response = httpResponse.createResponse();

                } else {
                    InMemoryUserRepository.save(user);
                    HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND_302, null, "/index.html");
                    response = httpResponse.createRedirectResponse();
                }
            }
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
