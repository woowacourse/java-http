package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.Socket;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.catalina.session.JSession;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager;
    private final StaticResourceReader staticResourceReader;
    private final Function<HttpRequest, HttpResponse> defaultProcessor;
    private final Map<Predicate<HttpRequest>, Function<HttpRequest, HttpResponse>> processors;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.sessionManager = new SessionManager();
        this.staticResourceReader = new StaticResourceReader();
        this.defaultProcessor = this::processStaticResource;
        this.processors = Map.of(
                req -> req.method().equals("GET") && req.path().equals("/"), this::processRootPage,
                req -> req.method().equals("GET") && req.path().equals("/login"), this::processLoginPage,
                req -> req.method().equals("GET") && req.path().equals("/register"), this::processRegisterPage,
                req -> req.method().equals("POST") && req.path().equals("/register"), this::processRegister
        );
    }

    @Override
    public void run() {
//        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            List<String> requestLines = Http11InputStreamReader.read(inputStream);
            HttpRequest request = HttpRequest.parse(requestLines);

            for (final var processor : processors.entrySet()) {
                if (processor.getKey().test(request)) {
//                    log.debug(request.toString());
                    HttpResponse response = processor.getValue().apply(request);
//                    log.debug(response.toString());
                    outputStream.write(response.toMessage());
                    outputStream.flush();
                    return;
                }
            }

            HttpResponse response = defaultProcessor.apply(request);
            outputStream.write(response.toMessage());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse processRootPage(HttpRequest request) {
        return HttpResponse.builder(Status.OK)
                .addHeader("Content-Type", "text/html;charset=utf-8")
                .body("Hello world!".getBytes())
                .build();
    }

    private HttpResponse processLoginPage(HttpRequest request) {
        if (request.cookies().containsKey(JSession.COOKIE_NAME)) {
            HttpSession session = request.getSession(sessionManager);
            if (session != null) {
                User user = (User) Objects.requireNonNull(session).getAttribute("user");

                log.info("세션 로그인 성공! - 아이디 : {}, 세션 ID : {}", user.getAccount(), session.getId());

                return HttpResponse.builder(Status.FOUND)
                        .location("/index.html")
                        .build();
            }
        }

        if (request.parameters().containsKey("account") && request.parameters().containsKey("password")) {
            String account = request.parameters().get("account");
            String password = request.parameters().get("password");

            Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
            if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
                User user = optionalUser.get();
                String sessionId = UUID.randomUUID().toString();
                JSession session = new JSession(sessionId);
                session.setAttribute("user", user);
                sessionManager.add(session);

                log.info("계정 정보 로그인 성공! - 아이디 : {}, 세션 ID : {}", user.getAccount(), sessionId);

                return HttpResponse.builder(Status.FOUND)
                        .location("/index.html")
                        .addCookie(JSession.COOKIE_NAME, sessionId)
                        .build();
            }

            return HttpResponse.builder(Status.FOUND)
                    .location("/401.html")
                    .build();
        }

        return processStaticResource(request.updatePath("login.html"));
    }

    private HttpResponse processRegisterPage(HttpRequest request) {
        return processStaticResource(request.updatePath("register.html"));
    }

    private HttpResponse processRegister(HttpRequest request) {
        Map<String, String> body = HttpRequest.extractParameters(request.body());

        if (!body.containsKey("account") ||
                !body.containsKey("password") ||
                !body.containsKey("email")) {
            return HttpResponse.builder(Status.BAD_REQUEST)
                    .build();
        }

        String account = body.get("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return HttpResponse.builder(Status.CONFLICT)
                    .build();
        }

        InMemoryUserRepository.save(new User(account, body.get("password"), body.get("email")));
        return HttpResponse.builder(Status.FOUND)
                .location("/index.html")
                .build();
    }

    private HttpResponse processStaticResource(HttpRequest request) {
        String contentType = URLConnection.guessContentTypeFromName(request.path());
        final byte[] responseBody;
        try {
            responseBody = staticResourceReader.read(request.path());
            if (responseBody == null) {
                return HttpResponse.builder(Status.NOT_FOUND)
                        .build();
            }
            return HttpResponse.builder(Status.OK)
                    .contentType(contentType)
                    .body(responseBody)
                    .build();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return HttpResponse.builder(Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
