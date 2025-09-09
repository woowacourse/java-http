package org.apache.coyote;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.catalina.Session;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Method;
import org.apache.coyote.http11.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Map<RequestMapping, Function<HttpRequest, HttpResponse>> requestMappings;

    public RequestHandler() {
        this.requestMappings = new HashMap<>();
        requestMappings.put(new RequestMapping("/", Method.GET), this::handleRootView);
        requestMappings.put(new RequestMapping("", Method.GET), this::handleRootView);

        requestMappings.put(new RequestMapping("/index.html", Method.GET), this::handleStaticResource);
        requestMappings.put(new RequestMapping("/index", Method.GET), this::handleStaticResource);
        requestMappings.put(new RequestMapping("/register.html", Method.GET), this::handleStaticResource);
        requestMappings.put(new RequestMapping("/register", Method.GET), this::handleStaticResource);
        requestMappings.put(new RequestMapping("/css/styles.css", Method.GET), this::handleStaticResource);
        requestMappings.put(new RequestMapping("/assets/chart-area.js", Method.GET), this::handleStaticResource);
        requestMappings.put(new RequestMapping("/js/scripts.js", Method.GET), this::handleStaticResource);
        requestMappings.put(new RequestMapping("/assets/chart-bar.js", Method.GET), this::handleStaticResource);
        requestMappings.put(new RequestMapping("/assets/chart-pie.js", Method.GET), this::handleStaticResource);
        requestMappings.put(new RequestMapping("/assets/img/error-404-monochrome.svg", Method.GET),
                this::handleStaticResource);
        requestMappings.put(new RequestMapping("/login.html", Method.GET), this::handleStaticResource);
        requestMappings.put(new RequestMapping("/login", Method.GET), this::handleStaticResource);

        requestMappings.put(new RequestMapping("/register", Method.POST), this::handleRegister);
        requestMappings.put(new RequestMapping("/login", Method.POST), this::handleLogin);
    }

    public HttpResponse handleRequest(HttpRequest httpRequest) {
        try {
            for (var entry : requestMappings.entrySet()) {
                if (entry.getKey().isSupported(httpRequest)) {
                    return entry.getValue().apply(httpRequest);
                }
            }
        } catch (UnauthorizedException e) {
            return responseUnauthorizedView();
        }
        return responseNotFoundView();
    }

    private HttpResponse handleRootView(HttpRequest httpRequest) {
        final var body = "Hello world!".getBytes(StandardCharsets.UTF_8);
        return HttpResponse.of(ResponseStatus.OK, ContentType.HTML, body);
    }

    private HttpResponse handleStaticResource(HttpRequest httpRequest) {
        if (httpRequest.getPath().equals("/index") || httpRequest.getPath().equals("/index.html")) {
            if (httpRequest.getSession(false) == null) {
                return HttpResponse.forRedirect(ResponseStatus.FOUND, "/login.html");
            }
        }
        if (httpRequest.getPath().equals("/login") || httpRequest.getPath().equals("/login.html") ||
                httpRequest.getPath().equals("/register") || httpRequest.getPath().equals("/register.html")) {
            if (httpRequest.getSession(false) != null) {
                Session session = httpRequest.getSession(false);
                User user = (User) session.getAttribute("user");
                return HttpResponse.forRedirect(ResponseStatus.FOUND, "/index.html");
            }
        }
        final String staticFilePath = getStaticFilePath(httpRequest);
        final byte[] body = readFile(staticFilePath);
        final var contentType = ContentType.fromFileName(staticFilePath);
        return HttpResponse.of(ResponseStatus.OK, contentType, body);
    }

    private HttpResponse handleRegister(HttpRequest httpRequest) {
        Map<String, String> requestBody = httpRequest.getBody();
        final String account = requestBody.getOrDefault("account", "");
        final String password = requestBody.getOrDefault("password", "");
        final String email = requestBody.getOrDefault("email", "");
        if (account.isBlank() || password.isBlank() || email.isBlank()) {
            return handleStaticResource(httpRequest);
        }
        final var user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        final var session = httpRequest.getSession(true);
        session.setAttribute("user", user);
        HttpResponse httpResponse = HttpResponse.forRedirect(ResponseStatus.FOUND, "/index.html");
        httpResponse.setCookie("JSESSIONID", session.getId());
        return httpResponse;
    }

    private HttpResponse handleLogin(HttpRequest httpRequest) {
        Map<String, String> requestBody = httpRequest.getBody();
        final String account = requestBody.getOrDefault("account", "");
        final String password = requestBody.getOrDefault("password", "");
        if (account.isBlank() || password.isBlank()) {
            return handleStaticResource(httpRequest);
        }
        final User user = InMemoryUserRepository.findByAccount(account).orElseThrow(UnauthorizedException::new);
        if (!user.checkPassword(password)) {
            throw new UnauthorizedException();
        }
        log.info("회원 조회 성공 : {}", user);
        final var session = httpRequest.getSession(true);
        session.setAttribute("user", user);
        HttpResponse httpResponse = HttpResponse.forRedirect(ResponseStatus.FOUND, "/index.html");
        httpResponse.setCookie("JSESSIONID", session.getId());
        return httpResponse;
    }

    private HttpResponse responseNotFoundView() {
        final byte[] body = readFile(Path.of("static", "404.html").toString());
        return HttpResponse.of(ResponseStatus.NOT_FOUND, ContentType.HTML, body);
    }

    private HttpResponse responseUnauthorizedView() {
        final byte[] body = readFile(Path.of("static", "401.html").toString());
        return HttpResponse.of(ResponseStatus.UNAUTHORIZED, ContentType.HTML, body);
    }


    private String getStaticFilePath(HttpRequest httpRequest) {
        final var staticFilePath = "static" + httpRequest.getPath();
        if (httpRequest.getContentType() == ContentType.HTML && !staticFilePath.endsWith(".html")) {
            return staticFilePath + "." + ContentType.HTML.getExtension();
        }
        return staticFilePath;
    }

    private byte[] readFile(String staticFilePath) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(staticFilePath)) {
            if (is == null) {
                throw new IllegalArgumentException("존재하지 않는 리소스입니다.: " + staticFilePath);
            }
            return is.readAllBytes();
        } catch (IOException e) {
            log.error("파일을 불러오는데 실패했습니다. : {} {}", staticFilePath, e.getMessage(), e);
            throw new IllegalArgumentException("파일을 불러오는데 실패했습니다.: " + staticFilePath, e);
        }
    }

    record RequestMapping(
            String path,
            Method method
    ) {

        public boolean isSupported(HttpRequest httpRequest) {
            return httpRequest.getPath().equals(path)
                    && httpRequest.getMethod() == method;
        }
    }
}
