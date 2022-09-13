package org.apache.coyote.handler;

import org.apache.constant.MediaType;
import org.apache.coyote.http.HttpMessage;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.status.Session;
import org.apache.coyote.status.SessionManager;
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

        @Override
        public String toString() {
            return "FileDto{" +
                    "extension='" + extension + '\'' +
                    ", fileName='" + fileName + '\'' +
                    '}';
        }
    }

    public void handle(final HttpRequest request, final HttpResponse response) {
        final FileDto dto = new FileDto(request.getUri());
        final String responseBody = IoUtils.readFile(dto.fileName);
        final String contentType = MediaType.find(dto.extension);
        log.info("Static File: {}", dto);

        if (isLoginPage(dto) && isAlreadyLogin(request)) {
            alreadyLoginEvent(response);
            return;
        }

        response.addStatus(HttpStatus.OK)
                .add(HttpMessage.CONTENT_TYPE, contentType)
                .body(responseBody);
    }

    private boolean isLoginPage(final FileDto dto) {
        return dto.fileName.contains("login");
    }

    private void alreadyLoginEvent(final HttpResponse response) {
        response.sendRedirect("/index.html");
        log.info("Already Login, Redirect: /index.html");
    }

    private boolean isAlreadyLogin(final HttpRequest request) {
        final Session foundSession = request.getSession(false);
        if (foundSession == null) {
            return false;
        }
        if (SessionManager.isValidSession(foundSession)) {
            return false;
        }
        return true;
    }
}
