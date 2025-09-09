package org.apache.coyote.render;

import org.apache.coyote.util.HttpResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PageRenderer{

    private static final String STATIC_FILE_ROOT = "static";
    private static final Logger log = LoggerFactory.getLogger(PageRenderer.class);

    public String handle(final String method, final String path) {
        if(!method.equals("GET")){
            throw new IllegalArgumentException("정적 응답 생성중 에러 발생");
        }
        return createStaticFileResponse(HttpStatus.OK.getStatusCode(), path);
    }

    public static String createStaticFileResponse(int statusCode,String path) {
        path = path.trim();
        path = PageEndpoint.findPageByPath(path);
        String content;
        String contentType;
        try {
            content = readStaticFile(path);
            contentType = ContentType.findContentType(path);
            return HttpResponseBuilder.getStaticHttpResponse(statusCode, contentType, content);
        } catch (IOException e) {
            return HttpResponseBuilder.getErrorHttpResponse(statusCode);
        }
    }

    private static String readStaticFile(final String path) throws IOException {
        String fullPath = STATIC_FILE_ROOT + path;
        log.info(fullPath);
        try (InputStream inputStream = PageRenderer.class.getClassLoader().getResourceAsStream(fullPath)) {
            if (inputStream == null) {
                throw new IOException("파일을 찾을 수 없습니다: " + fullPath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

}
