package org.apache.catalina.requesthandler;

import com.techcourse.exception.UnauthorizedException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Method;
import org.apache.coyote.http11.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final Map<RequestMapping, Controller> requestMappings;

    public RequestHandler() {
        this.requestMappings = new HashMap<>();
        initializeMappings();
    }

    private void initializeMappings() {
        RootController rootController = new RootController();
        StaticResourceController staticController = new StaticResourceController();
        UserController userController = new UserController();

        // Root mappings
        requestMappings.put(new RequestMapping("/", Method.GET), rootController);
        requestMappings.put(new RequestMapping("", Method.GET), rootController);

        // Static resource mappings
        requestMappings.put(new RequestMapping("/index.html", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/index", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/register.html", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/register", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/css/styles.css", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/assets/chart-area.js", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/js/scripts.js", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/assets/chart-bar.js", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/assets/chart-pie.js", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/assets/img/error-404-monochrome.svg", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/login.html", Method.GET), staticController);
        requestMappings.put(new RequestMapping("/login", Method.GET), staticController);

        // User controller mappings
        requestMappings.put(new RequestMapping("/register", Method.POST), userController);
        requestMappings.put(new RequestMapping("/login", Method.POST), userController);
    }

    public HttpResponse handleRequest(HttpRequest request) {
        try {
            for (var entry : requestMappings.entrySet()) {
                if (entry.getKey().isSupported(request)) {
                    Controller controller = entry.getValue();
                    HttpResponse response = HttpResponse.empty();
                    controller.service(request, response);
                    return response;
                }
            }
        } catch (UnauthorizedException e) {
            return responseUnauthorizedView();
        } catch (Exception e) {
            log.error("요청 처리 중 오류 발생", e);
            return responseSeverErrorView();
        }
        return responseNotFoundView();
    }

    private HttpResponse responseNotFoundView() {
        final byte[] body = readFile(Path.of("static", "404.html").toString());
        return HttpResponse.of(ResponseStatus.NOT_FOUND, ContentType.HTML, body);
    }

    private HttpResponse responseUnauthorizedView() {
        final byte[] body = readFile(Path.of("static", "401.html").toString());
        return HttpResponse.of(ResponseStatus.UNAUTHORIZED, ContentType.HTML, body);
    }

    private HttpResponse responseSeverErrorView() {
        final byte[] body = readFile(Path.of("static", "500.html").toString());
        return HttpResponse.of(ResponseStatus.INTERNAL_SERVER_ERROR, ContentType.HTML, body);
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
