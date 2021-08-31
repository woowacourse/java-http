package nextstep.jwp.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.constants.ContentType;
import nextstep.jwp.constants.Http;
import nextstep.jwp.constants.StatusCode;
import nextstep.jwp.exception.PageNotFoundException;

public class ResponseEntity {

    private final StatusCode statusCode;
    private final String responseBody;
    private final ContentType contentType;

    public static class Builder {
        private StatusCode statusCode = StatusCode.OK;
        private String responseBody = "";
        private ContentType contentType = ContentType.HTML;

        private Builder() {
        }

        public Builder statusCode(StatusCode statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder responseBody(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public Builder responseResource(String uri) throws IOException {
            final String uriWithExtension = checkFileExtension(uri);
            this.responseBody = findResource(uriWithExtension);
            this.contentType = extractContentType(uriWithExtension);
            return this;
        }

        public String build() {
            return String.join(Http.NEW_LINE,
                    "HTTP/1.1 " + this.statusCode.getStatusCode() + " " + this.statusCode.getStatus() + " ",
                    "Content-Type: " + contentType.getContentType() + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        private ContentType extractContentType(String uri) {
            String[] splitByExtension = uri.split(Http.FILE_EXTENSION_SEPARATOR);
            String fileType = splitByExtension[splitByExtension.length - 1];
            return ContentType.findContentType(fileType);
        }

        private String checkFileExtension(String uri) {
            if (!uri.contains(Http.DOT)) {
                uri += Http.FILE_EXTENSION_HTML;
            }
            return uri;
        }

        private String findResource(String uri) throws IOException {
            try {
                final URL resource = getClass().getClassLoader().getResource(Http.DIRECTORY_STATIC + uri);
                return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            } catch (NullPointerException e) {
                throw new PageNotFoundException("해당하는 정적 리소스 페이지가 없어요");
            }
        }

    }

    private ResponseEntity(Builder builder) {
        this.statusCode = builder.statusCode;
        this.responseBody = builder.responseBody;
        this.contentType = builder.contentType;
    }

    public static Builder statusCode(StatusCode statusCode) {
        return new Builder().statusCode(statusCode);
    }

    public static Builder responseBody(String responseBody) {
        return new Builder().responseBody(responseBody);
    }

    public static Builder responseResource(String uri) throws IOException {
        return new Builder().responseResource(uri);
    }

}
