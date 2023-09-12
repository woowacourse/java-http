package org.apache.coyote.http11.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.header.ContentType;

public class FileController implements Controller {

    private static final ClassLoader CLASS_LOADER = FileController.class.getClassLoader();

    private static final String STATIC_RESOURCES_PATH = "static";
    private static final String DEFAULT_INDEX = "index";
    private static final String DEFAULT_EXTENSION = ".html";

    private static final Map<String, ContentType> CONTENT_TYPES_BY_EXTENSION = Map.of(
            "html", new ContentType("text/html"),
            "css", new ContentType("text/css")
    );

    @Override
    public void handle(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        try {
            String targetPath = fillExtensionIfDoesNotExist(httpRequest.getTarget().getPath());

            File resource = getResourceFileFrom(targetPath);
            String body = Files.readString(resource.toPath());
            httpResponse.setContentType(getContentTypeFrom(targetPath));
            httpResponse.setBody(body);
        } catch (IOException exception) {
            throw new IllegalArgumentException("파일 읽기에 실패했습니다");
        }
    }

    private String fillExtensionIfDoesNotExist(String target) {
        if (Objects.isNull(getExtensionOf(target))) {
            return target + DEFAULT_EXTENSION;
        }
        return target;
    }

    private ContentType getContentTypeFrom(final String target) {
        String extension = getExtensionOf(target);
        if (CONTENT_TYPES_BY_EXTENSION.containsKey(extension)) {
            return CONTENT_TYPES_BY_EXTENSION.get(extension);
        }
        return new ContentType("text/html");
    }

    private File getResourceFileFrom(final String target) {
        String relativePath = getRelativePathFrom(target);
        URL resource = CLASS_LOADER.getResource(relativePath);
        validatePresenceOf(resource);

        String absolutePath = resource.getPath();
        return new File(absolutePath);
    }

    private String getRelativePathFrom(final String target) {
        if (target.endsWith("/")) {
            return STATIC_RESOURCES_PATH + target + DEFAULT_INDEX;
        }
        return STATIC_RESOURCES_PATH + target;
    }

    private void validatePresenceOf(final URL resource) {
        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException("리소스를 찾지 못했습니다.");
        }
    }

    private String getExtensionOf(String path) {
        int lastSlash = path.lastIndexOf('/');
        String lastSubPath = path.substring(lastSlash + 1);
        if (lastSubPath.contains(".")) {
            int extensionStart = lastSubPath.lastIndexOf('.');
            return lastSubPath.substring(extensionStart + 1);
        }
        return null;
    }
}
