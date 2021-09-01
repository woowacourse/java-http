package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {
    private final HttpMethod httpMethod;
    private final String url;
    private final Map<String, String> requestParams;
    private final Map<String, String> requestHeaders;
    private final String requestBody;

    public HttpRequest(final InputStreamReader inputStream) {
        final BufferedReader bufferedReader = new BufferedReader(inputStream);
        try {
            List<String> requestLine = parseFirstLine(bufferedReader);
            this.httpMethod = HttpMethod.matchHttpMethod(requestLine.get(0));
            this.url = requestLine.get(1).substring(1);
            this.requestParams = parseParams(url);
            this.requestHeaders = parseHeaders(bufferedReader);
            this.requestBody = parseBody(bufferedReader, requestHeaders);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private Map<String, String> parseParams(final String requestUrl) {
        int paramIndex = requestUrl.indexOf("?");
        if (paramIndex < 0) {
            return new HashMap<>();
        }
        return parseBody(requestUrl, paramIndex);
    }

    private Map<String, String> parseBody(final String requestUrl, int paramIndex) {
        String[] splitParams = requestUrl.substring(paramIndex + 1).split("&");
        return Arrays.stream(splitParams)
                .map(param -> param.split("=", 2))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));
    }

    public Map<String, String> parseRequestBodyParams() {
        return parseBody(requestBody, -1);
    }

    public boolean containsFunctionInUrl(final String functionNames) {
        return url.contains(functionNames);
    }

    private Map<String, String> parseHeaders(final BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line.isEmpty()) {
                break;
            }
            String[] header = line.split(":");
            headers.put(header[0], header[1]);
        }
        return headers;
    }

    private List<String> parseFirstLine(final BufferedReader bufferedReader) {
        String firstLine;
        try {
            firstLine = bufferedReader.readLine();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        if (firstLine == null) {
            throw new IllegalArgumentException("request가 존재하지 않습니다.");
        }
        return Arrays.asList(firstLine.split(" "));
    }

    private String parseBody(final BufferedReader bufferedReader, final Map<String, String> requestHeaders) {
        if (requestHeaders.containsKey("Content-Length")) {
            try {
                int contentLength = Integer.parseInt(requestHeaders.get("Content-Length").trim());
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                return new String(buffer);
            } catch (IOException e) {
                throw new IllegalArgumentException("requestBody가 존재하지 않습니다.");
            }
        }
        return null;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }
}
