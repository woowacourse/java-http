package org.apache.coyote.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.util.stream.Collectors.joining;

public class FileUtil {

    private FileUtil() {
    }

    public static String readStaticFile(String uri) {
        InputStream inputStream = FileUtil.class
                .getClassLoader()
                .getResourceAsStream("static/" + uri);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return bufferedReader.lines()
                .collect(joining(System.lineSeparator()));
    }
}
