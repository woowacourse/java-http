package org.apache.coyote.http11.handler;

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
        String acceptHeader = httpRequest.header()
                .get("Accept")
                .orElse("*/*");

        String result = forward(httpRequest);

        List<String> headerTokens = new ArrayList<>();
        boolean isRedirect = false;
        if (result.startsWith("redirect:")) {
            result = result.split(":")[1];
            headerTokens.add("Location:" + result);
            isRedirect = true;
        }

        String resourcePath = getClass().getClassLoader().getResource("static/" + result).getPath();
        String contentType = determineContentType(resourcePath, acceptHeader);
        headerTokens.add("Content-Type: " + contentType);
        Header header = new Header(headerTokens);

        if (isRedirect) {
            return new HttpResponse(HttpStatus.FOUND, header, new byte[]{});
        }

        return new HttpResponse(HttpStatus.OK, header, readStaticResource(resourcePath));
    }

    private String determineContentType(String resourcePath, String acceptHeader) {
        String encodedContentType = "%s;charset=utf-8";
        if (acceptHeader.startsWith("*/*")) {
            if (resourcePath.endsWith(".html")) {
                return String.format(encodedContentType, "text/html");
            }

            if (resourcePath.endsWith(".css")) {
                return String.format(encodedContentType, "text/css");
            }

            if (resourcePath.endsWith(".js")) {
                return String.format(encodedContentType, "text/javascript");
            }
        }

        if (acceptHeader.startsWith("text/html") && resourcePath.endsWith(".html")) {
            return String.format(encodedContentType, "text/html");
        }

        if (acceptHeader.startsWith("text/css") && resourcePath.endsWith(".css")) {
            return String.format(encodedContentType, "text/css");
        }

        if (acceptHeader.startsWith("text/javascript") && resourcePath.endsWith(".js")) {
            return String.format(encodedContentType, "text/javascript");
        }

        throw new IllegalStateException();
    }

    private byte[] readStaticResource(String resourcePath) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(resourcePath))) {
            return bufferedInputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
