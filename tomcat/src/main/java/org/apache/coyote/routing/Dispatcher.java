package org.apache.coyote.routing;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.http11.header.Cookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dispatcher {
    public static final String PLAIN_CONTENT_TYPE = "text/plain";
    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);
    private static final String RESOURCES_PREFIX = "static";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String NOT_FOUND_PAGE = "/404.html";
    private final RequestMapping mapping;

    public Dispatcher(RequestMapping mapping) {
        this.mapping = mapping;
    }

    public void doDispatch(final HttpRequest request, final HttpResponse response) {
        Controller handler = mapping.getHandler(RouteKey.from(request))
                .orElseGet(() -> (request1, response1) -> request.getPath());

        String handle = handler.handle(request, response);
        render(handle, response);
        addSessionCookieIfMissing(request, response);
    }

    private void addSessionCookieIfMissing(final HttpRequest request, final HttpResponse response) {
        if (request.getCookie().get(JSESSIONID).isPresent()) {
            return;
        }
        final HttpSession session = request.getSession(true);
        response.addHeader(
                "Set-Cookie",
                Cookie.of(JSESSIONID, session.getId()).toHeaderValue()
        );
    }

    private void render(final String path, final HttpResponse response) {
        if (getContentType(path).equals(PLAIN_CONTENT_TYPE)) {
            response.setContentType(getContentType(path));
            response.setBody(path);
            return;
        }
        if (isResourcePresent(path)) {
            response.setContentType(getContentType(path));
            response.setBody(modelToView(path));
            return;
        }
        response.setStatus(HttpStatus.NOT_FOUND);
        response.setContentType(getContentType(NOT_FOUND_PAGE));
        response.setBody(modelToView(NOT_FOUND_PAGE));
    }

    private boolean isResourcePresent(String path) {
        final URL resource = getClass().getClassLoader().getResource(RESOURCES_PREFIX + path);
        return resource != null;
    }

    private String getContentType(String path) {
        if(path.startsWith("/")) {
            if (path.endsWith(".html")) {
                return "text/html;charset=utf-8";
            }
            if (path.endsWith(".css")) {
                return "text/css";
            }
            if (path.endsWith(".js")) {
                return "text/javascript";
            }
            return "";
        }
        return PLAIN_CONTENT_TYPE;
    }

    private String modelToView(String path) {
        final URL resource = getClass().getClassLoader().getResource(RESOURCES_PREFIX + path);
        if (resource == null) {
            log.info("존재하지 않는 파일 명입니다. 파일 경로를 확인해주세요. path: {}", path);
            return "";
        }
        try {
            URI uri = resource.toURI();
            return Files.readString(Paths.get(uri));
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }


}
