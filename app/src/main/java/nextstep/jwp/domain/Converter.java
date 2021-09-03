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
    private static final String HEADER_DELIMITER = ": ";
    private static final int REQUEST_LINE_INDEX = 0;
    private static final int METHOD = 0;
    private static final int URI = 1;
    private static final int PROTOCOL_VERSION = 2;
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String BLANK = " ";
    private static final String REQUEST_BODY_DELIMITER = "&";
    private static final String REQUEST_BODY_KEY_VALUE_DELIMITER = "=";
    private static final int KEY = 0;
    private static final int VALUE = 1;

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
        String[] requestLineArr = splitRequestHeaders[REQUEST_LINE_INDEX].split(BLANK);

        RequestLine requestLine = new RequestLine(requestLineArr[METHOD], Uri.of(requestLineArr[URI]), requestLineArr[PROTOCOL_VERSION]);
        Map<String, String> httpHeaders = createRequestHeaders(splitRequestHeaders);
        RequestBody requestBody = createRequestBody(httpHeaders, reader);

        return HttpRequest.of(requestLine, httpHeaders, requestBody);
    }

    private static Map<String, String> createRequestHeaders(String[] splitRequestHeaders) {
        Map<String, String> headers = new LinkedHashMap<>();
        for (int i = 1; i < splitRequestHeaders.length; i++) {
            String[] split = splitRequestHeaders[i].split(HEADER_DELIMITER);
            headers.put(split[KEY].trim(), split[VALUE].trim());

        }
        return headers;
    }

    private static RequestBody createRequestBody(Map<String, String> httpHeaders, BufferedReader reader) throws IOException {
        if (httpHeaders.get(CONTENT_LENGTH) != null) {
            int contentLength = Integer.parseInt(httpHeaders.get(CONTENT_LENGTH));
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
        String[] splitRequestBody = requestBody.split(REQUEST_BODY_DELIMITER);
        for (String requestParam : splitRequestBody) {
            String[] splitRequestParam = requestParam.split(REQUEST_BODY_KEY_VALUE_DELIMITER);
            requestBodyMap.put(splitRequestParam[KEY], splitRequestParam[VALUE]);
        }
        return requestBodyMap;
    }
}