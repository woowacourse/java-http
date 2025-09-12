package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.parser.HeaderParser;
import org.apache.coyote.parser.QueryParamsParser;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;
    private final Map<String, String> parameters;

    public HttpRequest(RequestLine requestLine, Map<String, String> headers, String body,
                       Map<String, String> parameters) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.parameters = parameters;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        RequestLine requestLine = RequestLine.from(reader.readLine());
        Map<String, String> headers = HeaderParser.parse(reader);
        String body = readBody(reader, headers);
        Map<String, String> parameters = parseParameters(requestLine, body, headers);
        return new HttpRequest(requestLine, headers, body, parameters);
    }

    private static String readBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        String contentLengthValue = headers.get("Content-Length");
        if (contentLengthValue == null) {
            return "";
        }
        int contentLength = Integer.parseInt(contentLengthValue);
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private static Map<String, String> parseParameters(RequestLine requestLine, String body,
                                                       Map<String, String> headers) {
        Map<String, String> parameters = new HashMap<>();
        if (requestLine.hasQuery()) {
            parameters.putAll(QueryParamsParser.parse(requestLine.getQueryString()));
        }
        String contentType = headers.get("Content-Type");
        if (!body.isEmpty() && contentType != null && contentType.startsWith("application/x-www-form-urlencoded")) {
            parameters.putAll(QueryParamsParser.parse(body));
        }
        return parameters;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public MimeType resolveMimeType() {
        String acceptHeaderValue = HeaderParser.extractPrimaryMimeType(headers);
        if (acceptHeaderValue != null && !acceptHeaderValue.isEmpty()) {
            return MimeType.fromMimeTypeString(acceptHeaderValue);
        }
        String extension = requestLine.getExtension();
        if (extension != null && !extension.isEmpty()) {
            return MimeType.fromExtensionString(extension);
        }
        return MimeType.HTML;
    }

    public boolean isSameMethod(String type) {
        return this.requestLine.getMethod().equals(type);
    }

    public boolean isRootPath() {
        return requestLine.isRootPath();
    }

    public boolean hasParameters() {
        return hasQuery() || hasBodyParameters();
    }

    public boolean hasQuery() {
        return requestLine.hasQuery();
    }

    public boolean hasBodyParameters() {
        return body != null && !body.isEmpty();
    }

    public String getExtension() {
        return requestLine.getExtension();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String name) {
        return headers.get(name);
    }
}
