package org.apache.coyote.http11.request;

import org.apache.catalina.session.Session;
import org.apache.coyote.http.cookie.HttpCookies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpRequest {

    private static final String HEADER_DELIMITER = ":";
    private static final int OFFSET = 0;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final RequestLine requestLine;
    private final RequestHeader header;
    private final RequestBody body;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String rawRequestLine = bufferedReader.readLine();
        this.requestLine = RequestLine.from(rawRequestLine);

        this.header = parseHeader(bufferedReader);
        this.body = parseBody(bufferedReader);
    }

    private RequestHeader parseHeader(BufferedReader bufferedReader) throws IOException {
        RequestHeader requestHeader = new RequestHeader();
        String line;

        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] headerLine = line.split(HEADER_DELIMITER);

            String key = URLDecoder.decode(headerLine[KEY_INDEX]).trim();
            String value = URLDecoder.decode(headerLine[VALUE_INDEX]).trim();

            requestHeader.addHeader(key, value);
        }
        return requestHeader;
    }

    private RequestBody parseBody(BufferedReader bufferedReader) throws IOException {
        if (requestLine.getHttpMethod().isGet()) {
            return RequestBody.empty();
        }

        StringBuilder stringBuilder = parsePayloads(bufferedReader);

        return new RequestBody(stringBuilder.toString());
    }

    private StringBuilder parsePayloads(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        int contentLength = header.getContentLength();
        char[] buffer = new char[contentLength];

        bufferedReader.read(buffer, OFFSET, contentLength);
        stringBuilder.append(buffer);

        return stringBuilder;
    }

    public boolean existsSession() {
        return header.existsSession();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public RequestHeader getRequestHeader() {
        return header;
    }

    public Session getSession() {
        HttpCookies cookies = HttpCookies.from(header.getCookies());
        return new Session(cookies.getJsessionId());
    }

    public Map<String, String> getRequestBody() {
        return body.getPayloads();
    }
}
