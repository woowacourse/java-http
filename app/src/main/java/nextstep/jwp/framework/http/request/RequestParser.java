package nextstep.jwp.framework.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static nextstep.jwp.framework.http.common.Constants.HTTP_HEADER_SEPARATOR;
import static nextstep.jwp.framework.http.common.Constants.NEWLINE;

public class RequestParser {

    private final BufferedReader bufferedReader;

    private RequestParser(final InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public static RequestParser of(final InputStream inputStream) {
        return new RequestParser(inputStream);
    }

    public HttpRequest extractHttpRequest() throws IOException {
        final StringBuilder httpRequestHeader = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.length() == 0) {
                break;
            }
            httpRequestHeader.append(line).append(NEWLINE);
        }
        return parseClientRequest(httpRequestHeader.toString());
    }

    private HttpRequest parseClientRequest(final String header) throws IOException {
        final String requestLine = parseRequestLine(header);
        final Map<String, String> requestHttpHeader = parseRequestHttpHeader(header);
        final String requestBody = parseRequestBody(requestHttpHeader);
        return HttpRequest.from(requestLine, requestHttpHeader, requestBody);
    }

    private String parseRequestLine(final String request) {
        final int firstLineSeparatorIndex = request.indexOf(NEWLINE);
        return request.substring(0, firstLineSeparatorIndex);
    }

    private Map<String, String> parseRequestHttpHeader(final String request) {
        final int firstLineSeparatorIndex = request.indexOf(NEWLINE);
        final String requestHeaders = request.substring(firstLineSeparatorIndex + NEWLINE.length());

        final Map<String, String> requestHttpHeaders = new HashMap<>();
        final String[] httpHeaders = requestHeaders.split(NEWLINE);
        for (String httpHeader : httpHeaders) {
            final String[] headerKeyAndValue = httpHeader.split(HTTP_HEADER_SEPARATOR);
            requestHttpHeaders.put(headerKeyAndValue[0].trim(), headerKeyAndValue[1].trim());
        }
        return requestHttpHeaders;
    }

    private String parseRequestBody(final Map<String, String> requestHttpHeader) throws IOException {
        final String contentLengthHeader = "Content-Length";
        if (requestHttpHeader.containsKey(contentLengthHeader)) {
            final int contentLength = Integer.parseInt(requestHttpHeader.get(contentLengthHeader));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return null;
    }
}
