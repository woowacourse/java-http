package nextstep.jwp.domain;

import nextstep.jwp.domain.request.HttpRequest;
import nextstep.jwp.domain.request.RequestBody;
import nextstep.jwp.domain.request.RequestLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Converter {

    private static final String NEW_LINE = "\r\n";

    private Converter() {
    }

    public static HttpRequest convertToHttpRequest(final BufferedReader reader) throws IOException {
        StringBuilder requestHeaders = new StringBuilder();

        String line = reader.readLine();
        while (!"".equals(line)) {
            if (line == null) {
                break;
            }
            requestHeaders.append(line).append(NEW_LINE);
            line = reader.readLine();
        }

        return createHttpRequest(requestHeaders, reader);
    }

    private static HttpRequest createHttpRequest(StringBuilder requestHeaders, BufferedReader reader) throws IOException {
        String[] splitRequestHeaders = requestHeaders.toString().split(NEW_LINE);
        String[] requestLineArr = splitRequestHeaders[0].split(" ");

        RequestLine requestLine = new RequestLine(requestLineArr[0], Uri.of(requestLineArr[1]), requestLineArr[2]);
        Map<String, String> httpHeaders = createRequestHeaders(splitRequestHeaders);
        RequestBody requestBody = createRequestBody(httpHeaders, reader);

        return HttpRequest.of(requestLine, httpHeaders, requestBody);
    }

    private static Map<String, String> createRequestHeaders(String[] splitRequestHeaders) {
        Map<String, String> headers = new LinkedHashMap<>();
        for (int i = 1; i < splitRequestHeaders.length; i++) {
            String[] split = splitRequestHeaders[i].split(": ");
            headers.put(split[0].trim(), split[1].trim());

        }
        return headers;
    }

    private static RequestBody createRequestBody(Map<String, String> httpHeaders, BufferedReader reader) throws IOException {
        if (httpHeaders.get("Content-Length") != null) {
            int contentLength = Integer.parseInt(httpHeaders.get("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            Map<String, String> requestBodyMap = collectRequestBody(new String(buffer));
            return RequestBody.of(requestBodyMap);
        }
        return null;
    }

    private static Map<String, String> collectRequestBody(String requestBody) {
        if (requestBody.isEmpty()) {
            return null;
        }
        Map<String, String> requestBodyMap = new HashMap<>();
        String[] splitRequestBody = requestBody.split("&");
        for (String requestParam : splitRequestBody) {
            String[] splitRequestParam = requestParam.split("=");
            requestBodyMap.put(splitRequestParam[0], splitRequestParam[1]);
        }
        return requestBodyMap;
    }
}