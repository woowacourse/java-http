package nextstep.jwp.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.RequestMapper;
import nextstep.jwp.UserService;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
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

            if (httpRequest.isEmpty()) {
                return;
            }
//            String responseBody = ResponseBody.createByPath(httpRequest.getRequestLinePart("uri")).getResponseBody();
//            response = replyOkResponse(responseBody);

            RequestMapper requestMapper = new RequestMapper();
            Controller matchedController = requestMapper.getController(httpRequest);
            HttpResponse response = matchedController.service(httpRequest);

//            if (httpMethod.equals("GET") && (uri.equals("/index") || uri.equals("/index.html") || uri.equals("/"))) {

//            } else if (httpMethod.equals("GET") && (uri.equals("/login.html") || uri.equals("/login"))) {
//                responseBody = ResponseBody.createByPath("/login.html").getResponseBody();
//                response = replyOkResponse(responseBody);
//            } else if (httpMethod.equals("POST") && (uri.equals("/login.html") || uri.equals("/login"))) {
//                String requestBody = httpRequest.getBody();
//                Optional<User> user = userService.findUserFromBody(requestBody);
//                if (user.isEmpty()) {
//                    responseBody = ResponseBody.createByPath("/401.html").getResponseBody();
//                    response = replyAfterLogin302Response(responseBody, "/401.html");
//                } else {
//                    responseBody = ResponseBody.createByPath("/index.html").getResponseBody();
//                    response = replyAfterLogin302Response(responseBody, "/index.html");
//                }
//            } else if (httpMethod.equals("GET") && (uri.equals("/register") || uri.equals("/register.html"))) {
//                responseBody = ResponseBody.createByPath("/register.html").getResponseBody();
//                response = replyOkResponse(responseBody);
//            } else if (httpMethod.equals("POST") && uri.equals("/register")) {
//                String requestBody = httpRequest.getBody();
//                userService.saveUser(requestBody);
//
//                responseBody = ResponseBody.createByPath("/index.html").getResponseBody();
//                response = replyOkResponse(responseBody);
//            } else if (httpMethod.equals("GET") && uri.equals("/css/styles.css")) {
//                responseBody = ResponseBody.createByPath("/css/styles.css").getResponseBody();
//                response = replyOkCssResponse(responseBody);
//            } else if (httpMethod.equals("GET") && uri.matches("/.*(js)$")) {
//                responseBody = ResponseBody.createByPath(uri).getResponseBody();
//                response = replyOkJsResponse(responseBody);
//            } else if (httpMethod.equals("GET") && uri.equals("/401.html")) {
//                responseBody = ResponseBody.createByPath(uri).getResponseBody();
//                response = replyOkResponse(responseBody);
//            }

            outputStream.write(response.getResponseToString().getBytes());
            outputStream.flush();

        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } catch (Exception exception) {
            log.error("Error", exception);
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
