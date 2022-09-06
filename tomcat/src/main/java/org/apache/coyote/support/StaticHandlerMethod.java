package org.apache.coyote.support;

import static support.IoUtils.writeAndFlush;

import java.io.BufferedWriter;
import org.apache.constant.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.IoUtils;

public enum StaticHandlerMethod {

    INSTANCE;

    private static final Logger log = LoggerFactory.getLogger(StaticHandlerMethod.class);

    private class FileDto {

        private static final String HTML_EXTENSION = "html";
        public String extension;
        public String fileName;

        public FileDto(final String uri) {
            fileName = uri;
            if (uri.contains(".")) { // index.html
                final String[] fileNameSplit = uri.split("\\.");
                extension = fileNameSplit[fileNameSplit.length - 1];
                return;
            }  // /index
            extension = HTML_EXTENSION;
            fileName += "." + extension;
        }
    }

    public void handle(final HttpRequest httpRequest, final BufferedWriter bufferedWriter) {
        final FileDto dto = new FileDto(httpRequest.getUri());
        final String responseBody = IoUtils.readLines(dto.fileName);
        final String contentType = getContentType(dto.extension);

        final String response = HttpResponse.builder()
                .addStatus(HttpStatus.OK)
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
