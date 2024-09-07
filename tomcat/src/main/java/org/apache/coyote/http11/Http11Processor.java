package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionGenerator;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Queries;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseCookie;
import org.apache.coyote.http11.response.ResponseFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Set<String> SERVING_PATHS = Set.of(
            "/login",
            "/register",
            "/index"
    );
    private final static SessionManager SESSION_MANAGER = new SessionManager();

    private final Socket connection;
    private final SessionGenerator sessionGenerator;

    public Http11Processor(Socket connection, SessionGenerator sessionGenerator) {
        this.connection = connection;
        this.sessionGenerator = sessionGenerator;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest request = HttpRequest.of(bufferedReader);
            HttpResponse response = getResponse(request);
            String formattedResponse = response.toResponse();

            outputStream.write(formattedResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getResponse(HttpRequest request) throws IOException {
        String requestPath = request.getPath();
        if (requestPath.equals("/login") && request.getMethod() == HttpMethod.GET && isLoginUser(request)) {
            return HttpResponse.createRedirectResponse(HttpStatus.FOUND, "/index.html");
        }
        if (SERVING_PATHS.contains(requestPath) && request.isQueriesEmpty() && request.getMethod() == HttpMethod.GET) {
            requestPath += ".html";
        }
        if (requestPath.contains(".")) {
            return getFileResponse(requestPath);
        }
        if (requestPath.equals("/login") && request.getMethod() == HttpMethod.POST) {
            return login(request);
        }
        if (requestPath.equals("/register") && request.getMethod() == HttpMethod.POST) {
            return register(request);
        }
        throw new IllegalArgumentException("요청을 처리할 수 없습니다.");
    }

    private boolean isLoginUser(HttpRequest request) {
        return request.getSessionId()
                .map(this::isLoginUser)
                .orElse(false);
    }

    private boolean isLoginUser(String sessionId) {
        HttpSession session = SESSION_MANAGER.findSession(sessionId);
        return session != null && session.getAttribute("user") != null;
    }

    private HttpResponse getFileResponse(String requestPath) {
        try {
            URL resource = getClass().getClassLoader().getResource("static/" + requestPath);
            if (resource == null) {
                throw new IllegalArgumentException("존재하지 않는 자원입니다: " + requestPath);
            }
            ResponseFile responseFile = ResponseFile.of(resource);
            return HttpResponse.createFileResponse(responseFile);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HttpResponse.createRedirectResponse(
                    HttpStatus.FOUND,
                    "/404.html"
            );
        }
    }

    private HttpResponse register(HttpRequest request) {
        Queries queries = Queries.of(request.getBody());
        String account = queries.get("account");

        User user = new User(account, queries.get("password"), queries.get("email"));

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            String message = "이미 존재하는 사용자입니다: " + account;
            log.warn(message);
            return HttpResponse.createTextResponse(HttpStatus.CONFLICT, message);
        }
        InMemoryUserRepository.save(user);
        return HttpResponse.createRedirectResponse(HttpStatus.FOUND, "/index.html");
    }

    private HttpResponse login(HttpRequest request) {
        String requestBody = request.getBody();
        Queries queries = Queries.of(requestBody);
        String account = queries.get("account");
        String password = queries.get("password");
        validateLoginRequest(account, password);

        try {
            User user = getLoginUser(account, password);
            Session session = sessionGenerator.create();
            session.setAttribute("user", user);
            SESSION_MANAGER.add(session);
            ResponseCookie sessionCookie = ResponseCookie.of(session);
            Map<String, String> headers = new HashMap<>();
            headers.put("Location", "/index.html");
            headers.put("Set-Cookie", sessionCookie.toResponse());
            return new HttpResponse(HttpStatus.FOUND, headers);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return HttpResponse.createRedirectResponse(HttpStatus.FOUND, "/401.html");
        }
    }

    private User getLoginUser(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return user;
    }

    private void validateLoginRequest(String account, String password) {
        if (account == null || account.isEmpty()) {
            throw new IllegalArgumentException("account는 비어 있을 수 없습니다.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("password는 비어 있을 수 없습니다.");
        }
    }
}
