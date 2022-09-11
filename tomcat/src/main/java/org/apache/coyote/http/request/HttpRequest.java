package org.apache.coyote.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpVersion;
import org.apache.coyote.http.session.Session;
import org.apache.coyote.http.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private static final String LINE_SEPARATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpRequestLine httpRequestLine;
    private final HttpHeader httpHeaders;
    private final RequestBody requestBody;

    public HttpRequest(final HttpRequestLine httpRequestLine, final HttpHeader httpHeaders,
                       final RequestBody requestBody) {
        this.httpRequestLine = httpRequestLine;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest parse(final BufferedReader bufferedReader) throws IOException {
        final HttpRequestLine httpRequestLine = createHttpRequestLine(bufferedReader);
        final HttpHeader httpRequestHeader = createHttpRequestHeader(bufferedReader);
        final RequestBody requestBody = parseRequestBody(bufferedReader, httpRequestHeader);
        return new HttpRequest(httpRequestLine, httpRequestHeader, requestBody);
    }

    private static HttpRequestLine createHttpRequestLine(final BufferedReader bufferedReader) throws IOException {
        final String[] line = bufferedReader.readLine().split(LINE_SEPARATOR);
        return HttpRequestLine.from(
                RequestMethod.findMethod(line[METHOD_INDEX]),
                line[URL_INDEX],
                HttpVersion.findVersion(line[VERSION_INDEX])
        );
    }

    private static HttpHeader createHttpRequestHeader(final BufferedReader bufferedReader) throws IOException {
        List<String> headers = new ArrayList<>();
        String line = bufferedReader.readLine();
        if (line == null) {
            return HttpHeader.from(headers);
        }
        while (!"".equals(line)) {
            headers.add(line);
            line = bufferedReader.readLine();
        }
        return HttpHeader.from(headers);
    }

    private static RequestBody parseRequestBody(final BufferedReader bufferedReader, final HttpHeader httpRequestHeader)
            throws IOException {
        if (httpRequestHeader.isContainContentLength()) {
            int contentLength = httpRequestHeader.getContentLength();
            final char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return RequestBody.from(new String(buffer));
        }
        return RequestBody.empty();
    }

    public boolean isGet() {
        return httpRequestLine.isGet();
    }

    public Optional<User> findUserByJSessionId() {
        try {
            final String jSessionId = httpHeaders.getJSessionId();
            log.info("jSessionId={}", jSessionId);
            final Session session = SessionManager.findSession(jSessionId);
            return Optional.ofNullable((User) session.getAttribute("user"));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public String getParameter(String parameter) {
        return requestBody.getValues().get(parameter);
    }

    public String getUrl() {
        return httpRequestLine.getPath();
    }
}
