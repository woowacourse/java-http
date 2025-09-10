package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestHandler requestHandler;
    private final HttpResponseHandler responseHandler;

    public Http11Processor(Socket connection) {
        this.connection = connection;
        this.requestHandler = new HttpRequestHandler();
        this.responseHandler = new HttpResponseHandler();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            HttpRequest request = requestHandler.handleRequest(inputStream);
            HttpUri requestUri = request.getUri();

            String path = requestUri.getPath();

            HttpCookie cookie = request.getCookies();
            String sessionId = cookie.getCookie("JSESSIONID");

            HttpResponse response;

            if (path.startsWith("/login") && request.getHttpMethod() == HttpMethod.GET) {
                Session session = SessionManager.findSession(sessionId);
                if (session != null) {
                    response = responseHandler.handleResponse(request, HttpStatusCode.FOUND);
                    response.setLocation("/index.html");
                    sendResponse(outputStream, response);
                    return;
                }
                response = responseHandler.handleResponse(request, HttpStatusCode.OK);
                sendResponse(outputStream, response);
                return;
            }

            if (path.startsWith("/login") && request.getHttpMethod() == HttpMethod.POST) {
                try {
                    Map<String, String> formData = request.getBody().getFormData();
                        String account = formData.get("account");
                        String password = formData.get("password");

                        User user = InMemoryUserRepository.findByAccount(account)
                                .orElseThrow(IllegalArgumentException::new);

                        boolean checkPassword = user.checkPassword(password);
                        if (!checkPassword) {
                            response = responseHandler.handleResponse(request, HttpStatusCode.FOUND);
                            response.setLocation("/401.html");
                            sendResponse(outputStream, response);
                            return;
                        }

                        UUID uuid = UUID.randomUUID();
                        Session session = new Session(uuid.toString());
                        session.setAttribute("user", user);
                        SessionManager.add(session);

                        response = responseHandler.handleResponse(request, HttpStatusCode.FOUND);
                        response.setLocation("/index.html");
                        response.addCookie("JSESSIONID", uuid.toString());
                        sendResponse(outputStream, response);
                        return;
                } catch (IllegalArgumentException e) {
                    response = responseHandler.handleResponse(request, HttpStatusCode.FOUND);
                    response.setLocation("/401.html");
                    sendResponse(outputStream, response);
                    return;
                }
            }

            if (path.startsWith("/register") && request.getHttpMethod() == HttpMethod.GET) {
                response = responseHandler.handleResponse(request, HttpStatusCode.OK);
                sendResponse(outputStream, response);
                return;
            }

            if (path.startsWith("/register") && request.getHttpMethod() == HttpMethod.POST) {
                try {
                    HttpRequestBody body = request.getBody();
                    Map<String, String> formData = body.getFormData();

                    String account = formData.get("account");
                    String password = formData.get("password");
                    String email = formData.get("email");

                    User user = new User(account, password, email);
                    InMemoryUserRepository.save(user);

                    response = responseHandler.handleResponse(request, HttpStatusCode.FOUND);
                    response.setLocation("/index.html");
                    sendResponse(outputStream, response);
                    return;
                } catch (IllegalArgumentException e) {
                    response = responseHandler.handleResponse(request, HttpStatusCode.FOUND);
                    response.setLocation("/401.html");
                    sendResponse(outputStream, response);
                    return;
                }
            }
            response = responseHandler.handleResponse(request, HttpStatusCode.OK);
            sendResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void sendResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.asString().getBytes());
        outputStream.flush();
    }
}
