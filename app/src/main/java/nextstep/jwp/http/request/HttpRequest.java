package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.http.cookie.HttpCookie;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final String requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, String requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        RequestLine requestLine = readRequestLine(bufferedReader);
        RequestHeaders headers = readHeaders(bufferedReader);

        if (headers.hasContent()) {
            return new HttpRequest(requestLine, headers, readBody(bufferedReader, headers.contentLength()));
        }
        return new HttpRequest(requestLine, headers, "");
    }

    private static RequestLine readRequestLine(BufferedReader bufferedReader) throws IOException {
        return RequestLine.of(bufferedReader.readLine());
    }

    private static RequestHeaders readHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> headers = new ArrayList<>();
        String tempLine;
        while (!(tempLine = bufferedReader.readLine()).equals("")) {
            headers.add(tempLine);
        }
        return RequestHeaders.of(headers);
    }

    private static String readBody(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public RequestLine requestLine() {
        return requestLine;
    }

    public RequestHeaders requestHeaders() {
        return requestHeaders;
    }

    public String requestBody() {
        return requestBody;
    }

    public boolean isGet() {
        return requestLine.method().isGet();
    }

    public SourcePath sourcePath() {
        return requestLine.sourcePath();
    }

    public HttpMethod httpMethod() {
        return requestLine.method();
    }

    public HttpCookie httpCookie(){
        // TODO :: null 체크 방식 수정, 쿠키가 없는 경우
        return HttpCookie.of(requestHeaders.cookie());
    }

    public String sessionId() {
        // TODO :: null 체크 방식 수정, 쿠키가 없는 경우
        String cookieQueryString = requestLine.queryParams().get("Cookie");
        if (Objects.isNull(cookieQueryString)) {
            QueryParams cookie = QueryParams.of(cookieQueryString);
            return cookie.get("sessionId");
        }
        return null;
    }

    public HttpSession getSession() {
        String sessionId = sessionId();
        // TODO :: 세션 값이 없거나, 잘못된 경우 어떻게 처리할 것인지.

        if (Objects.isNull(sessionId)) {
            return null;
        }
        return HttpSessions.getSession(sessionId);
    }
}
