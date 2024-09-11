package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.Controller;
import org.apache.catalina.RequestMapper;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.request.RequestLine;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager;
    private final RequestMapper requestMapper;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.sessionManager = SessionManager.getInstance();
        this.requestMapper = new RequestMapper(sessionManager);
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

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            RequestLine requestLine = new RequestLine(reader.readLine());
            HttpHeader httpHeader = new HttpHeader(readRequestHeaders(reader));
            RequestBody requestBody = readRequestBody(reader, httpHeader);

            HttpRequest request = new HttpRequest(requestLine, httpHeader, requestBody);
            HttpResponse response = new HttpResponse();
            handle(request, response);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> readRequestHeaders(BufferedReader reader) throws IOException {
        List<String> rawHeaders = new ArrayList<>();

        while (reader.ready()) {
            String line = reader.readLine();
            if (line.isBlank()) {
                break;
            }
            rawHeaders.add(line);
        }

        return rawHeaders;
    }

    private RequestBody readRequestBody(BufferedReader reader, HttpHeader httpHeader) throws IOException {
        if (httpHeader.contains(HttpHeaderType.CONTENT_LENGTH.getName())) {
            int contentLength = Integer.parseInt(httpHeader.get(HttpHeaderType.CONTENT_LENGTH.getName()));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new RequestBody(new String(buffer));
        }
        return null;
    }

    private void handle(HttpRequest request, HttpResponse response) {
        Controller controller = requestMapper.getController(request);
        controller.service(request, response);

//        if (request.pointsTo(GET, "/")) {
//            return HttpResponse.ok("Hello world!");
//        }
//
//        if (request.pointsTo(GET, "/login")) {
//            return getLoginPage(request);
//        }
//
//        if (request.pointsTo(POST, "/login")) {
//            return login(request);
//        }
//
//        if (request.pointsTo(POST, "/register")) {
//            return saveUser(request);
//        }
//
//        try {
//            return HttpResponse.withStaticFile(request.getPath().substring(1));
//        } catch (Exception e) {
//            return HttpResponse.notFound();
//        }
    }

    private HttpResponse saveUser(HttpRequest request) {
        RequestBody requestBody = request.getRequestBody();

        if (!requestBody.containsExactly("account", "email", "password")) {
            throw new UncheckedServletException("올바르지 않은 Request Body 형식입니다.");
        }

        String account = requestBody.get("account");
        String email = requestBody.get("email");
        String password = requestBody.get("password");

        if (!InMemoryUserRepository.existsByAccount(account)) {
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            String sessionId = saveSessionAndGetId(user);
            return HttpResponse.redirectTo("/index.html")
                    .setCookie(HttpCookie.ofSessionId(sessionId));
        }

        throw new UncheckedServletException("이미 존재하는 ID입니다.");
    }

    private String saveSessionAndGetId(User user) {
        String sessionId = sessionManager.generateId();
        sessionManager.add(sessionId, Session.ofUser(user));
        return sessionId;
    }
}
