package org.apache.coyote.http11.message.response;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.common.HttpBody;
import org.apache.coyote.http11.message.common.HttpHeaderField;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.util.ResourceReader;

public class HttpResponse {

    private static final String CHARSET = ";charset=utf-8";
    private final HttpStatusLine statusLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpResponse() {
        this(new HttpStatusLine(HttpStatus.OK), new HttpHeaders(), new HttpBody(""));
    }

    public HttpResponse(HttpStatusLine statusLine, HttpHeaders headers, HttpBody body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public void setStatusLine(HttpStatus httpStatus) {
        this.statusLine.setHttpStatus(httpStatus);
    }

    public void setHeader(String key, String value) {
        this.headers.setHeaders(key, value);
    }

    public void setContentType(ContentType contentType) {
        this.headers.setHeaders(HttpHeaderField.CONTENT_TYPE.getName(), contentType.getType());
    }

    public void setStaticBody(String source) throws URISyntaxException, IOException {
        URL url = ResourceReader.readResource(source);
        Path path = new File(url.getPath()).toPath();
        String contentType = Files.probeContentType(path);

        setContentType(contentType);
        setBody(new String(Files.readAllBytes(path)));
    }

    public void setContentType(String contentType) {
        this.headers.setHeaders(HttpHeaderField.CONTENT_TYPE.getName(), contentType + CHARSET);
    }

    public void setBody(String body) {
        this.body.setBody(body);
        this.setHeader(HttpHeaderField.CONTENT_LENGTH.getName(), String.valueOf(body.getBytes().length));
    }


    @Override
    public String toString() {
        return String.join("\r\n", statusLine.toString(), headers.toString(), body.toString());
    }
}
