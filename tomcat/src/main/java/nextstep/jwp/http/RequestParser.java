package nextstep.jwp.http;

import nextstep.jwp.exception.CustomNotFoundException;
import nextstep.jwp.exception.FileAccessException;
import org.apache.http.HttpHeader;
import org.apache.http.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestParser {

    private RequestParser() {
    }

    public static Request parse(final BufferedReader bufferedReader) {
        final RequestInfo requestInfo = extractRequestInfo(bufferedReader);
        final Headers headers = extractHeaders(bufferedReader);
        final String body = extractBody(bufferedReader);
        return new Request(requestInfo, headers, body);
    }

    private static RequestInfo extractRequestInfo(final BufferedReader bufferedReader) {
        final String line = readLine(bufferedReader);
        final Optional<HttpMethod> httpMethod = HttpMethod.find(line);
        if (httpMethod.isPresent()) {
            final String uri = getUri(line);
            final String queryString = getQueryString(line);
            return new RequestInfo(httpMethod.get(), uri, queryString);
        }
        throw new CustomNotFoundException("요청 정보를 찾을 수 없음");
    }

    private static String getUri(final String line) {
        final String url = line.split(" ")[1];
        return url.split("\\?")[0];
    }

    private static String getQueryString(final String line) {
        final String url = line.split(" ")[1];
        final String[] split = url.split("\\?");
        if (split.length <= 1) {
            return null;
        }
        return split[1];
    }

    private static Headers extractHeaders(final BufferedReader bufferedReader) {
        final Headers headers = new Headers();
        String line;
        while (isNotEnd(line = readLine(bufferedReader))) {
            final String[] headerKeyValue = line.split(": ");
            final Optional<HttpHeader> httpHeader = HttpHeader.find(headerKeyValue[0]);
            httpHeader.ifPresent(header -> headers.put(header, headerKeyValue[1].trim()));
        }
        return headers;
    }

    private static String extractBody(final BufferedReader bufferedReader) {
        final List<String> builder = new ArrayList<>();
        String line;
        while (isNotEnd(line = readLine(bufferedReader))) {
            builder.add(line);
        }
        return String.join("\r\n", builder);
    }

    private static boolean isNotEnd(final String line) {
        return line != null && !line.isBlank();
    }

    private static String readLine(final BufferedReader bufferedReader) {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new FileAccessException();
        }
    }
}
