package org.apache.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.exception.UncheckedServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceUtil {

    private static final Logger log = LoggerFactory.getLogger(ResourceUtil.class);
    private static final String PREFIX = "static";

    public static String getResource(final String fileName) {
        String responseBody = null;
        try {
            responseBody = new String(Files.readAllBytes(getPath(fileName)));
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
        return responseBody;
    }

    public static Path getPath(final String fileName) {
        String filePath = ClassLoader.getSystemResource(PREFIX + fileName)
                .getPath();
        log.info("path : {}", filePath);
        return new File(filePath).toPath();
    }
}
