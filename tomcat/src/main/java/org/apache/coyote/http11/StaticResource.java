package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class StaticResource {

    private final String content;
    private final String contentType;

    public StaticResource(String path) throws IOException {
        this.content = read(path);
        this.contentType = resolveContentType(path);
    }

    private String read(String path) throws IOException {
        if (path.equals("/")) {
            return "Hello world!";
        }

        //클래스는 클래스로더에 대한 정보를 가짐
        //클래스로더는 파일의 위치에 대한 정보를 가짐
        //getResource는 파일을 찾지 못하면 null을 반환함
        URL resource = getClass().getClassLoader().getResource("static" + path);

        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()), StandardCharsets.UTF_8);
    }

    private String resolveContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }

    public String getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }
}
