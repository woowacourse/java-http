package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

class StaticResource {

    private static final String ROOT_DIRECTORY = "static";
    private static final String ROOT_PATH = "/";
    private static final String DEFAULT_EXTENSION = ".html";
    private static final String DEFAULT_CONTENT = "Hello world!";

    private final ContentType contentType;
    private final String content;

    private StaticResource(ContentType contentType, String content) {
        this.contentType = contentType;
        this.content = content;
    }

    static StaticResource from(String path) throws IOException {
        if (path.equals(ROOT_PATH)) {
            return new StaticResource(ContentType.HTML, DEFAULT_CONTENT);
        }

        String resourcePath = withDefaultExtension(path);
        URL resource = StaticResource.class.getClassLoader().getResource(ROOT_DIRECTORY + resourcePath);
        if (resource == null) {
            return new StaticResource(ContentType.HTML, DEFAULT_CONTENT);
        }

        String content = Files.readString(new File(resource.getFile()).toPath());
        return new StaticResource(ContentType.from(resourcePath), content);
    }

    private static String withDefaultExtension(String path) {
        if (path.contains(".")) {
            return path;
        }
        return path + DEFAULT_EXTENSION;
    }

    void writeTo(HttpResponse response) {
        response.ok(contentType, content);
    }
}
