package org.apache.coyote.http11.util;

import com.techcourse.exception.UncheckedServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import javax.annotation.Nullable;

public class StaticFileUtils {
    private static final String STATIC_RESOURCE_LOCATION = "static";

    private StaticFileUtils() {
    }

    public static boolean isExistStaticFile(String filePath) {
        return getResource(filePath) != null;
    }

    public static String readStaticFile(String filePath) {
        URL resource = getResource(filePath);
        return readFile(resource);
    }

    @Nullable
    private static URL getResource(String filePath) {
        return StaticFileUtils.class.getClassLoader()
                .getResource(STATIC_RESOURCE_LOCATION + filePath);
    }

    private static String readFile(@Nullable URL resource) {
        try {
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException | NullPointerException e) {
            throw new UncheckedServletException("해당 파일이 존재하지 않습니다.");
        }
    }
}
