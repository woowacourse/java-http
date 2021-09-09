package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import nextstep.jwp.http.entity.HttpBody;
import nextstep.jwp.http.entity.HttpHeaders;
import nextstep.jwp.http.entity.RequestLine;

public class HttpRequestReader {

    private HttpRequestReader() {
    }

    public static HttpRequest read(InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        RequestLine requestLine = RequestLine.of(reader.readLine());
        HttpHeaders httpHeaders = readHeaders(reader);

        HttpBody httpBody = readBody(reader, httpHeaders);
        return HttpRequest.of(requestLine, httpHeaders, httpBody);
    }

    private static HttpHeaders readHeaders(BufferedReader reader) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        String line = reader.readLine();
        while (!"".equals(line)) {
            httpHeaders.addHeader(line);
            line = reader.readLine();
        }
        return httpHeaders;
    }

    private static HttpBody readBody(BufferedReader reader, HttpHeaders httpHeaders) throws IOException {
        if (httpHeaders.hasHeaderName("Content-Length")) {
            int contentLength = Integer.parseInt(httpHeaders.get("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return HttpBody.of(new String(buffer));
        }
        return HttpBody.empty();
    }
}
