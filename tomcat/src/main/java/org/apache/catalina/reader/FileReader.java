package org.apache.catalina.reader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import com.techcourse.exception.UncheckedServletException;

public class FileReader {

    private static final String RESOURCE_PATH_PREFIX = "static";

    private FileReader() {}

    public static String loadFileContent(String fileName) {
        URL resource = FileReader.class.getClassLoader().getResource(RESOURCE_PATH_PREFIX + fileName);
        if (resource == null) {
            throw new IllegalArgumentException("'" + fileName + "'이란 페이지를 찾을 수 없습니다.");
        }
        try {
            return Files.readString(new File(resource.getPath()).toPath());
        } catch (IOException | UncheckedServletException e) {
            throw new RuntimeException("'" + fileName + "파일을 문자열로 변환하는데 오류가 발생했습니다.");
        }
    }
}
