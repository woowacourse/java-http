package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Objects;
import org.apache.catalina.Session;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpMethodType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final StaticFileHandler staticFileHandler = new StaticFileHandler();

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            HttpRequest request = parseRequest(inputStream);
            if (request == null) {
                return;
            }

            HttpResponse response = createResponse(request);
            sendResponse(outputStream, response);
            
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseRequest(final InputStream inputStream) throws IOException {
        try {
            return HttpRequest.from(inputStream);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 HTTP 요청: {}", e.getMessage());
            return null;
        }
    }

    private HttpResponse createResponse(final HttpRequest request) throws IOException {
        String requestPath = request.getPath();
        
        if (Objects.equals("/login", requestPath)) {
            if (request.getMethodType() == HttpMethodType.GET) {
                return handleLoginGetRequest(request);
            } else if (request.getMethodType() == HttpMethodType.POST) {
                return handleLoginRequest(request);
            }
        }
        
        if (Objects.equals("/register", requestPath) && request.getMethodType() == HttpMethodType.POST) {
            return handleRegisterRequest(request);
        }
        
        String resolvedPath = resolveFilePath(requestPath);
        return generateHttpResponse(resolvedPath);
    }

    private HttpResponse handleLoginRequest(final HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        
        if (account == null || password == null) {
            return HttpResponse.redirect("/401.html");
        }
        
        return processLoginCredentials(account, password, request);
    }

    private HttpResponse processLoginCredentials(final String account, final String password, final HttpRequest request) {
        return InMemoryUserRepository.findByAccount(account)
            .filter(user -> user.checkPassword(password))
            .map(user -> {
                Session session = request.getSession(true);
                session.setAttribute("user", user);
                String setCookieHeader = HttpCookie.createJSessionIdSetCookieHeader(session.getId());
                HttpResponse response = HttpResponse.redirect("/index.html");
                response.addHeader("Set-Cookie", setCookieHeader);
                log.info("로그인 성공: {}", user);
                return response;
            })
            .orElseGet(() -> {
                log.info("로그인 실패 - account: {}, password: {}", account, password);
                return HttpResponse.redirect("/401.html");});
    }

    private HttpResponse handleRegisterRequest(final HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return HttpResponse.redirect("/register.html");
        }
        
        User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);
        
        Session session = request.getSession(true);
        session.setAttribute("user", newUser);
        String setCookieHeader = HttpCookie.createJSessionIdSetCookieHeader(session.getId());
        HttpResponse response = HttpResponse.redirect("/index.html");
        response.addHeader("Set-Cookie", setCookieHeader);
        return response;
    }

    private HttpResponse handleLoginGetRequest(final HttpRequest request) {
        Session session = request.getSession(false);
        
        if (session != null) {
            User user = getUser(session);
            if (user != null) {
                return HttpResponse.redirect("/index.html");
            }
        }
        
        String resolvedPath = resolveFilePath("/login");
        try {
            return generateHttpResponse(resolvedPath);
        } catch (IOException e) {
            log.error("로그인 페이지 로드 실패", e);
            return HttpResponse.notFound();
        }
    }

    private User getUser(Session session) {
        return (User) session.getAttribute("user");
    }


    private String resolveFilePath(final String requestPath) {
        String htmlPath = requestPath + ".html";
        if (staticFileHandler.exists(htmlPath)) {
            return htmlPath;
        }
        
        return requestPath;
    }

    private HttpResponse generateHttpResponse(final String requestPath) throws IOException {
        if (Objects.equals("/", requestPath)) {
            return HttpResponse.ok("Hello world!", MimeType.TEXT_PLAIN);
        }
        
        return createFileResponse(requestPath);
    }

    private HttpResponse createFileResponse(final String requestPath) throws IOException {
        if (staticFileHandler.exists(requestPath)) {
            String fileContent = staticFileHandler.readFile(requestPath);
            MimeType mimeType = staticFileHandler.getContentType(requestPath);
            return HttpResponse.ok(fileContent, mimeType);
        } else {
            return HttpResponse.notFound();
        }
    }

    private void sendResponse(final java.io.OutputStream outputStream, final HttpResponse response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
