package nextstep.jwp.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HttpRequest {

    private static final String CRLF = "\r\n";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest(RequestLine requestLine, Map<String,String> headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(BufferedReader bufferedReader) {
        try {
            final RequestLine requestLine = RequestLine.of(bufferedReader.readLine());
            final Map<String, String> headers = extractHeaders(bufferedReader);
            final String body = extractBody(bufferedReader, headers);

            return new HttpRequest(requestLine, headers, body);
        } catch (IOException e) {
            // TODO
            throw new IllegalArgumentException();
        }
    }

    private static Map<String, String> extractHeaders(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line = bufferedReader.readLine();
        while(!"".equals(line)) {
            final String[] keyValue = line.split(": ");
            headers.put(keyValue[0], keyValue[1]);
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
        }
        return headers;
    }

    private static String extractBody(BufferedReader bufferedReader, Map<String, String> headers) throws IOException {
        if (bufferedReader.ready()) {
            final int contentLength = Integer.parseInt(headers.get("Content-Length"));
            final char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return "";
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public URI toURI() {
        return requestLine.getURI();
    }

    public String findHeaderValue(String key) {
        return headers.getOrDefault(key, "");
    }
}
