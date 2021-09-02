package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
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
            HttpResponse httpResponse = new HttpResponse(outputStream);
            String method = httpRequest.getMethod();
            String path = httpRequest.getPath();

            if (path.equals("/")) {
                httpResponse.setStatus(HttpStatus.OK_200);
                httpResponse.setBody("Hello world!");
                httpResponse.setPath(path);
                httpResponse.forward();
            }

            if (path.contains(".")) {
                httpResponse.setStatus(HttpStatus.OK_200);
                httpResponse.setBody(createBody(path));
                httpResponse.setPath(path);
                httpResponse.forward();
            }

            if (method.equals("GET") && path.equals("/register")) {
                httpResponse.setStatus(HttpStatus.OK_200);
                httpResponse.setBody(createBody("/register.html"));
                httpResponse.setPath(path);
                httpResponse.forward();
            }

            if (method.equals("GET") && path.equals("/login")) {
                httpResponse.setStatus(HttpStatus.OK_200);
                httpResponse.setBody(createBody("/login.html"));
                httpResponse.setPath(path);
                httpResponse.forward();
            }

            if (method.equals("POST") && path.equals("/login")) {
                Map<String, String> params = httpRequest.getParams();
                Optional<User> account = InMemoryUserRepository.findByAccount(params.get("account"));

                if (account.isPresent() && account.get().checkPassword(params.get("password"))) {
                    httpResponse.setStatus(HttpStatus.FOUND_302);
                    httpResponse.setRedirectUrl("/index.html");
                    httpResponse.redirect();
                } else {
                    httpResponse.setStatus(HttpStatus.UNAUTHORIZED_401);
                    httpResponse.setBody(createBody("/401.html"));
                    httpResponse.setPath(path);
                    httpResponse.forward();
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
                    httpResponse.setStatus(HttpStatus.UNAUTHORIZED_401);
                    httpResponse.setBody(createBody("/401.html"));
                    httpResponse.setPath(path);
                    httpResponse.forward();

                } else {
                    InMemoryUserRepository.save(user);
                    httpResponse.setStatus(HttpStatus.FOUND_302);
                    httpResponse.setRedirectUrl("/index.html");
                    httpResponse.redirect();
                }
            }
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String createBody(String filePath) throws IOException {
        final URL url = getClass().getClassLoader().getResource("static" + filePath);
        File file = new File(Objects.requireNonNull(url).getFile());
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
