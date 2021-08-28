package nextstep.joanne;

import exception.UserNotFoundException;
import nextstep.joanne.db.InMemoryUserRepository;
import nextstep.joanne.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
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

            final BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(inputStream));

            final StringBuilder requestHeaderBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (!"".equals(line)) {
                if (line == null) return;
                requestHeaderBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }

            String[] httpRequestHeaders = requestHeaderBuilder.toString().split("\n");

            // requestURI
            String requestURI = httpRequestHeaders[0];
            String[] splittedRequestURI = requestURI.split(" ");
            String uri = splittedRequestURI[1].substring(1);
            int index = uri.indexOf("?");
            String path = uri;
            String queryString = "";
            String httpStatus = "200 OK";
            if (index > 0) {
                path = uri.substring(0, index);
                queryString = uri.substring(index + 1);
            }

            HashMap<String, String> queryStringMap = new HashMap<>();
            if (!queryString.isBlank()) {
                String[] queryStrings = queryString.split("&");
                for (String query : queryStrings) {
                    String[] values = query.split("=");
                    queryStringMap.put(values[0], values[1]);
                }
            }

            if (!queryStringMap.isEmpty()) {
                Optional<User> user = InMemoryUserRepository.findByAccount(queryStringMap.get("account"));

                if (user.isPresent() && user.get().checkPassword(queryStringMap.get("password"))) {
                    log.info("login success, account: {}", user.get().getAccount());
                    path = "index.html";
                    httpStatus = "302 Found";
                } else {
                    log.info("login failed, account: {}, password: {}", queryStringMap.get("account"), queryStringMap.get("password"));
                    path = "401.html";
                    httpStatus = "401 Unauthorized";
                }
            }

            String responseBody = "";
            if (path.isBlank()) {
                responseBody = "Hello world!";
            } else {
                if (!path.contains(".")) {
                    path += ".html";
                }
                URL resource = getClass().getClassLoader().getResource("static/" + path);
                if (Objects.nonNull(resource)) {
                    final Path filePath = new File(resource.getPath()).toPath();
                    responseBody = Files.readString(filePath);
                }
            }

            final String response = makeHttpResponse(httpStatus, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String makeHttpResponse(String httpStatus, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus + " ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
