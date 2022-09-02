package org.apache.coyote.http;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.util.StringParser;

public class HttpRequest {

    private static final String POST = "POST";
    private static final String GET = "GET";

    private static final String QUERY_STRING_PREFIX = "?";

    private final String httpMethod;
    private final String path;
    private final Map<String, String> queryParams;
    private final String contentType;
    private final Map<String, String> headers;

    private HttpRequest(final String httpMethod, final String path, final Map<String, String> queryParams,
                        final String contentType, final Map<String, String> headers) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryParams = queryParams;
        this.contentType = contentType;
        this.headers = headers;
    }

    public static HttpRequest of(final String startLine, final Map<String, String> headers) throws IOException {
        final String[] splitStartLine = startLine.split(" ");
        final String httpMethod = splitStartLine[0];
        final String uri = splitStartLine[1];
        final String path = getPath(uri);
        final Map<String, String> queryParams = getQueryParams(uri);
        final String contentType = getContentType(path);

        return new HttpRequest(httpMethod, path, queryParams, contentType, headers);
    }

    private static String getPath(final String uri) {
        if (!hasQueryString(uri)) {
            return uri;
        }
        final int prefixIndex = uri.indexOf(QUERY_STRING_PREFIX);
        return uri.substring(0, prefixIndex);
    }

    private static Map<String, String> getQueryParams(final String uri) {
        if (!hasQueryString(uri)) {
            return Collections.emptyMap();
        }
        final int prefixIndex = uri.indexOf(QUERY_STRING_PREFIX);
        final String queryString = uri.substring(prefixIndex + 1);

        return StringParser.toMap(queryString);
    }

    private static boolean hasQueryString(final String uri) {
        return uri.contains(QUERY_STRING_PREFIX);
    }

    private static String getContentType(final String path) throws IOException {
        final Path filePath = new File(path).toPath();
        return Files.probeContentType(filePath);
    }

    public boolean isRegister() {
        return isPost() && path.contains("register");
    }

    public boolean isLogin() {
        return isPost() && path.contains("login");
    }

    public boolean isLoginPage() {
        return isGet() && path.contains("login");
    }

    public boolean alreadyLogin() throws IOException {
        final String cookie = headers.get("Cookie");
        if (cookie == null) {
            return false;
        }
        if (!cookie.contains("JSESSION")) {
            return false;
        }

        final SessionManager sessionManager = new SessionManager();
        final HttpSession session = sessionManager.findSession(cookie.split("JSESSIONID=")[1]);
        final User user = (User) session.getAttribute("user");
        if (user == null) {
            return false;
        }
        return InMemoryUserRepository.findByAccount(user.getAccount())
                .isPresent();
    }

    public boolean isPost() {
        return httpMethod.equals(POST);
    }

    public boolean isGet() {
        return httpMethod.equals(GET);
    }

    public String getPath() {
        return path;
    }

    public String getContentType() {
        return contentType;
    }

    public int getContentLength() {
        if (headers.containsKey("Content-Length")) {
            return Integer.parseInt(headers.get("Content-Length"));
        }
        return 0;
    }
}
