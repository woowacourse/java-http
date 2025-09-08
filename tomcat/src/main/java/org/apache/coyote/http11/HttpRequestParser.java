package org.apache.coyote.http11;

import com.http.enums.HttpMethod;
import com.http.enums.HttpStatus;
import com.techcourse.exception.HttpStatusException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.catalina.domain.HttpHeader;
import org.apache.catalina.domain.request.HttpRequest;
import org.apache.catalina.domain.request.HttpRequestBody;
import org.apache.catalina.domain.request.RequestStartLine;

public final class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parse(BufferedReader reader) throws IOException {
        final List<String> requestLines = parseRequestLines(reader);

        final RequestStartLine requestStartLine = RequestStartLine.from(requestLines);
        final Map<String, String> queryStrings = parseQueryStrings(requestLines);
        final HttpHeader header = HttpHeader.from(requestLines);
        final HttpRequestBody body = new HttpRequestBody(parseBody(reader, requestStartLine, header));

        return new HttpRequest(requestStartLine, queryStrings, header, body);
    }

    private static List<String> parseRequestLines(BufferedReader reader) throws IOException {
        List<String> requestLines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            requestLines.add(line);
        }

        return requestLines;
    }

    // TODO ?에 대한 파싱 처리 필요할 거 같음
    private static Map<String, String> parseQueryStrings(List<String> requestLines) {
        final String startLine = requestLines.getFirst();

        if (startLine == null || !startLine.contains("?")) {
            return Map.of();
        }

        String queryString = startLine.split(" ")[1].split("\\?")[1];
        String[] queries = queryString.split("&");

        return Stream.of(queries)
                .map(query -> query.split("="))
                .filter(query -> query.length == 2)
                .collect(Collectors.toMap(query -> query[0], query -> query[1]));
    }

    private static String parseBody(BufferedReader reader, RequestStartLine requestStartLine, HttpHeader httpHeader)
            throws IOException {
        // POST 요청이 아닌 경우 빈 문자열 반환
        if (requestStartLine.method() != HttpMethod.POST) {
            return "";
        }

        if (httpHeader.hasNotContentLength()) {
            throw new HttpStatusException("Length Required", HttpStatus.LENGTH_REQUIRED);
        }

        final int contentLength = httpHeader.getContentLength();
        if (contentLength < 0) {
            throw new HttpStatusException("Invalid Content-Length", HttpStatus.BAD_REQUEST);
        }

        if (contentLength == 0) {
            return "";
        }

        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

}
