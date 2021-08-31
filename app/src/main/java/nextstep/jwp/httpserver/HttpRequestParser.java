package nextstep.jwp.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import nextstep.jwp.httpserver.domain.Body;
import nextstep.jwp.httpserver.domain.Cookie;
import nextstep.jwp.httpserver.domain.Headers;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.request.RequestLine;

public class HttpRequestParser {
    private static final String LAST_HEADER_SYMBOL = "";
    private static final String HEADER_DIVIDER = ":";
    private static final String PARAMETER_DIVIDER = "&";
    private static final String KEY_VALUE_DIVIDER = "=";

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        final RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        final Headers headers = extractAllHeaders(bufferedReader);
        final List<Cookie> cookies = extractCookieFromHeaders(headers);
        final Body body = extractRequestBody(requestLine, headers, bufferedReader);

        return new HttpRequest(requestLine, headers, cookies, body);
    }

    private static Headers extractAllHeaders(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();

        String line = bufferedReader.readLine();
        while (!LAST_HEADER_SYMBOL.equals(line)) {
            int index = line.indexOf(HEADER_DIVIDER);
            headers.put(line.substring(0, index), line.substring(index + 2));
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
        }

        return new Headers(headers);
    }

    private static List<Cookie> extractCookieFromHeaders(Headers headers) {
        final String cookieChain = headers.getCookie();
        if (cookieChain.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(cookieChain.split("; "))
                     .map(c -> c.split("=", 2))
                     .map(c -> new Cookie(c[0], c[1]))
                     .collect(Collectors.toList());
    }

    private static Body extractRequestBody(RequestLine requestLine, Headers headers, BufferedReader bufferedReader) throws IOException {
        final Map<String, String> param = new HashMap<>();
        int contentLength = Integer.parseInt(headers.contentLength());

        if (requestLine.isPost() && contentLength > 0) {
            getParameter(bufferedReader, param, contentLength);
        }

        return new Body(param);
    }

    private static void getParameter(BufferedReader bufferedReader, Map<String, String> param, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);

        String[] parameters = requestBody.split(PARAMETER_DIVIDER);
        for (String parameter : parameters) {
            String[] keyValue = parameter.split(KEY_VALUE_DIVIDER);
            param.put(keyValue[0], keyValue[1]);
        }
    }
}
