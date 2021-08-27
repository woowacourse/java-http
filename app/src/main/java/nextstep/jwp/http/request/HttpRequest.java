package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import nextstep.jwp.http.request.body.Body;
import nextstep.jwp.http.request.headers.Headers;
import nextstep.jwp.http.request.requestline.RequestLine;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final Body body;

    private HttpRequest(RequestLine requestLine, Headers headers, Body body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        RequestLine requestLine = RequestLine.parse(bufferedReader.readLine());
        Headers headers = Headers.parse(bufferedReader);
        Body body = extractBody(bufferedReader, headers);

        return new HttpRequest(requestLine, headers, body);
    }

    private static Body extractBody(BufferedReader bufferedReader, Headers headers) throws IOException {
        if (headers.requestHasBody()) {
            int contentLength = headers.getContentLength();
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);

            return new Body(String.valueOf(buffer));
        }

        return Body.empty();
    }
}
