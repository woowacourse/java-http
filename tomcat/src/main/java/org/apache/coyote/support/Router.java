package org.apache.coyote.support;

import static support.IoUtils.writeAndFlush;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import nextstep.jwp.model.User;
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
            }  // /index
            extension = "html";
            fileName += "." + extension;
        }
    }

    public void route(final HttpRequest httpRequest, final OutputStream outputStream,
                      final BufferedWriter bufferedWriter) throws IOException {
        // 방어 코드: 공백의 문자열이 들어오는 현상
        if (httpRequest.isEmpty()) {
            log.info("isEmpty = {}", httpRequest);
            return;
        }
        HandlerMethod handlerMethod = HandlerMethod.find(httpRequest);
        if (handlerMethod == null) {
            log.info("View Request = {}", httpRequest);
            routeForStaticResource(httpRequest, outputStream, bufferedWriter);
            return;
        }
        log.info("API Request = {}", httpRequest);
        handlerMethod.service(httpRequest, bufferedWriter);
    }

    private void routeForStaticResource(final HttpRequest httpRequest, final OutputStream outputStream,
                                        final BufferedWriter bufferedWriter) {
        final FileDto dto = getFileNameWithExtension(httpRequest.getUri());
        final String responseBody = getResponseBody(dto.fileName);
        final String contentType = getContentType(dto.extension);

        final var response = getResponse(responseBody, contentType);
        writeAndFlush(bufferedWriter, response);
    }

    private FileDto getFileNameWithExtension(final String uri) {
        FileDto dto = new FileDto(uri);
        dto.fileName = uri;
        if (uri.contains(".")) { // index.html
            final String[] fileNameSplit = uri.split("\\.");
            dto.extension = fileNameSplit[fileNameSplit.length - 1];
            dto.fileName = uri;
        } else { // /index
            dto.extension = "html";
            dto.fileName += "." + dto.extension;
        }
        return dto;
    }

    private String getResponse(final String responseBody, final String fileType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + fileType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
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

    private String getResponseBody(final String fileName) {
        if ("/".equals(fileName)) {
            return "Hello world!";
        }
        return IoUtils.readLines(fileName);
    }
}
