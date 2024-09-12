package org.apache.coyote.processor;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import org.apache.coyote.http.HeaderName;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.ResourceType;
import org.apache.coyote.http.StatusCode;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.session.Session;
import org.apache.coyote.manager.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.sessionManager = new SessionManager();
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

            HttpRequest httpRequest = new HttpRequest(inputStream);
            HttpResponse httpResponse = new HttpResponse(httpRequest, ResourceType.NON_STATIC);

            if (httpRequest.isMethod(HttpMethod.GET)) {
                if (httpRequest.isStaticRequest()) {
                    httpResponse = new HttpResponse(httpRequest, ResourceType.STATIC);
                }

                if (!httpRequest.isStaticRequest()) {
                    if (httpRequest.isPath("/")) {
                        httpResponse.setStatusCode(StatusCode.OK);
                        httpResponse.setBody("/index.html");
                    }

                    if (httpRequest.isPath("/login")) {
                        if (httpRequest.hasSession() && sessionManager.isSessionExist(httpRequest.getJESSIONID())) {
                            httpResponse.setStatusCode(StatusCode.FOUND);
                            httpResponse.setHeader(HeaderName.LOCATION, "/index.html");
                        }
                        if (!httpRequest.hasCookie() || !httpRequest.hasJESSIONID()
                            || !sessionManager.isSessionExist(httpRequest.getJESSIONID())) {
                            httpResponse.setStatusCode(StatusCode.OK);
                            httpResponse.setBody("/login.html");
                        }
                    }

                    if (httpRequest.isPathWithQuery("/login")) {
                        String account = httpRequest.getQueryParam("account");
                        String password = httpRequest.getQueryParam("password");
                        Optional<User> user = InMemoryUserRepository.findByAccount(account);
                        if (user.isEmpty() || !user.get().checkPassword(password)) {
                            httpResponse.setStatusCode(StatusCode.UNAUTHORIZED);
                            httpResponse.setBody("/401.html");
                        }
                        if (user.isPresent() && user.get().checkPassword(password)) {
                            httpResponse.setStatusCode(StatusCode.FOUND);
                            httpResponse.setBody("/index.html");
                            httpResponse.generateJSESSIONID();
                            Session session = new Session(httpResponse.getJESSIONID());
                            session.setAttribute("user", user.get());
                            sessionManager.add(session);
                        }
                    }

                    if (httpRequest.isPath("/register")) {
                        httpResponse.setStatusCode(StatusCode.OK);
                        httpResponse.setBody("/register.html");
                    }
                }
            }

            if (httpRequest.isMethod(HttpMethod.POST)) {
                if (httpRequest.isPath("/register")) {
                    RequestBody requestBody = httpRequest.getBody();
                    String account = requestBody.get("account");
                    String password = requestBody.get("password");
                    String email = requestBody.get("email");
                    InMemoryUserRepository.save(new User(account, password, email));

                    httpResponse.setStatusCode(StatusCode.FOUND);
                    httpResponse.setHeader(HeaderName.LOCATION, "/index.html");
                }
            }

            outputStream.write(httpResponse.getReponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
