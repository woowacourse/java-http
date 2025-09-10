package com.techcourse.util;

import com.techcourse.exception.NotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class FileUtil {

    public static String createFileName(String path) {
        return String.format("/static/%s", toNormalizedPath(path));
    }

    private static String toNormalizedPath(String path) {
        if (path.startsWith("/")) {
            return path.substring(1);
        }
        return path;
    }

    public static String readResource(final String fileName) {
        try {
            URL url = FileUtil.class.getResource(fileName);
            Objects.requireNonNull(url, fileName + "에 파일이 없습니다.");

            Path filePath = Paths.get(url.toURI());
            return Files.readString(filePath);
        } catch (URISyntaxException | IOException | NullPointerException e) {
            throw new NotFoundException("존재하지 않는 파일입니다. :" + e.getMessage());
        }
    }
}
