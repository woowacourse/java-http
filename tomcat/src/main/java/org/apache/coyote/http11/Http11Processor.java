package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

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
                final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream()
        ) {
            final SessionManager sessionManager = SessionManager.getInstance();

            final Request request = new Request(inputStream);
            final String requestURI = request.getRequestURI();
            final String method = request.getMethod();
            final String requestBody = request.getMessageBody();
            final Cookies requestCookies = request.getCookies();
            final Response response = new Response(outputStream);

            if (requestURI.equals("/") || requestURI.equals("/index.html")) {
                response.sendResource("/index.html");
                return;
            }
            if (requestURI.equals("/login") && method.equals("GET")) {
                if (requestCookies != null && requestCookies.hasValue("JSESSIONID")) {
                    final String sessionId = requestCookies.getValue("JSESSIONID");
                    if (sessionManager.contains(sessionId)) {
                        response.sendRedirection("/index.html");
                        return;
                    }
                }
                response.sendResource("/login.html");
                return;
            }
            if (requestURI.startsWith("/login") && method.equals("POST")) {
                final String[] split = requestBody.split("&");
                final String account = split[0].split("=")[1];
                final String password = split[1].split("=")[1];
                InMemoryUserRepository.findByAccount(account).ifPresentOrElse(
                        user -> {
                            if (user.checkPassword(password)) {
                                final String sessionId = UUID.randomUUID().toString();
                                final Session session = new Session(sessionId);
                                session.setAttribute("user", user);
                                sessionManager.add(session);
                                final Cookies responseCookies = new Cookies(Map.of("JSESSIONID", sessionId));
                                response.addCookies(responseCookies);
                                try {
                                    response.sendRedirection("/index.html");
                                } catch (IOException e) {
                                    throw new UncheckedServletException(e);
                                }
                                return;
                            }
                            try {
                                response.sendRedirection("/401.html");
                            } catch (IOException e) {
                                throw new UncheckedServletException(e);
                            }
                        },
                        () -> {
                            try {
                                response.sendRedirection("/401.html");
                            } catch (IOException e) {
                                throw new UncheckedServletException(e);
                            }
                        }
                );
                return;
            }
            if (requestURI.equals("/register") && method.equals("GET")) {
                response.sendResource("/register.html");
                return;
            }
            if (requestURI.equals("/register") && method.equals("POST")) {
                final String[] split = requestBody.split("&");
                final String account = split[0].split("=")[1];
                final String email = split[1].split("=")[1].replace("%40", "@");
                final String password = split[2].split("=")[1];
                InMemoryUserRepository.save(new User(account, password, email));
                response.sendRedirection("/index.html");
                return;
            }

            response.sendResource(requestURI);
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
