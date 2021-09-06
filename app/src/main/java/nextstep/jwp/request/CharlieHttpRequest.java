package nextstep.jwp.request;

import nextstep.jwp.exception.InvalidHttpSessionException;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.web.model.Cookie;
import nextstep.jwp.web.model.HttpCookie;
import nextstep.jwp.web.model.HttpSession;
import nextstep.jwp.web.model.HttpSessions;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class CharlieHttpRequest implements HttpRequest {
    private static final String CONTENT_LENGTH_HEADER_NAME = "Content-Length";
    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String CONTENT_TYPE_FORM_DATA = "application/x-www-form-urlencoded";
    private static final String COOKIE_HEADER_NAME = "Cookie";
    private static final String COOKIES_BOUNDARY = "; ";
    private static final String COOKIE_KEY_VALUE_BOUNDARY = "=";
    private static final int INDEX_OF_COOKIE_KEY = 0;
    private static final int INDEX_OF_COOKIE_VALUE = 1;
    private static final String SESSION_ID = "JSESSIONID";

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public CharlieHttpRequest(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static CharlieHttpRequest of(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.of(bufferedReader.readLine());
        RequestHeader requestHeader = RequestHeader.of(bufferedReader);
        RequestBody requestBody = RequestBody.empty();
        if (isFormData(requestHeader)) {
            requestBody = RequestBody.of(bufferedReader, requestHeader.getHeader(CONTENT_LENGTH_HEADER_NAME));
        }
        return new CharlieHttpRequest(requestLine, requestHeader, requestBody);
    }

    private static boolean isFormData(RequestHeader requestHeader) {
        return CONTENT_TYPE_FORM_DATA.equalsIgnoreCase(requestHeader.getHeader(CONTENT_TYPE_HEADER_NAME));
    }

    public HttpCookie getCookies() {
        String cookies = this.requestHeader.getHeader(COOKIE_HEADER_NAME);
        if (Objects.isNull(cookies)) {
            return HttpCookie.emptyCookies();
        }
        String[] splitCookies = cookies.split(COOKIES_BOUNDARY);
        return new HttpCookie(Arrays.stream(splitCookies).map(cookie -> cookie.split(COOKIE_KEY_VALUE_BOUNDARY, 2))
                .collect(Collectors.toMap(cookie -> cookie[INDEX_OF_COOKIE_KEY], cookie ->
                        new Cookie(cookie[INDEX_OF_COOKIE_KEY], cookie[INDEX_OF_COOKIE_VALUE]))));
    }

    @Override
    public HttpSession getSession(HttpResponse httpResponse) {
        HttpCookie httpCookie = this.getCookies();
        if (hasValidJSessionId(httpCookie)) {
            return HttpSessions.getSession(httpCookie.getJSessionId())
                    .orElseThrow(() -> new InvalidHttpSessionException("세션이 존재하지 않습니다."));
        }

        HttpSession httpSession = HttpSession.create();
        httpResponse.addCookie(new Cookie(SESSION_ID, httpSession.getId()));
        return HttpSessions.save(httpSession);
    }

    private boolean hasValidJSessionId(HttpCookie httpCookie) {
        if (httpCookie.hasJSessionId()) {
            String jSessionId = httpCookie.getJSessionId();
            return HttpSessions.isExistsId(jSessionId);
        }
        return false;
    }

    @Override
    public String getResourceName() {
        return this.requestLine.getRequestPath();
    }

    @Override
    public String getAttribute(String name) {
        return this.requestBody.getValue(name);
    }

    @Override
    public RequestLine getRequestLine() {
        return requestLine;
    }

    @Override
    public RequestHeader getRequestHeader() {
        return requestHeader;
    }
}
