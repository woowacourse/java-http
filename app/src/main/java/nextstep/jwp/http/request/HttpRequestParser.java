package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpRequestParser {

    public HttpRequest parse(InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = getBufferedReader(inputStream);
        final RequestLine requestLine = getParsedRequestLine(bufferedReader);
        final RequestHeaders headers = getParsedHeaders(bufferedReader);
        final RequestBody body = getParsedBody(bufferedReader, requestLine, headers);
        return new HttpRequest(requestLine, headers, body);
    }

    private BufferedReader getBufferedReader(InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new BufferedReader(inputStreamReader);
    }

    private RequestLine getParsedRequestLine(BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();
        validateNonNull(firstLine);
        return new RequestLine(firstLine);
    }

    private RequestHeaders getParsedHeaders(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new ConcurrentHashMap<>();
        String line = null;
        while (!"".equals(line)) {
            line = bufferedReader.readLine();
            validateNonNull(line);
            parseHeader(headers, line);
        }
        return new RequestHeaders(headers);
    }

    private RequestBody getParsedBody(BufferedReader bufferedReader, RequestLine requestLine, RequestHeaders headers) throws IOException {
        if (requestLine.hasMethod(HttpMethod.POST)) {
            int contentLength = headers.getContentLength();
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            final String body = new String(buffer);
            return new RequestBody(body);
        }
        return RequestBody.empty();
    }

    private void parseHeader(Map<String, String> headers, String line) {
        if (line.contains(":")) {
            final String key = line.split(":")[0];
            final String value = line.split(":")[1];
            headers.put(key, value);
        }
    }

    private void validateNonNull(String line) throws IOException {
        if (line == null) {
            throw new IOException("BufferedReader가 readLine()으로 읽은 결과가 null 입니다.");
        }
    }
}
