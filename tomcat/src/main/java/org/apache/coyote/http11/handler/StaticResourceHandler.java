package org.apache.coyote.http11.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import org.apache.coyote.http11.handler.component.HttpResponse;
import org.apache.coyote.http11.handler.component.StaticResourceContentType;

public class StaticResourceHandler {

    public HttpResponse getResponse(final String path) throws IOException {
        final String contentType = getContentType(path);
        final String responseBody = new String(getResourceByteArray(path));
        return new HttpResponse(
            "HTTP/1.1 200 OK ",
            responseBody,
            List.of(
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.getBytes().length + " "
            )
        );
    }

    public HttpResponse getResponse(final String path, final Charset charset) throws IOException {
        final String contentType = getContentType(path, charset);
        final String responseBody = new String(getResourceByteArray(path));
        return new HttpResponse(
            "HTTP/1.1 200 OK ",
            responseBody,
            List.of(
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.getBytes().length + " "
            )
        );
    }

    public String getContentType(final String targetUrl) {
        return StaticResourceContentType.find(targetUrl);
    }

    public String getContentType(final String targetUrl, final Charset charset) {
        return StaticResourceContentType.findWithAdditionalCharset(targetUrl, charset);
    }

    protected byte[] getResourceByteArray(final String targetUrl) throws IOException {
        final URL resource = getResource(targetUrl);
        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException("해당 리소스가 존재하지 않습니다. 경로 : " + targetUrl);
        }

        return Files.readAllBytes(new File(resource.getFile()).toPath());
    }

    private URL getResource(final String targetUrl) {
        return getClass().getClassLoader().getResource("static" + targetUrl);
    }
}
