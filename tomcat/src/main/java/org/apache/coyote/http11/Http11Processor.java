package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import org.apache.catalina.HeaderName;
import org.apache.catalina.HttpMethod;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.HttpResponse;
import org.apache.catalina.RequestBody;
import org.apache.catalina.ResourceType;
import org.apache.catalina.Session;
import org.apache.catalina.StatusCode;
import org.apache.catalina.manager.SessionManager;
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

            if (httpRequest.isMethod(HttpMethod.GET)) {  // TODO: 404 추가하기
                // 정적 파일인 경우
                if (httpRequest.isStaticRequest()) {
                    httpResponse = new HttpResponse(httpRequest, ResourceType.STATIC);
                }

                // 정적 파일이 아닌 경우
                if (!httpRequest.isStaticRequest()) {
                    if (httpRequest.isPath("/")) {
                        httpResponse.setStatusCode(StatusCode._200);
                        httpResponse.setBody("/index.html");
                    }

                    if (httpRequest.isPath("/login")) {
                        // session이 있는 경우 다른 설정은 하지 않고, 쿠키에 그 세션 아이디를 넣어주고 리다이렉션한다.
                        if (httpRequest.hasCookie() && httpRequest.hasJESSIONID()
                            && sessionManager.isSessionExist(httpRequest.getJESSIONID())) { // TODO: 객체에게 옮기기
                            httpResponse.setStatusCode(StatusCode._302);
                            httpResponse.setHeader(HeaderName.LOCATION, "/index.html");
                        }
                        if (!httpRequest.hasCookie() || !httpRequest.hasJESSIONID()
                            || !sessionManager.isSessionExist(httpRequest.getJESSIONID())) {
                            httpResponse.setStatusCode(StatusCode._200);
                            httpResponse.setBody("/login.html");
                        }
                    }

                    if (httpRequest.isPathWithQuery("/login")) {
                        String account = httpRequest.getQueryParam("account");
                        String password = httpRequest.getQueryParam("password");
                        Optional<User> user = InMemoryUserRepository.findByAccount(account);
                        if (user.isEmpty() || !user.get().checkPassword(password)) {
                            httpResponse.setStatusCode(StatusCode._401);
                            httpResponse.setBody("/401.html");
                        }
                        if (user.isPresent() && user.get().checkPassword(password)) {
                            httpResponse.setStatusCode(StatusCode._302);
                            httpResponse.setBody("/index.html");
                            httpResponse.setJSESSIONID();
                            Session session = new Session(httpResponse.getJESSIONID());
                            session.setAttribute("user", user.get());
                            sessionManager.add(session);
                        }
                    }

                    if (httpRequest.isPath("/register")) {
                        httpResponse.setStatusCode(StatusCode._200);
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

                    httpResponse.setStatusCode(StatusCode._302);
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
