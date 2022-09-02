package org.apache.support;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.exception.UncheckedServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceFindUtils {
    private static final Logger log = LoggerFactory.getLogger(ResourceFindUtils.class);

    public static String getResourceFile(String resource) {
        URL url = ResourceFindUtils.class.getClassLoader().getResource("static" + resource);
        final File file = new File(url.getPath());
        final Path path = file.toPath();

        try {
            final String responseBody = Files.readString(path);
            return responseBody;
        } catch (IOException exception) {
            log.error(exception.getMessage());
            throw new UncheckedServletException("유효하지 않은 리소스에 대한 접근입니다.");
        }
    }
}
