package org.apache.coyote;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.request.RequestHeader;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.request.RequestPath;

public class HttpRequestParser {

    private static final String PARAM_DELIMITER = "&";
    private static final char PAIR_DELIMITER = '=';
    private static final char PATH_DELIMITER = '?';
    private static final char HEADER_DELIMITER = ':';
    private static final int REQUEST_LINE_TOKENS = 3;
    private static final String EMPTY_BODY = "";

    private HttpRequestParser() {
    }

    public static HttpRequest parseRequest(InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            RequestLine requestLine = parseRequestLine(reader.readLine());
            RequestHeader header = parseHeaders(reader);
            RequestBody body = parseBody(reader, header.getHeader("Content-Length"));

            return new HttpRequest(requestLine, header, body);

        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    private static RequestLine parseRequestLine(String requestLine) {
        validateRequestLine(requestLine);

        StringTokenizer tokenizer = new StringTokenizer(requestLine);
        String method = tokenizer.nextToken();
        RequestPath path = parsePath(tokenizer.nextToken());
        String protocol = tokenizer.nextToken();

        return new RequestLine(method, path, protocol);
    }

    private static RequestPath parsePath(String pathInfo) {
        int queryIndex = pathInfo.indexOf(PATH_DELIMITER);

        if (queryIndex == -1) {
            return new RequestPath(pathInfo, new HashMap<>());
        }

        return new RequestPath(pathInfo.substring(0, queryIndex), parseQueryParams(pathInfo.substring(queryIndex + 1)));
    }

    private static Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> params = new HashMap<>();

        for (String pair : queryString.split(PARAM_DELIMITER)) {
            addQueryParameter(pair, params);
        }

        return params;
    }

    private static void addQueryParameter(String pair, Map<String, String> params){
        int equalIndex = pair.indexOf(PAIR_DELIMITER);
        if (equalIndex == -1) {
            return;
        }
        params.put(pair.substring(0, equalIndex), pair.substring(equalIndex + 1));
    }

    private static RequestHeader parseHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(HEADER_DELIMITER);
            if (colonIndex > 0) {
                String headerName = line.substring(0, colonIndex).trim();
                String headerValue = line.substring(colonIndex + 1).trim();
                headers.put(headerName, headerValue);
            }
        }

        return new RequestHeader(headers);
    }

    private static RequestBody parseBody(BufferedReader reader, String contentLength) throws IOException {
        final String formDataBody = getBodyString(reader, contentLength);
        return new RequestBody(parseQueryParams(formDataBody));
    }

    private static String getBodyString(BufferedReader reader, String contentLength) throws IOException {
        if (contentLength == null) {
            return EMPTY_BODY;
        }

        int length = Integer.parseInt(contentLength);
        if (length == 0) {
            return EMPTY_BODY;
        }

        char[] buffer = new char[length];
        reader.read(buffer, 0, length);
        return new String(buffer);
    }

    private static void validateRequestLine(String requestLine) {
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("요청 형식이 올바르지 않습니다.");
        }

        final StringTokenizer tokenizer = new StringTokenizer(requestLine);
        if(tokenizer.countTokens() != REQUEST_LINE_TOKENS) {
            throw new IllegalArgumentException("요청 형식이 올바르지 않습니다.");
        }
    }
}
