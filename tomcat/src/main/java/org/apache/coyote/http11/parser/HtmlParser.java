package org.apache.coyote.http11.parser;

import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.Session;

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

    public RequestResult parseContent(
            String contentPath,
            Map<String, String> query,
            String method,
            Map<String, String> requestBody,
            HttpCookies cookies, Session session
    ) throws IOException {
        if (!isParseAble(contentPath)) {
            throw new IllegalArgumentException("처리할 수 없는 요청입니다" + contentPath);
        }

        URL resource = classLoader.getResource("static" + contentPath);
        if (resource == null || resource.getFile() == null) {
            throw new IllegalArgumentException("존재하지않는 파일입니다 404 " + contentPath);
        }

        FileInputStream fileInputStream = new FileInputStream(resource.getFile());
        byte[] result = fileInputStream.readAllBytes();

        return new RequestResult(result, "Content-Type: text/html;charset=utf-8 ");
    }
}
