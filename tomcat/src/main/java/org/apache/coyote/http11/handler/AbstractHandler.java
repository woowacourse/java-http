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

    protected abstract ForwardResult forward(HttpRequest httpRequest);

    public HttpResponse handle(HttpRequest httpRequest) {
        ForwardResult forwardResult = forward(httpRequest);
        String resourcePath = getClass().getClassLoader().getResource("static/" + forwardResult.path()).getPath();
        List<String> headerTokens = new ArrayList<>();
        headerTokens.add("Content-Type: " + determineContentType(resourcePath));

        if (forwardResult.isRedirect()) {
            headerTokens.add("Location:" + forwardResult.path());
            return new HttpResponse(HttpStatus.FOUND, new Header(headerTokens), new byte[]{});
        }

        return new HttpResponse(HttpStatus.OK, new Header(headerTokens), readStaticResource(resourcePath));
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
