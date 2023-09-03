package org.apache.coyote.http11.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class DefaultHandler implements Handler {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        String detail = request.getUri().getDetail();
        URL resource = getClass().getClassLoader().getResource("static" + detail);
        Path path = new File(resource.getPath()).toPath();

        String content;
        try {
            content = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String extension = request.getUri().getExtension();

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.CONTENT_TYPE, ContentType.getDetailfromExtension(extension));
        headers.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(content.getBytes().length));

        return HttpResponse.create(StatusCode.OK, headers, content);
    }
}
