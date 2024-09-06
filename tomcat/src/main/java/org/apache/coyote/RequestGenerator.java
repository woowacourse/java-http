package org.apache.coyote;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.common.Request;
import org.apache.coyote.common.RequestBody;
import org.apache.coyote.common.RequestHeaders;
import org.apache.coyote.common.RequestLine;
import org.apache.coyote.common.RequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestGenerator {

    private static final Logger log = LoggerFactory.getLogger(RequestGenerator.class);

    public static Request accept(BufferedReader reader) throws IOException {
        String firstLine = reader.readLine();
        RequestLine requestLine = parseRequestLine(firstLine);
        RequestHeaders requestHeaders = readHeaders(reader);
        RequestParameters requestParameters = parseParameters(firstLine);
        RequestBody requestBody = readRequestBody(reader, requestHeaders.getContentLength());
        Request request = new Request(requestLine, requestHeaders, requestParameters, requestBody);
        log.info("request: {}", request);
        return request;
    }

    private static RequestLine parseRequestLine(String line) {
        String[] token = line.split(" ");
        return new RequestLine(token[0], parseUri(token[1]), token[2]);
    }

    private static String parseUri(String uri) {
        if (uri.contains("?")) {
            return uri.substring(0, uri.indexOf("?"));
        }
        return uri;
    }

    private static RequestHeaders readHeaders(BufferedReader reader) {
        String[] headers = reader.lines()
                .takeWhile(line -> !line.isEmpty())
                .toArray(String[]::new);
        return parseHeaders(headers);
    }

    private static RequestHeaders parseHeaders(String[] headers) {
        Map<String, String> result = new HashMap<>();
        for (String header : headers) {
            String[] token = header.split(": ");
            result.put(token[0], token[1]);
        }
        return new RequestHeaders(result);
    }

    private static RequestParameters parseParameters(String requestLine) {
        String line = requestLine.split(" ")[1];
        if (!line.contains("?")) {
            return new RequestParameters(new HashMap<>());
        }
        String[] parameters = line.substring(line.indexOf("?") + 1).split("&");
        return new RequestParameters(Arrays.stream(parameters)
                                             .map(param -> param.split("=", 2))
                                             .filter(param -> param.length == 2)
                                             .collect(Collectors.toMap(
                                                     entry -> entry[0],
                                                     entry -> entry[1],
                                                     (existing, replacement) -> existing,
                                                     HashMap::new)));
    }

    private static RequestBody readRequestBody(BufferedReader reader, int contentLength) throws IOException {
        if (contentLength == 0) {
            return RequestBody.empty();
        }
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return RequestBody.fromFormData(requestBody);
    }
}
