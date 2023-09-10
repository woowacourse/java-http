package org.apache.coyote.http11.response;

import org.apache.coyote.http11.header.Header;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.RequestHeader;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.Session;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;

import static org.apache.coyote.http11.header.EntityHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.header.EntityHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.header.ResponseHeader.LOCATION;
import static org.apache.coyote.http11.header.ResponseHeader.SET_COOKIE;
import static org.apache.coyote.http11.response.StatusCode.*;

public class Response {

    private final StatusLine statusLine;
    private final Headers headers;
    private String body;

    public Response() {
        this(StatusLine.DEFAULT_STATUS_LINE, new Headers(), "");
    }

    public Response(final String body) {
        this(StatusLine.DEFAULT_STATUS_LINE, new Headers(), body);
    }

    public Response(final StatusLine statusLine,
                    final Headers headers,
                    final String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static Response getRedirectResponse(final String path) {
        final Headers redirectHeaders = new Headers();
        redirectHeaders.addHeader(LOCATION, path);
        return new Response(new StatusLine(FOUND), redirectHeaders, "");
    }

    public static Response getNotFoundResponse() {
        return new Response(new StatusLine(NOT_FOUND), new Headers(), "");
    }

    public static Response getUnauthorizedResponse() {
        return new Response(new StatusLine(UNAUTHORIZED), new Headers(), "");
    }

    public String parseString() {
        return String.join("\r\n",
                statusLine.parseResponse(),
                headers.parseResponse(),
                "",
                body);
    }

    public void writeBody(final String content) {
        final String contentLength = String.valueOf(content.length());
        headers.addHeader(CONTENT_LENGTH, contentLength);

        this.body = content;
    }

    public void writeStaticResource(final String path) {
        final ClassLoader classLoader = getClass().getClassLoader();
        final String name = "static" + path;
        final URL fileURL = classLoader.getResource(name);

        if (fileURL == null) {
            throw new IllegalStateException("정적 파일이 존재하지 않습니다");
        }

        final URI fileURI;
        try {
            fileURI = fileURL.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("경로 문제가 발생했습니다");
        }

        final StringBuilder stringBuilder = new StringBuilder();
        try (final InputStream inputStream = new FileInputStream(Paths.get(fileURI).toFile());
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(nextLine)
                        .append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new IllegalStateException("파일을 읽는 중 문제가 발생했습니다.");
        }

        this.body = stringBuilder.toString();
        decideContentLength();
    }

    public void addHeader(final Header header, final String value) {
        headers.addHeader(header, value);
    }

    public void decideHeaders(final Request request) {
        addJSessionId(request);
        decideContentLength();
    }

    public void addJSessionId(final Request request) {
        final Session session = request.getSession();
        final String sessionId = session.getId();
        final String sessionIdCookie = request.getHttpCookie().findCookie("JSESSIONID");
        if (!Objects.equals(sessionIdCookie, sessionId)) {
            headers.addHeader(SET_COOKIE, "JSESSIONID=" + sessionId);
        }
    }

    public void init(final Request request) {
        final String acceptHeaderValue = request.getHeaders().getValue(RequestHeader.ACCEPT);
        final String requestPath = request.getRequestLine().getRequestPath();
        final String contentTypeValue = decideResponseContentType(acceptHeaderValue, requestPath);
        headers.addHeader(CONTENT_TYPE, contentTypeValue);
    }

    private String decideResponseContentType(final String requestAcceptHeader,
                                             final String requestUri) {
        String responseFileExtension = requestUri.substring(requestUri.indexOf(".") + 1);
        if ("text/css".equals(requestAcceptHeader) || "css".equals(responseFileExtension)) {
            return  "text/css,*/*;q=0.1";
        }
        if ("application/javascript".equals(requestAcceptHeader) || "js".equals(responseFileExtension)) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    private void decideContentLength() {
        final byte[] bytes = body.getBytes();
        headers.addHeader(CONTENT_LENGTH, String.valueOf(bytes.length));
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setStatusCode(final StatusCode statusCode) {
        statusLine.setStatusCode(statusCode);
    }

    @Override
    public String toString() {
        return "Response{" +
                "statusLine=" + statusLine +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
