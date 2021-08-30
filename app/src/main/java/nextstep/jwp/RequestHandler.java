package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.request.CharlieHttpRequest;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RequestHandler implements Runnable {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String HTTP = "HTTP/1.1 ";

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private String requestUri;
    private String statusCode;
    private ResponseHeader responseHeader = new ResponseHeader();

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            HttpRequest httpRequest = CharlieHttpRequest.of(bufferedReader);

            if ("/index".equals(requestUri)) {
                requestUri = "/index.html";
                statusCode = "200 OK ";
            }
            if ("/login".equals(requestUri)) {
                requestUri = login(Collections.emptyMap());
            }
            final String responseBody = getResourceContents(requestUri);

//            "Content-Type: text/html;charset=utf-8 ",
//                    "Content-Length: " + responseBody.getBytes().length + " ",

            final String response = String.join("\r\n",
                    HTTP + statusCode,
                    responseHeader.toHeaderLine(),
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String login(Map<String, String> parametersOfQueryString) {
        String account = parametersOfQueryString.get("account");
        String password = parametersOfQueryString.get("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isEmpty() || optionalUser.get().invalidPassword(password)) {
            statusCode = "302 FOUND ";
            return "/401.html";
        }
        System.out.println(optionalUser.get());
        statusCode = "302 FOUND ";
        return "/index.html";
    }

    private String getResourceContents(String requestPath) throws IOException {
        String requestResourceName = "static" + requestPath;
        URL resource = getClass().getClassLoader().getResource(requestResourceName);
        Path resourcePath = new File(resource.getFile()).toPath();
        return new String(Files.readAllBytes(resourcePath));
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
