package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpResponseHandler {

    public HttpResponse handleResponse(HttpRequest request, HttpStatusCode statusCode) throws IOException {
        HttpProtocol httpProtocol = request.getHttpProtocol();

        String resourcePath = request.getResourcePath();

        return new HttpResponse(httpProtocol, statusCode, findResource(resourcePath));
    }

    private HttpResponseBody findResource(String resourcePath) throws IOException {
        if (resourcePath.equals("/")) {
            return new HttpResponseBody("Hello world!".getBytes(), MimeType.TEXT_HTML);
        }

        int index = resourcePath.lastIndexOf("/");
        String resource =  resourcePath.substring(index + 1);
        if (resource.contains(".")) {
            resourcePath = "./static" + resourcePath;
        } else {
            resourcePath = "./static" + resourcePath + ".html";
        }

        URL systemResource = ClassLoader.getSystemResource(resourcePath);

        if (systemResource == null) {
            throw new IllegalArgumentException();
        }

        Path path = Path.of(systemResource.getPath());

        final var responseBody = Files.readAllBytes(path);
        return new HttpResponseBody(responseBody, getContentType(resourcePath));
    }

    private MimeType getContentType(String resourcePath) {
        String extension = ".html";
        if (resourcePath.equals("/")) {
            return MimeType.getMimeType(extension);
        }

        int index = resourcePath.lastIndexOf("/");
        String resource =  resourcePath.substring(index + 1);

        if (resource.contains(".")) {
            int dotIndex = resource.lastIndexOf(".");
            extension = resource.substring(dotIndex);
            return MimeType.getMimeType(extension);
        }
        return MimeType.getMimeType(".html");
    }
}
