package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
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
            Request request = Request.createFromInputStream(inputStream);
            final String httpMethod = request.getRequestLine("httpMethod");
            final String uri = request.getRequestLine("uri");
            String responseBody = "";
            String response = "";

            if (request.isEmpty()) {
                return;
            }

            if (httpMethod.equals("GET") && (uri.equals("/index") || uri.equals("/index.html") || uri.equals("/"))) {
                responseBody = ResponseBody.createByPath("/index.html").getResponseBody();
                response = replyOkResponse(responseBody);
            } else if (httpMethod.equals("GET") && (uri.equals("/login.html") || uri.equals("/login"))) {
                responseBody = ResponseBody.createByPath("/login.html").getResponseBody();
                response = replyOkResponse(responseBody);
            } else if (httpMethod.equals("POST") && (uri.equals("/login.html") || uri.equals("/login"))) {
                String requestBody = request.getBody();
                Optional<User> user = userService.findUserFromBody(requestBody);
                if (user.isEmpty()) {
                    responseBody = ResponseBody.createByPath("/401.html").getResponseBody();
                    response = replyAfterLogin302Response(responseBody, "/401.html");
                } else {
                    responseBody = ResponseBody.createByPath("/index.html").getResponseBody();
                    response = replyAfterLogin302Response(responseBody, "/index.html");
                }
            } else if (httpMethod.equals("GET") && (uri.equals("/register") || uri.equals("/register.html"))) {
                responseBody = ResponseBody.createByPath("/register.html").getResponseBody();
                response = replyOkResponse(responseBody);
            } else if (httpMethod.equals("POST") && uri.equals("/register")) {
                String requestBody = request.getBody();
                userService.saveUser(requestBody);

                responseBody = ResponseBody.createByPath("/index.html").getResponseBody();
                response = replyOkResponse(responseBody);
            } else if (httpMethod.equals("GET") && uri.equals("/css/styles.css")) {
                responseBody = ResponseBody.createByPath("/css/styles.css").getResponseBody();
                response = replyOkCssResponse(responseBody);
            } else if (httpMethod.equals("GET") && uri.matches("/.*(js)$")) {
                responseBody = ResponseBody.createByPath(uri).getResponseBody();
                response = replyOkJsResponse(responseBody);
            } else if (httpMethod.equals("GET") && uri.equals("/401.html")) {
                responseBody = ResponseBody.createByPath(uri).getResponseBody();
                response = replyOkResponse(responseBody);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }


    private String replyAfterLogin302Response(String responseBody, String location) {
        final String response = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location + " ",
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

    private String replyOkJsResponse(String responseBody) {
        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: application/js;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        log.debug("JS OK Content-Length: " + responseBody.getBytes().length);
        return response;
    }

    private String getStaticFileContents(String path) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + path);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }


    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
