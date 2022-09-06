package org.apache.coyote.support;

import static support.IoUtils.writeAndFlush;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import org.apache.constant.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.IoUtils;

public class Router {

    private static final Logger log = LoggerFactory.getLogger(Router.class);

    private class FileDto {

        public String extension;
        public String fileName;

        public FileDto(final String uri) {
            fileName = uri;
            if (uri.contains(".")) { // index.html
                final String[] fileNameSplit = uri.split("\\.");
                extension = fileNameSplit[fileNameSplit.length - 1];
                return;
            }  // /index
            extension = "html";
            fileName += "." + extension;
        }
    }

    public void route(final HttpRequest httpRequest,
                      final BufferedReader bufferedReader,
                      final BufferedWriter bufferedWriter) throws IOException {
        // 방어 코드: 공백의 문자열이 들어오는 현상
        if (httpRequest.isEmpty()) {
            log.info("isEmpty = {}", httpRequest);
            return;
        }
        HandlerMethod handlerMethod = HandlerMethod.find(httpRequest);
        if (handlerMethod == null) {
            log.info("View Request = {}", httpRequest);
            routeForStaticResource(httpRequest, bufferedWriter);
            return;
        }
        log.info("API Request = {}", httpRequest);
        handlerMethod.service(httpRequest, bufferedWriter);
    }

    private void routeForStaticResource(final HttpRequest httpRequest, final BufferedWriter bufferedWriter) {
        final FileDto dto = new FileDto(httpRequest.getUri());
        final String responseBody = IoUtils.readLines(dto.fileName);
        final String contentType = getContentType(dto.extension);

        final String response = HttpResponse.builder()
                .add(HttpHeader.HTTP_1_1_STATUS_CODE, "200 OK")
                .add(HttpHeader.CONTENT_TYPE, contentType)
                .body(responseBody)
                .build();

        writeAndFlush(bufferedWriter, response);
    }

    private String getContentType(final String fileExtension) {
        switch (fileExtension) {
            case "html":
                return MediaType.HTML;
            case "css":
                return MediaType.CSS;
            case "js":
                return MediaType.JAVASCRIPT;
            default:
                return MediaType.PLAIN;
        }
    }
}
