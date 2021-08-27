package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import nextstep.jwp.http.common.Body;
import nextstep.jwp.http.request.headers.RequestHeaders;
import nextstep.jwp.http.request.requestline.RequestLine;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final Body body;

    private HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, Body body) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.body = body;
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        RequestLine requestLine = RequestLine.parse(bufferedReader.readLine());
        RequestHeaders requestHeaders = RequestHeaders.parse(bufferedReader);
        Body body = extractBody(bufferedReader, requestHeaders);

        return new HttpRequest(requestLine, requestHeaders, body);
    }

    private static Body extractBody(BufferedReader bufferedReader, RequestHeaders requestHeaders) throws IOException {
        if (requestHeaders.requestHasBody()) {
            int contentLength = requestHeaders.getContentLength();
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);

            return new Body(String.valueOf(buffer));
        }

        return Body.empty();
    }
}
