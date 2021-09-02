package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.ContentTypeMapper;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String HTTP_STATUS_200 = "200 OK";
    private static final String HTTP_STATUS_302 = "302 FOUND";
    private static final String HTTP_STATUS_401 = "401 UNAUTHORIZED";

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
            String resource = httpRequest.getPath();

            String responseStatusCode = "";
            String responseBody = "";
            String response = "";

            if (resource.equals("/")) {
                responseStatusCode = HTTP_STATUS_200;
                responseBody = "Hello world!";
                response = create200Response(responseStatusCode, responseBody, ContentTypeMapper.extractContentType(resource));
            }

            if (resource.contains(".")) {
                responseStatusCode = HTTP_STATUS_200;
                responseBody = createStaticFileResponseBody(resource);
                response = create200Response(responseStatusCode, responseBody, ContentTypeMapper.extractContentType(resource));
            }

            if (method.equals("GET") && resource.equals("/register")) {
                responseStatusCode = HTTP_STATUS_200;
                responseBody = createStaticFileResponseBody("/register.html");
                response = create200Response(responseStatusCode, responseBody, ContentTypeMapper.extractContentType(resource));
            }

            if (method.equals("GET") && resource.equals("/login")) {
                responseStatusCode = HTTP_STATUS_200;
                responseBody = createStaticFileResponseBody("/login.html");
                response = create200Response(responseStatusCode, responseBody, ContentTypeMapper.extractContentType(resource));
            }

            if (method.equals("POST") && resource.equals("/login")) {
                Map<String, String> params = httpRequest.getParams();
                Optional<User> account = InMemoryUserRepository.findByAccount(params.get("account"));

                if (account.isPresent() && account.get().checkPassword(params.get("password"))) {
                    responseStatusCode = HTTP_STATUS_302;
                    response = create302Response(responseStatusCode, "/index.html");
                } else {
                    responseStatusCode = HTTP_STATUS_401;
                    responseBody = createStaticFileResponseBody("/401.html");
                    response = create200Response(responseStatusCode, responseBody, ContentTypeMapper.extractContentType(resource));
                }
            }

            if (method.equals("POST") && resource.equals("/register")) {
                Map<String, String> params = httpRequest.getParams();
                User user = new User(InMemoryUserRepository.size() + 1L,
                        params.get("account"),
                        params.get("password"),
                        params.get("email"));

                Optional<User> account = InMemoryUserRepository.findByAccount(params.get("account"));
                if (account.isPresent()) {
                    responseStatusCode = HTTP_STATUS_401;
                    responseBody = createStaticFileResponseBody("/401.html");
                    response = create200Response(responseStatusCode, responseBody, ContentTypeMapper.extractContentType(resource));

                } else {
                    InMemoryUserRepository.save(user);
                    responseStatusCode = HTTP_STATUS_302;
                    response = create302Response(responseStatusCode, "/index.html");
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

    private String createStaticFileResponseBody(String render) throws IOException {
        String filePath = "static" + render;
        final URL url = getClass().getClassLoader().getResource(filePath);
        File file = new File(Objects.requireNonNull(url).getFile());
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes);
    }

    private String create200Response(String responseHeader, String responseBody, String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 " + responseHeader + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String create302Response(String responseHeader, String redirectUrl) {
        return String.join("\r\n",
                "HTTP/1.1 " + responseHeader + " ",
                "Location: http://localhost:8080" + redirectUrl);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
