package org.apache.coyote.http11.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class HtmlParser implements HttpParser {

    private static final String FILE_EXTENSION = ".html";

    private final ClassLoader classLoader = ClassLoader.getSystemClassLoader();


    public boolean isParseAble(String request) {
        return request.contains(FILE_EXTENSION) && request.length() > FILE_EXTENSION.length();
    }

    public ContentParseResult parseContent(String contentPath, Map<String, String> query) throws IOException {
        if (!isParseAble(contentPath)) {
            throw new IllegalArgumentException("처리할 수 없는 요청입니다" + contentPath);
        }

        URL resource = classLoader.getResource("static" + contentPath);
        if (resource == null || resource.getFile() == null) {
            throw new IllegalArgumentException("존재하지않는 파일입니다 404 " + contentPath);
        }

        FileInputStream fileInputStream = new FileInputStream(resource.getFile());
        byte[] result = fileInputStream.readAllBytes();

        return new ContentParseResult(result, "Content-Type: text/html;charset=utf-8 ");
    }
}
