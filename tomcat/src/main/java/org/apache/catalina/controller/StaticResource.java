package org.apache.catalina.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class StaticResource {

    private StaticResource() {
    }

    public static String read(final String path) throws URISyntaxException, IOException {
        final String fileName = "static" + resolveFileName(path);
        final URL url = ClassLoader.getSystemResource(fileName); // 클래스패스에서 static/ 아래 파일을 찾아 실제 위치를 URL로 돌려 줌
        final File file = new File(url.toURI());
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    // 요청 path를 받아서, 서버에서 찾을 파일 이름을 돌려 줌
    private static String resolveFileName(final String path) {
        if (!path.contains(".")) {
            return path + ".html";
        }
        return path;
    }
}
