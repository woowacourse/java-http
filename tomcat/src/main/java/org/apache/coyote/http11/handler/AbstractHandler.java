package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHandler {

    public abstract boolean canHandle(HttpRequest httpRequest);

    protected abstract String forward(HttpRequest httpRequest);

    public HttpResponse handle(HttpRequest httpRequest) {
        String result = forward(httpRequest);

        List<String> headerTokens = new ArrayList<>();
        boolean isRedirect = false;
        if (result.startsWith("redirect:")) {
            result = result.split(":")[1];
            headerTokens.add("Location:" + result);
            isRedirect = true;
        }

        String resourcePath = getClass().getClassLoader().getResource("static/" + result).getPath();
        String contentType = determineContentType(resourcePath);
        headerTokens.add("Content-Type: " + contentType);
        Header header = new Header(headerTokens);

        if (isRedirect) {
            return new HttpResponse(HttpStatus.FOUND, header, new byte[]{});
        }

        return new HttpResponse(HttpStatus.OK, header, readStaticResource(resourcePath));
    }

    private String determineContentType(String resourcePath) {
        String encodedContentTypeFormat = "%s;charset=utf-8";
        for (ContentType contentType : ContentType.values()) {
            if (resourcePath.endsWith(contentType.getExtension())) {
                return String.format(encodedContentTypeFormat, contentType.getName());
            }
        }

        return String.format(encodedContentTypeFormat, ContentType.PLAIN.getName());
    }

    private byte[] readStaticResource(String resourcePath) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(resourcePath))) {
            return bufferedInputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
