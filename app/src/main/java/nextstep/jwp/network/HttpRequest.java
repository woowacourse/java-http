package nextstep.jwp.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

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
            final String[] keyValue = line.split(":");
            headers.put(keyValue[0].trim(), keyValue[1].trim());
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

    public URI getURI() {
        return requestLine.getURI();
    }

    public Map<String, String> getBody() {
        final Map<String, String> bodyAsMap = new HashMap<>();
        final String[] params = body.split("&");
        for (String param : params) {
            final String[] keyAndValue = param.split("=");
            bodyAsMap.put(keyAndValue[0], keyAndValue[1]);
        }
        return bodyAsMap;
    }
}
