package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

public class StaticResourceController extends AbstractController {

    private static final String ROOT_URI = "/";
    private static final String STATIC_RESOURCE_ROOT = "static";
    private static final String LOGIN = "/login";
    private static final String REGISTER = "/register";
    private static final String DOT_HTML = ".html";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        String requestUri = extractRequestPath(request.getUri());
        if (requestUri.equals(LOGIN) || requestUri.equals(REGISTER)) {
            requestUri += DOT_HTML;
        }

        final String contentType = getContentType(requestUri);
        final byte[] body = getResponseBody(requestUri);
        final String setCookie = createSetCookieHeader(request.getCookie(JSESSIONID));
        setOkResponse(response, contentType, body, setCookie);
    }

    private byte[] getResponseBody(final String requestUri) throws IOException {
        if (requestUri.equals(ROOT_URI)) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }
        final Path path = Path.of(getResourcePath(STATIC_RESOURCE_ROOT + requestUri));
        return Files.readAllBytes(path);
    }

    private String getResourcePath(final String path) {
        final URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new UncheckedServletException(new FileNotFoundException("리소스를 찾을 수 없습니다: " + path));
        }
        return resource.getPath();
    }

    private String getContentType(final String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css";
        }
        if (requestUri.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html;charset=utf-8";
    }

    private String extractRequestPath(final String requestUri) {
        final int queryStringIndex = requestUri.indexOf('?');
        if (queryStringIndex == -1) {
            return requestUri;
        }
        return requestUri.substring(0, queryStringIndex);
    }
}
