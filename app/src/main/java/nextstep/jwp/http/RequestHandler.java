package nextstep.jwp.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.UserService;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.ResponseBody;
import nextstep.jwp.http.response.ResponseHeader;
import nextstep.jwp.http.response.ResponseLine;
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
            HttpRequest httpRequest = HttpRequest.createFromInputStream(inputStream);
            final String httpMethod = httpRequest.getRequestLine("httpMethod");
            final String uri = httpRequest.getRequestLine("uri");
            String responseBody = "";
            String response = "";

            if (httpRequest.isEmpty()) {
                return;
            }

            if (httpMethod.equals("GET") && (uri.equals("/index") || uri.equals("/index.html") || uri.equals("/"))) {
                responseBody = ResponseBody.createByPath("/index.html").getResponseBody();
                response = replyOkResponse(responseBody);
            } else if (httpMethod.equals("GET") && (uri.equals("/login.html") || uri.equals("/login"))) {
                responseBody = ResponseBody.createByPath("/login.html").getResponseBody();
                response = replyOkResponse(responseBody);
            } else if (httpMethod.equals("POST") && (uri.equals("/login.html") || uri.equals("/login"))) {
                String requestBody = httpRequest.getBody();
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
                String requestBody = httpRequest.getBody();
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
        return new HttpResponse(
                new ResponseLine("302", "Found"),
                new ResponseHeader.ResponseHeaderBuilder(SupportedContentType.HTML,
                        responseBody.getBytes().length).
                        location(location).build(),
                ResponseBody.createByString(responseBody)).getResponseToString();
    }

    private String replyOkResponse(String responseBody) {
        return new HttpResponse(
                new ResponseLine("200", "OK"),
                new ResponseHeader.ResponseHeaderBuilder(SupportedContentType.HTML,
                        responseBody.getBytes().length).build(),
                ResponseBody.createByString(responseBody)).getResponseToString();
    }

    private String replyOkCssResponse(String responseBody) {
        return new HttpResponse(
                new ResponseLine("200", "OK"),
                new ResponseHeader.ResponseHeaderBuilder(SupportedContentType.CSS,
                        responseBody.getBytes().length).build(),
                ResponseBody.createByString(responseBody)).getResponseToString();
    }

    private String replyOkJsResponse(String responseBody) {
        return new HttpResponse(
                new ResponseLine("200", "OK"),
                new ResponseHeader.ResponseHeaderBuilder(SupportedContentType.JS,
                        responseBody.getBytes().length).build(),
                ResponseBody.createByString(responseBody)).getResponseToString();
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
