package nextstep.jwp.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestConverter {

    private static final String NEW_LINE = "\r\n";

    private RequestConverter() {
    }

    public static HttpRequest convertToHttpRequest(final BufferedReader bufferedReader) throws IOException {
        StringBuilder requestHeaders = new StringBuilder();

        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            if (line == null) {
                break;
            }
            requestHeaders.append(line).append(NEW_LINE);
            line = bufferedReader.readLine();
        }

        return createHttpRequest(requestHeaders);
    }

    private static HttpRequest createHttpRequest(StringBuilder requestHeaders) {
        String[] splitRequestHeaders = requestHeaders.toString().split(NEW_LINE);
        String[] requestLineArr = splitRequestHeaders[0].split(" ");

        RequestLine requestLine = new RequestLine(requestLineArr[0], Uri.of(requestLineArr[1]), requestLineArr[2]);
        Map<String, String> httpHeaders = createRequestHeaders(splitRequestHeaders);

        return HttpRequest.of(requestLine, httpHeaders);
    }

    private static Map<String, String> createRequestHeaders(String[] splitRequestHeaders) {
        Map<String, String> headers = new LinkedHashMap<>();
        for (int i = 1; i < splitRequestHeaders.length; i++) {
            String[] split = splitRequestHeaders[i].split(" ");
            headers.put(split[0], split[1]);

        }
        return headers;
    }
}