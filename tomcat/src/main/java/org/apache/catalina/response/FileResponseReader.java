package org.apache.catalina.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import com.techcourse.exception.UncheckedServletException;

public class FileResponseReader {
    private static final String RESOURCE_PATH_PREFIX = "static";

    public static String loadFileContent(String fileName) {
        URL resource = FileResponseReader.class.getClassLoader().getResource(RESOURCE_PATH_PREFIX + fileName);
        if (resource == null) {
            throw new RuntimeException("'" + fileName + "'이란 페이지를 찾을 수 없습니다.");
        }
        Path path = new File(resource.getPath()).toPath();

        try {
            return Files.readString(path);
        } catch (IOException | UncheckedServletException e) {
            throw new RuntimeException("'" + fileName + "파일을 문자열로 변환하는데 오류가 발생했습니다.");
        }
    }
}
