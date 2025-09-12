package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class RequestBody {

    private final String rawBody;
    private final Map<String, String> bodyParams;

    public RequestBody(RequestLine requestLine, HttpHeaders headers, BufferedReader reader) throws IOException {
        this.rawBody = parseRawBody(requestLine, headers, reader);
        this.bodyParams = parseBodyParams(headers, this.rawBody);
    }

    private String parseRawBody(RequestLine requestLine, HttpHeaders headers, BufferedReader reader)
            throws IOException {
        if (!requestLine.isPost()) {
            return null;
        }
        String contentLengthValue = headers.get("Content-Length");
        if (contentLengthValue == null) {
            return null;
        }
        int contentLength = Integer.parseInt(contentLengthValue);
        char[] buffer = new char[contentLength];
        reader.read(buffer);
        return new String(buffer);
    }

    private Map<String, String> parseBodyParams(HttpHeaders headers, String rawBody) {
        if (rawBody == null || !"application/x-www-form-urlencoded".equalsIgnoreCase(headers.get("Content-Type"))) {
            return Collections.emptyMap();
        }
        return HttpParamParser.parseKeyValuePairs(rawBody, "&");
    }

    public String getRawBody() {
        return rawBody;
    }

    public String getParam(String key) {
        return bodyParams.get(key);
    }

    public Map<String, String> getParams() {
        return Collections.unmodifiableMap(bodyParams);
    }
}
