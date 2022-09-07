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

    public void handle(final HttpRequest request, final BufferedWriter bufferedWriter) {
        final FileDto dto = new FileDto(request.getUri());
        final String responseBody = IoUtils.readFile(dto.fileName);
        final String contentType = MediaType.find(dto.extension);

        // TODO login폼에서 로그인후 다시 login폼에 왔을시에 JS=JS={id} 현싱
        if (dto.fileName.contains("login") && isAlreadyLogin(request)) {
            alreadyLoginEvent(bufferedWriter, request);
        }

        final String response = HttpResponseBuilder.builder()
                .addStatus(HttpStatus.OK)
                .add(HttpHeader.CONTENT_TYPE, contentType)
                .body(responseBody)
                .build();

        writeAndFlush(bufferedWriter, response);
    }

    private void alreadyLoginEvent(final BufferedWriter bufferedWriter, final HttpRequest request) {
        final String response = HttpResponseBuilder.builder()
                .addStatus(HttpStatus.FOUND)
                .add(HttpHeader.LOCATION, "/index.html")
                .addCooke(request.getSession(false))
                .build();

        writeAndFlush(bufferedWriter, response);
        log.info("Redirect: /index.html");
    }

    private boolean isAlreadyLogin(final HttpRequest request) {
        final Session foundSession = request.getSession(false);
        if (foundSession == null) {
            return false;
        }
        if (SessionManager.findSession(foundSession.getId()) == null) {
            return false;
        }
        return true;
    }
}
