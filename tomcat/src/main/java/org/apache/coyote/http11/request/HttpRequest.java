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

    private static final String HEADER_DELIMITER = ": ";

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

            String key = URLDecoder.decode(headerLine[0]);
            String value = URLDecoder.decode(headerLine[1]);

            requestHeader.addHeader(key, value);
        }
        return requestHeader;
    }

    private RequestBody parseBody(BufferedReader bufferedReader) throws IOException {
        if (requestLine.getHttpMethod().equals(HttpMethod.GET)) {
            return RequestBody.empty();
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < header.getContentLength(); i++) {
            stringBuilder.append((char) bufferedReader.read());
        }

        return new RequestBody(stringBuilder.toString());
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
        return body.getValues();
    }
}
