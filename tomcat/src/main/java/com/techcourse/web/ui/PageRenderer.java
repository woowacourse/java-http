package com.techcourse.web.ui;

import com.techcourse.web.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PageRenderer implements HttpRequestHandler {

    private static final String STATIC_FILE_ROOT = "static";
    private static final Logger log = LoggerFactory.getLogger(PageRenderer.class);

    @Override
    public String handle(final String method, final String path) {
        if(!method.equals("GET")){
            throw new IllegalArgumentException("정적 응답 생성중 에러 발생");
        }
        return createStaticFileResponse(path);
    }


    private String createStaticFileResponse(String path) {
        path = path.trim();
        path = PageEndpoint.findPageByPath(path);

        try {
            String content = readStaticFile(path);
            String contentType = ContentType.findContentType(path);
            return createHttpResponse(200, contentType, content);
        } catch (IOException e) {
            return "ERROR";
        }
    }

    private String readStaticFile(final String path) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(STATIC_FILE_ROOT + path);
        return new String(inputStream.readAllBytes());
    }


    private String createHttpResponse(final int statusCode, final String contentType, final String content) {
        log.info("contentType : {}", contentType);
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " " + getStatusCodeStatus(statusCode) + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);

    }

    private String getStatusCodeStatus(int statusCode) {
        Map<Integer, String> statusTexts = new HashMap<>();
        statusTexts.put(200, "OK");
        return statusTexts.getOrDefault(statusCode,"Unknown");
    }
}
