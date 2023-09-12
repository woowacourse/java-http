package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.TargetPath;
import org.apache.coyote.http11.header.ContentType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class FileController implements Controller {

    private static final Map<String, ContentType> CONTENT_TYPES_BY_EXTENSION = Map.of(
            "html", new ContentType("text/html"),
            "css", new ContentType("text/css")
    );

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        TargetPath targetPath = httpRequest.getTarget().getPath().autoComplete();
        return targetPath.asStaticFile().exists();
    }

    @Override
    public void handle(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        try {
            TargetPath targetPath = httpRequest.getTarget().getPath().autoComplete();
            File staticFile = targetPath.asStaticFile();
            validatePresenceOf(staticFile);

            httpResponse.setContentType(getContentTypeBy(targetPath.getExtension()));
            httpResponse.setBody(Files.readString(staticFile.toPath()));
        } catch (IOException exception) {
            throw new IllegalArgumentException("파일 읽기에 실패했습니다");
        }
    }

    private ContentType getContentTypeBy(final String extension) {
        if (CONTENT_TYPES_BY_EXTENSION.containsKey(extension)) {
            return CONTENT_TYPES_BY_EXTENSION.get(extension);
        }
        return new ContentType("text/html");
    }

    private void validatePresenceOf(final File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("리소스를 찾지 못했습니다.");
        }
    }
}
