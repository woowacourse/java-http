package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.Protocol;

public class RequestParser {

    private static final String BODY_KEY_PAIR_SPLIT_DELIMITER = "&";
    private static final String BODY_KEY_VALUE_SPLIT_DELIMITER = "=";
    private static final String LINE_SPLIT_DELIMITER = " ";
    private static final String SPLIT_VALUE_DELIMITER = ",";
    private static final String HEADER_DELIMITER = ": ";

    private final BufferedReader bufferedReader;

    public RequestParser(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        this.bufferedReader = new BufferedReader(inputStreamReader);
    }

    public Request parse() throws IOException {
        RequestLine requestLine = readRequestUrl();
        Map<String, String> headers = getHeaders();

        RequestHeader requestHeader = new RequestHeader(requestLine, headers);
        RequestBody requestBody = getRequestBody(requestHeader);
        return new Request(requestHeader, requestBody);
    }

    private RequestLine readRequestUrl() throws IOException {
        String message = bufferedReader.readLine();

        String httpMethod = message.split(LINE_SPLIT_DELIMITER)[0];
        String url = message.split(LINE_SPLIT_DELIMITER)[1];
        String protocol = message.split(LINE_SPLIT_DELIMITER)[2];
        return new RequestLine(HttpMethod.from(httpMethod), RequestUrl.from(url), Protocol.from(protocol));
    }

    public Map<String, String> getHeaders() throws IOException {
        Map<String, String> headers = new HashMap<>();
        String header;
        while ((header = bufferedReader.readLine()) != null && !header.isBlank()) {
            String entry = header.split(SPLIT_VALUE_DELIMITER)[0];
            String key = entry.split(HEADER_DELIMITER)[0];
            String value = entry.split(HEADER_DELIMITER)[1];
            headers.put(key, value);
        }
        return headers;
    }

    public RequestBody getRequestBody(RequestHeader requestHeader) throws IOException {
        if (!requestHeader.hasRequestBody()) {
            return new RequestBody(new HashMap<>());
        }
        String body = getBody(requestHeader.getContentLength());
        Map<String, String> requestBody = new HashMap<>();
        for (String entry : body.split(BODY_KEY_PAIR_SPLIT_DELIMITER)) {
            String key = entry.split(BODY_KEY_VALUE_SPLIT_DELIMITER)[0];
            String value = entry.split(BODY_KEY_VALUE_SPLIT_DELIMITER)[1];
            requestBody.put(key, value);
        }
        return new RequestBody(requestBody);
    }

    private String getBody(int contentLength) throws IOException {
        char[] requestBodyBuffer = new char[contentLength];
        bufferedReader.read(requestBodyBuffer, 0, contentLength);
        return new String(requestBodyBuffer);
    }
}
