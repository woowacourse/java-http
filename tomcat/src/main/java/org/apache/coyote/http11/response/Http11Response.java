package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.HttpHeaderName.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeaderName.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpHeaderName.LOCATION;
import static org.apache.coyote.http11.HttpHeaderName.SET_COOKIE;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.HttpHeaderName;

public class Http11Response implements HttpResponse {

    private Http11ResponseStartLine startLine;
    private final Http11ResponseHeaders headers;
    private String body;

    private Http11Response(Http11ResponseStartLine startLine, Http11ResponseHeaders headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static Http11Response create() {
        Http11ResponseStartLine startLine = Http11ResponseStartLine.defaultLine();
        return new Http11Response(startLine, new Http11ResponseHeaders(), null);
    }

    @Override
    public void sendRedirect(String url) {
        this.startLine = new Http11ResponseStartLine(HttpStatusCode.FOUND);
        addHeader(LOCATION, url);
    }

    @Override
    public void addStaticBody(String name) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + name);
        if (resource == null) {
            throw new IllegalArgumentException("존재하지 않는 자원입니다.");
        }
        Path path = new File(resource.getFile()).toPath();
        String contentType = Files.probeContentType(path);
        addBody(new String(Files.readAllBytes(path)));
        addContentType(contentType);
    }

    @Override
    public void addCookie(String key, String value) {
        addHeader(SET_COOKIE, key + "=" + value);
    }

    @Override
    public void addContentType(String accept) {
        addHeader(CONTENT_TYPE, accept + ";charset=utf-8");
    }

    @Override
    public void addBody(String body) {
        this.body = body;
        addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }

    private void addHeader(HttpHeaderName name, String value) {
        headers.add(name, value);
    }

    @Override
    public String toString() {
        if (body == null) {
            return String.join(
                    "\r\n",
                    startLine.toString(),
                    headers.toString(),
                    ""
            );
        }
        return String.join(
                "\r\n",
                startLine.toString(),
                headers.toString(),
                "",
                body
        );
    }
}
