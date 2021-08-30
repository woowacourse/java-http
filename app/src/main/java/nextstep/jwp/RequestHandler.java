package nextstep.jwp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream()) {
            UserService userService = new UserService();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            Map<String, String> parsedRequestHeaders = getParsedRequestHeaders(bufferedReader);
            final String httpMethod = parsedRequestHeaders.get("httpMethod");
            final String uri = parsedRequestHeaders.get("uri");
            String responseBody = "";
            String response = "";
            if (parsedRequestHeaders.isEmpty()) {
                return;
            }

            if (httpMethod.equals("GET") && (uri.equals("/index") || uri.equals("/index.html") || uri.equals("/"))) {
                responseBody = getStaticFileContents("/index.html");
                response = replyOkResponse(responseBody);
            } else if (httpMethod.equals("GET") && (uri.equals("/login.html") || uri.equals("/login"))) {
                responseBody = getStaticFileContents("/login.html");
                response = replyOkResponse(responseBody);
            } else if (httpMethod.equals("GET") && (uri.matches("/login.*account.*password.*"))) {
                Optional<User> user = userService.findUserFromUri(uri);
                log.debug(user.toString());
                if (user.isEmpty()) {
                    responseBody = getStaticFileContents("/401.html");
                    response = reply302Response(responseBody);
                } else {
                    responseBody = getStaticFileContents("/index.html");
                    response = replyOkResponse(responseBody);
                }
            } else if (httpMethod.equals("POST") && (uri.equals("/login.html") || uri.equals("/login"))) {
                String requestBody = extractRequestBody(bufferedReader, parsedRequestHeaders);
                Optional<User> user = userService.findUserFromBody(requestBody);
                if (user.isEmpty()) {
                    responseBody = getStaticFileContents("/401.html");
                    response = reply302Response(responseBody);
                } else {
                    responseBody = getStaticFileContents("/index.html");
                    response = replyOkResponse(responseBody);
                }

            } else if (httpMethod.equals("GET") && (uri.equals("/register") || uri.equals("/register.html"))) {
                responseBody = getStaticFileContents("/register.html");
                response = replyOkResponse(responseBody);
            } else if (httpMethod.equals("POST") && uri.equals("/register")) {
                String requestBody = extractRequestBody(bufferedReader, parsedRequestHeaders);
                userService.saveUser(requestBody);

                responseBody = getStaticFileContents("/index.html");
                response = replyOkResponse(responseBody);
            } else if (httpMethod.equals("GET") && uri.equals("/css/styles.css")) {
                responseBody = getStaticFileContents("/css/styles.css");
                response = replyOkCssResponse(responseBody);
            }
            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String extractRequestBody(BufferedReader bufferedReader, Map<String, String> parsedRequestHeaders)
            throws IOException {
        int contentLength = Integer.parseInt(parsedRequestHeaders.get("Content-Length").strip());
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return requestBody;
    }

    private String reply302Response(String responseBody) {
        final String response = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        log.debug("302 Content-Length: " + responseBody.getBytes().length);
        return response;
    }

    private String replyOkResponse(String responseBody) {
        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        log.debug("OK Content-Length: " + responseBody.getBytes().length);
        return response;
    }

    private String replyOkCssResponse(String responseBody) {
        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        log.debug("CSS OK Content-Length: " + responseBody.getBytes().length);
        return response;
    }

    private String getStaticFileContents(String path) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + path);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private Map<String, String> getParsedRequestHeaders(BufferedReader bufferedReader)
            throws IOException {

        final Map<String, String> parsedRequests = new HashMap<>();

        if (bufferedReader.ready()) {
            String headers = bufferedReader.readLine();
            String[] splitHeader = headers.split(" ");
            parsedRequests.put("httpMethod", splitHeader[0]);
            parsedRequests.put("uri", splitHeader[1]);
            parsedRequests.put("httpVersion", splitHeader[2]);
        }

        while (bufferedReader.ready()) {
            String headers = bufferedReader.readLine();
            String[] splitHeader = headers.split(": ");
            if (splitHeader[0].equals("")) {
                break;
            }
            parsedRequests.put(splitHeader[0], splitHeader[1]);
        }

        return parsedRequests;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
