package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders header;
    private final RequestBody body;

    private HttpRequest(RequestLine requestLine, HttpHeaders headers,
        RequestBody body) throws IOException {
        this.requestLine = requestLine;
        this.header = headers;
        this.body = body;
    }

    public static HttpRequest parse (InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        RequestLine requestLine = RequestLine.parse(bufferedReader.readLine());
        HttpHeaders headers = HttpHeaders.parse(bufferedReader);
        RequestBody body = RequestBody.parse(bufferedReader, headers);

        return new HttpRequest(requestLine, headers, body);

    }
}
