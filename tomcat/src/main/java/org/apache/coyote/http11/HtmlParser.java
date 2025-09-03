package org.apache.coyote.http11;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class HtmlParser implements HttpContentParser{

    private static final String FILE_EXTENSTION = ".html";

    private final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    public boolean isParseAble(String request) {
        return request.contains(FILE_EXTENSTION) && request.length() > FILE_EXTENSTION.length();
    }

    public byte[] parseContent(String contentPath) throws IOException {
        URL resource = classLoader.getResource("static" + contentPath);
        if(resource == null || resource.getFile() == null){
            throw new IllegalArgumentException("존재하지않는 파일입니다 404");
        }

        FileInputStream fileInputStream = new FileInputStream(resource.getFile());
        return fileInputStream.readAllBytes();
    }
}
