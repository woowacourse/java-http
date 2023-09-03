package org.apache.coyote.http11.handler;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class StaticResourceHandler implements Handler {

    private static final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();

    @Override
    public boolean supports(HttpRequest request) {
        return doesStaticFileExists(request.getUrl());
    }

    private boolean doesStaticFileExists(String url) {
        try (final FileInputStream fileStream = new FileInputStream(
                findStaticResourceURL(url).getFile())) {
            return true;
        } catch (IOException | NullPointerException e) {
            return false;
        }
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String url = request.getUrl();
        try (
                FileInputStream fileStream = new FileInputStream(
                        findStaticResourceURL(url).getFile())
        ) {
            String path = findStaticResourceURL(url).getFile();
            String extension = getResourceExtension(path);
            return new HttpResponse.Builder().setHttpStatusCode(HttpStatusCode.OK)
                    .setContentType(toTextContentType(extension))
                    .setBody(fileStream.readAllBytes())
                    .build();
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException();
        }
    }

    private URL findStaticResourceURL(String url) {
        return SYSTEM_CLASS_LOADER.getResource("static" + url);
    }

    private String getResourceExtension(String path) {
        return path.split("\\.")[1];
    }

    private String toTextContentType(String extension) {
        return "text/" + extension;
    }
}
