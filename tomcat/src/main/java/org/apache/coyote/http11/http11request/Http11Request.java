package org.apache.coyote.http11.http11request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HeaderElement;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.cookie.HttpCookie;

public class Http11Request {

    private static final String FIRST_LINE_DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final HttpMethod httpMethod;
    private final String uri;
    private final Map<String, String> header;
    private final String body;

    public Http11Request(String httpMethod, String uri, Map<String, String> header, String body) {
        this.httpMethod = HttpMethod.valueOf(httpMethod.toUpperCase());
        this.uri = uri;
        this.header = header;
        this.body = body;
    }

    public static Http11Request of(BufferedReader bufferedReader) throws IOException {
        String[] firstLineDatas = bufferedReader.readLine().split(FIRST_LINE_DELIMITER);
        String httpMethod = firstLineDatas[HTTP_METHOD_INDEX];
        String uri = firstLineDatas[URI_INDEX];
        Map<String, String> headers = parseHeaders(bufferedReader);

        String body = parseBody(headers.get("Content-Length"), bufferedReader);

        return new Http11Request(httpMethod, uri, headers, body);
    }

    private static Map<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        while (true) {
            String data = bufferedReader.readLine();
            if (data == null || data.equals("")) {
                break;
            }
            String[] header = data.split(":");
            headers.put(header[HEADER_NAME_INDEX].strip(), header[HEADER_VALUE_INDEX].strip());
        }
        return headers;
    }

    private static String parseBody(String contentLength, BufferedReader bufferedReader) throws IOException {
        if (contentLength == null) {
            return "";
        }

        StringBuilder bodyBuilder = new StringBuilder();
        char[] buffer = new char[Integer.parseInt(contentLength)];
        bufferedReader.read(buffer);
        bodyBuilder.append(new String(buffer));

        return bodyBuilder.toString();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public String getBody() {
        return body;
    }

    public String getSessionId() {
        if (!header.containsKey(HeaderElement.COOKIE.getValue())) {
            return null;
        }
        HttpCookie httpCookie = HttpCookie.of(header.get(HeaderElement.COOKIE.getValue()));
        if (httpCookie.hasJessionId()) {
            return httpCookie.getJsessionId();
        }
        return null;
    }
}
