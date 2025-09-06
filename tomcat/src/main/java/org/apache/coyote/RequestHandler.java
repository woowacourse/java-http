package org.apache.coyote;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
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

        requestMappings.put(new RequestMapping("/login.html", Method.GET), this::handleLogin);
        requestMappings.put(new RequestMapping("/login", Method.GET), this::handleLogin);
    }

    public HttpResponse handleRequest(HttpRequest httpRequest) {
        for (var entry : requestMappings.entrySet()) {
            if (entry.getKey().isSupported(httpRequest)) {
                return entry.getValue().apply(httpRequest);
            }
        }
        return responseNotFoundView();
    }

    private HttpResponse handleRootView(HttpRequest httpRequest) {
        final var body = "Hello world!".getBytes(StandardCharsets.UTF_8);
        return HttpResponse.of(ResponseStatus.OK, ContentType.HTML, body);
    }

    private HttpResponse handleStaticResource(HttpRequest httpRequest) {
        final String staticFilePath = getStaticFilePath(httpRequest);
        final byte[] body = readFile(staticFilePath);
        final var contentType = ContentType.fromFileName(staticFilePath);
        return HttpResponse.of(ResponseStatus.OK, contentType, body);
    }

    private HttpResponse handleLogin(HttpRequest httpRequest) {
        final String account = httpRequest.getQueryParameterValue("account");
        final String password = httpRequest.getQueryParameterValue("password");
        if (account.isBlank() || password.isBlank()) {
            return handleStaticResource(httpRequest);
        }
        final User user = InMemoryUserRepository.findByAccount(account).orElseThrow(() ->
                new IllegalArgumentException("회원이 존재하지 않습니다. : " + account));
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("회원이 존재하지 않습니다. : " + account);
        }
        log.info("회원 조회 성공 : {}", user);
        return handleStaticResource(httpRequest);
    }

    private HttpResponse responseNotFoundView() {
        final byte[] body = readFile(Path.of("static", "404.html").toString());
        return HttpResponse.of(ResponseStatus.NOT_FOUND, ContentType.HTML, body);
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
