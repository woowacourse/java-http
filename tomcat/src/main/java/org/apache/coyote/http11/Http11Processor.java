package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private SessionManager sessionManager = SessionManager.getInstance();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = new HttpRequest(bufferedReader);
            HttpResponse httpResponse = new HttpResponse();
            HttpCookie httpCookie = new HttpCookie();

            String requestPath = httpRequest.getUrlPath();
            if (httpRequest.matchesUrlPath("/login") && httpRequest.matchesMethod("GET")) {
                if (httpRequest.hasCookie() && httpRequest.hasJSessionId()) {
                    httpResponse.addHttpResponseHeader("Location", "/index.html");
                    httpResponse.setHttpStatusCode(302);
                    httpResponse.setHttpStatusMessage("FOUND");
                }
            }

            if (httpRequest.matchesUrlPath("/login") && httpRequest.matchesMethod("POST")) {
                try {
                    User user = InMemoryUserRepository.findByAccount(httpRequest.findRequestBodyBy("account"))
                            .orElseThrow(IllegalArgumentException::new);

                    if (!user.checkPassword(httpRequest.findRequestBodyBy("password"))) {
                        throw new IllegalArgumentException();
                    }

                    if (!httpRequest.hasCookie() || !httpRequest.hasJSessionId()) {
                        UUID sessionId = UUID.randomUUID();
                        Session session = new Session(sessionId.toString());
                        session.setAttribute("user", user);
                        sessionManager.add(session);
                        UUID jSessionId = UUID.randomUUID();
                        httpCookie.addJSessionId(jSessionId.toString());
                        httpResponse.addHttpResponseHeader("Set-Cookie", httpCookie.getJSessionId());
                    }

                    httpResponse.addHttpResponseHeader("Location", "/index.html");
                    httpResponse.setHttpStatusCode(302);
                    httpResponse.setHttpStatusMessage("FOUND");
                    log.info("user : {}", user);

                } catch (IllegalArgumentException e) {
                    httpResponse.addHttpResponseHeader("Location", "/401.html");
                    httpResponse.setHttpStatusCode(302);
                    httpResponse.setHttpStatusMessage("FOUND");
                    sendHttpResponse(outputStream, httpRequest, httpResponse, requestPath);
                    return;
                }
            }

            if (httpRequest.matchesUrlPath("/register") && httpRequest.matchesMethod("POST")) {
                InMemoryUserRepository.save(new User(
                        httpRequest.findRequestBodyBy("account"),
                        httpRequest.findRequestBodyBy("password"),
                        httpRequest.findRequestBodyBy("email")
                ));
                log.info("savedUser = {}", InMemoryUserRepository.findByAccount(httpRequest.findRequestBodyBy("account")));
                httpResponse.addHttpResponseHeader("Location", "/index.html");
                httpResponse.setHttpStatusCode(302);
                httpResponse.setHttpStatusMessage("FOUND");
            }

            if (requestPath.equals("/")) {
                httpRequest.setUrlPath("/index");
            }

            sendHttpResponse(outputStream, httpRequest, httpResponse, requestPath);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void sendHttpResponse(OutputStream outputStream, HttpRequest httpRequest, HttpResponse httpResponse, String requestPath) throws IOException {
        httpResponse.setContentType(httpRequest);
        String responseBody = httpResponse.getResourceBody(requestPath);
        String response = httpResponse.toHttpResponse(responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
