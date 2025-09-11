package org.apache.coyote.render;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.dto.HttpRequest;
import org.apache.coyote.dto.HttpResponse;
import org.apache.coyote.util.HttpResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageRenderer{

    private static final String STATIC_FILE_ROOT = "static";
    private static final Logger log = LoggerFactory.getLogger(PageRenderer.class);

    public String handle(final HttpRequest httpRequest) {
        if(!httpRequest.method().equals("GET")){
            throw new IllegalArgumentException("정적 응답 생성중 부적절한 Method가 들어왔습니다.");
        }
        return createStaticFileResponse(
                httpRequest.version(),
                HttpStatus.OK.getStatusCode(),
                httpRequest.path()
        );
    }

    public static String createStaticFileResponse(String version, int statusCode ,String path){
        path = PageEndpoint.findPageByPath(path.trim());
        String content = "";
        String contentType = "";
        try {
            content = readStaticFile(path);
            contentType = ContentType.findContentType(path);

            HttpResponse httpResponse = HttpResponseBuilder.staticResponse(
                    version,
                    statusCode,
                    contentType,
                    content
            );
            return httpResponse.toHttpString();
        } catch (IOException e) {
            return returnErrorPage(version);
        }
    }

    private static String readStaticFile(final String path) throws IOException {
        String fullPath = STATIC_FILE_ROOT + path;
        try (InputStream inputStream = PageRenderer.class.getClassLoader().getResourceAsStream(fullPath)) {
            if (inputStream == null) {
                throw new IOException("파일을 찾을 수 없습니다: " + fullPath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String returnErrorPage(String version) {
        String content = "";
        String contentType = "";
        try {
            content = readStaticFile("/404");
            contentType = ContentType.HTML.getContentType();

            HttpResponse httpResponse = HttpResponseBuilder.staticResponse(
                    version,
                    404,
                    contentType,
                    content
            );
            return httpResponse.toHttpString();
            }
            catch (IOException e) {
            throw new IllegalArgumentException("페이지 처리중 에러가 발생했습니다.");
        }
    }

    public static String sendRedirect(String version, int statusCode, Map<String,String> headers) {
        HttpResponse httpResponse = HttpResponseBuilder.redirectResponse(version, statusCode, headers);
        return httpResponse.toHttpString();
    }
}
