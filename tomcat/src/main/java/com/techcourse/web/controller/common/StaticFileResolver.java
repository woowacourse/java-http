package com.techcourse.web.controller.common;

import com.techcourse.web.request.AppRequest;
import com.techcourse.web.view.AppResponse;
import com.techcourse.web.view.StandardResponse;
import common.ContentType;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StaticFileResolver {

    public static AppResponse resolve(final AppRequest request) {
        final String path = request.getPath();
        final String resourcePath = toResourcePath(path);
        
        return load(resourcePath);
    }

    private static String toResourcePath(final String path) {
        final String resourcePath = "static" + path;
        if (path.contains(".")) {
            return resourcePath;
        }
        return resourcePath + ContentType.HTML_EXTENSION;
    }

    private static StandardResponse load(final String resourcePath) {
        try {
            final ContentType contentType = ContentType.fromFilePath(resourcePath);

            final URL resource = StaticFileResolver.class.getClassLoader().getResource(resourcePath);

            if (resource == null) {
                return StandardResponse.notFound();
            }

            final String fileContent = Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);

            return StandardResponse.ok(contentType, fileContent);
        } catch (final Exception e) {
            log.error("정적 파일 서빙 중 오류 발생: {}", resourcePath, e);
            return StandardResponse.serverError();
        }
    }
}
