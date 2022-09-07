package org.apache.coyote.support;

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

    public void handle(final HttpRequest request, final HttpResponse response) {
        final FileDto dto = new FileDto(request.getUri());
        final String responseBody = IoUtils.readFile(dto.fileName);
        final String contentType = MediaType.find(dto.extension);

        // TODO login 폼에서 로그인후 다시 login폼에 왔을시에 JS=JS={id} 현싱
        if (dto.fileName.contains("login") && isAlreadyLogin(request)) {
            alreadyLoginEvent(request, response);
        }

//        final String response = HttpResponseBuilderOld.builder()
//                .addStatus(HttpStatus.OK)
//                .add(HttpHeader.CONTENT_TYPE, contentType)
//                .body(responseBody)
//                .build();

        response.addStatus(HttpStatus.OK)
                .add(HttpHeader.CONTENT_TYPE, contentType)
                .body(responseBody);
    }

    private void alreadyLoginEvent(final HttpRequest request, final HttpResponse response) {
//        final String response = HttpResponseBuilderOld.builder()
//                .addStatus(HttpStatus.FOUND)
//                .add(HttpHeader.LOCATION, "/index.html")
//                .addCooke(request.getSession(false))
//                .build();
        response.sendRedirect("/index.html")
                .addCooke(request.getSession(false));

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
