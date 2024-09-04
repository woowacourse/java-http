package org.apache.coyote.http11.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.util.FileUtils;

public class StaticResourceHandler implements HttpRequestHandler {

    private static final String STATIC_RESOURCE_PATH = "static";

    @Override
    public boolean supports(HttpRequest request) {
        try {
            final String fileName = request.getUriPath();
            final String filePath = getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + fileName).getFile();
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        final String fileName = request.getUriPath();
        String fileContent = readFile(fileName);
        return HttpResponse.ok(fileContent, FileUtils.getFileExtension(fileName));
    }

    private String readFile(final String fileName) throws IOException {
        URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + fileName);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
