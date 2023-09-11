package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.Headers;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {

    private static final String REQUEST_API_DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final String QUERY_STRING_SYMBOL = "?";

    private static final String DOT = ".";
    private static final int START_LINE_SIZE = 3;

    private final HttpMethod method;
    private final String uri;
    private final String version;
    private Headers headers;
    private RequestBody requestBody;
    private QueryString queryString;

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        final String requestApi = bufferedReader.readLine();
        final String[] apiInfo = requestApi.split(REQUEST_API_DELIMITER);

        if (apiInfo.length != START_LINE_SIZE) {
            throw new IllegalArgumentException("잘못된 http 요청 입니다.");
        }

        this.method = HttpMethod.valueOf(apiInfo[HTTP_METHOD_INDEX]);
        this.uri = apiInfo[REQUEST_URI_INDEX];
        this.version = apiInfo[HTTP_VERSION_INDEX];
        initHeaders(bufferedReader);
        initRequestBody(bufferedReader);
        initQueryString();
    }

    private void initHeaders(final BufferedReader bufferedReader) throws IOException {
        headers = Headers.from(bufferedReader);
    }

    private void initRequestBody(final BufferedReader bufferedReader) throws IOException {
        if (headers.containsHeader("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            requestBody = RequestBody.of(bufferedReader, contentLength);
        }
    }

    private void initQueryString() {
        if (hasQueryString()) {
            this.queryString = QueryString.of(uri);
        }
    }

    public boolean hasQueryString() {
        return uri.contains(QUERY_STRING_SYMBOL);
    }

    public String getUri() {
        if (hasQueryString()) {
            final int queryIndex = uri.indexOf(QUERY_STRING_SYMBOL);
            return uri.substring(0, queryIndex);
        }
        return uri;
    }

    public boolean isStaticRequest() {
        return uri.contains(DOT) || uri.equals("/");
    }

    public String getExtension() {
        final int dotIndex = uri.indexOf(DOT);
        return uri.substring(dotIndex + 1);
    }

    public QueryString getQueryString() {
        return queryString;
    }

    public boolean isGetRequest() {
        return method.isGet();
    }

    public boolean hasRequestBody() {
        return Objects.nonNull(requestBody);
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public boolean hasCookie() {
        return headers.hasCookie();
    }

    public boolean hasJSessionId() {
        if (hasCookie()) {
            final HttpCookie cookie = headers.getCookie();
            return cookie.hasJSessionId();
        }
        return false;
    }

    public Session getSession(boolean create) {
        if (hasJSessionId()) {
            final HttpCookie cookie = headers.getCookie();
            final String jsessionid = cookie.getJsessionid();
            Session session = SessionManager.findSession(jsessionid);
            if (Objects.nonNull(session)) {
                return session;
            }
        }

        if (!create) {
            return null;
        }
        final Session session = new Session();
        SessionManager.add(session);
        return session;
    }
}
