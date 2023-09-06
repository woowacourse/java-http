package org.apache.coyote.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

public class FileUtil {

    private FileUtil() {
    }

    public static String readStaticFile(String uri) {
        InputStream inputStream = FileUtil.class
                .getClassLoader()
                .getResourceAsStream("static/" + uri);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
            Objects.requireNonNull(inputStream, "파일을 찾을 수 없습니다. 파일: " + uri), StandardCharsets.UTF_8)
        );

        return bufferedReader.lines()
                .collect(joining("\r\n")) + "\r\n";
    }
}
