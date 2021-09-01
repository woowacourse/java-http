package nextstep.jwp.model.web.request;

import nextstep.jwp.model.web.Headers;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomHttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestBody requestBody;

    public CustomHttpRequest(RequestLine requestLine, Headers headers, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static CustomHttpRequest from(BufferedReader reader) throws IOException {
        RequestLine requestLine = new RequestLine(reader.readLine());
        Headers headers = new Headers(parseHttpHeaders(reader));
        RequestBody body = new RequestBody(parseHttpBody(reader, headers));

        return new CustomHttpRequest(requestLine, headers, body);
    }

    private static Map<String, String> parseHttpBody(BufferedReader reader, Headers headers) throws IOException {
        int contentLength = headers.getContentLength();
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String bodyLine = new String(buffer);
        bodyLine = URLDecoder.decode(bodyLine, "UTF-8");

        return Stream.of(bodyLine.split("&"))
                .map(line -> line.split("="))
                .collect(Collectors.toMap(line -> line[0], line -> line[1]));
    }

    private static Map<String, String> parseHttpHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line = "";

        while (!("".equals(line = reader.readLine()))) {
            String[] kV = line.split("; ");
            headers.put(kV[0], kV[1]);
        }

        return headers;
    }
}
