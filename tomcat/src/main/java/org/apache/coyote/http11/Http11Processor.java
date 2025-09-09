package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;
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

            HttpResponse response;

            if (path.startsWith("/login")) {
                try {
                    if (!request.hasQueryParameter()) {
                        response = responseHandler.handleResponse(request, HttpStatusCode.OK);
                        sendResponse(outputStream, response);
                        return;
                    }

                    String account = request.getQueryParameter("account");
                    String password = request.getQueryParameter("password");

                    User user = InMemoryUserRepository.findByAccount(account).orElseThrow(IllegalArgumentException::new);
                    boolean checkPassword = user.checkPassword(password);

                    if (!checkPassword) {
                        response = responseHandler.handleResponse(request, HttpStatusCode.FOUND);
                        response.setLocation("/401.html");
                        sendResponse(outputStream, response);
                        return;
                    }

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
