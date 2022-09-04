package org.apache.coyote.http;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.util.StringParser;

public class HttpRequest {

    private static final String QUERY_STRING_PREFIX = "?";

    private final HttpMethod httpMethod;
    private final String path;
    private final Map<String, String> queryParams;
    private final String contentType;
    private final HttpHeader header;

    private HttpRequest(final HttpMethod httpMethod, final String path, final Map<String, String> queryParams,
                        final String contentType, final HttpHeader header) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryParams = queryParams;
        this.contentType = contentType;
        this.header = header;
    }

    public static HttpRequest of(final String startLine, final HttpHeader headers) throws IOException {
        final String[] splitStartLine = startLine.split(" ");
        final HttpMethod httpMethod = HttpMethod.from(splitStartLine[0]);
        final String uri = splitStartLine[1];
        final String path = getPath(uri);
        final Map<String, String> queryParams = getQueryParams(uri);
        final String contentType = StringParser.toMimeType(uri);

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

    public boolean isRegister() {
        return httpMethod.isPost() && path.contains("register");
    }

    public boolean isLogin() {
        return httpMethod.isPost() && path.contains("login");
    }

    public boolean isLoginPage() {
        return httpMethod.isGet() && path.contains("login");
    }

    public boolean alreadyLogin() throws IOException {
        if (!header.hasSessionId()) {
            return false;
        }

        final Session session = SessionManager.findSession(header.getSessionId());
        final User user = (User) session.getAttribute("user");
        if (user == null) {
            return false;
        }
        return InMemoryUserRepository.findByAccount(user.getAccount())
                .isPresent();
    }

    public String getPath() {
        return path;
    }

    public String getContentType() {
        return contentType;
    }

    public int getContentLength() {
        return header.getContentLength();
    }
}
