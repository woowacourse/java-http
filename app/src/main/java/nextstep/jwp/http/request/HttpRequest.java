package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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

    public QueryParams requestParam() {
        return QueryParams.of(requestBody);
    }

    public SourcePath sourcePath() {
        return requestLine.sourcePath();
    }

    public HttpMethod httpMethod() {
        return requestLine.method();
    }

    public HttpCookie httpCookie() {
        return HttpCookie.of(requestHeaders.cookie());
    }

    public HttpSession getSession() {
        return httpCookie().getSession();
    }
}
