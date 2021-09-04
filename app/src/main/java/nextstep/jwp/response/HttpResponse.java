package nextstep.jwp.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.constants.ContentType;
import nextstep.jwp.constants.HeaderType;
import nextstep.jwp.constants.HttpTerms;
import nextstep.jwp.constants.StatusCode;
import nextstep.jwp.exception.PageNotFoundException;

public class HttpResponse {
    private final StatusCode statusCode;
    private final String responseBody;
    private final ContentType contentType;

    public static class Builder {
        private final Map<String, String> headers;

        private StatusCode statusCode = StatusCode.OK;
        private String responseBody = "";
        private ContentType contentType = ContentType.HTML;

        private Builder() {
            this.headers = new LinkedHashMap<>();
            setDefaultHeaders();
        }

        private void setDefaultHeaders() {
            headers.put(HeaderType.CONTENT_TYPE.getValue(), "text/html;charset=utf-8");
            headers.put(HeaderType.CONTENT_LENGTH.getValue(), "0");
        }

        public Builder statusCode(StatusCode statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder responseBody(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public Builder addHeaders(HeaderType headerType, String value) {
            headers.put(headerType.getValue(), value);
            return this;
        }

        public Builder responseResource(String uri) throws IOException {
            final String uriWithExtension = checkFileExtension(uri);
            this.responseBody = findResource(uriWithExtension);
            this.contentType = extractContentType(uriWithExtension);
            return this;
        }

        public String build() {
            return String.join(HttpTerms.NEW_LINE,
                    "HTTP/1.1 " + this.statusCode.getStatusCode() + " " + this.statusCode.getStatus() + " ",
                    assembleHeaders(),
                    responseBody);
        }

        private String assembleHeaders() {
            updateDefaultHeaders();
            String headers = this.headers.entrySet().stream()
                    .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                    .collect(Collectors.joining(HttpTerms.NEW_LINE));
            headers += HttpTerms.NEW_LINE;
            return headers;
        }

        private void updateDefaultHeaders() {
            headers.put(HeaderType.CONTENT_TYPE.getValue(), contentType.getContentType() + ";charset=utf-8");
            headers.put(HeaderType.CONTENT_LENGTH.getValue(), String.valueOf(responseBody.getBytes().length));
        }

        private ContentType extractContentType(String uri) {
            String[] splitByExtension = uri.split(HttpTerms.FILE_EXTENSION_SEPARATOR);
            String fileType = splitByExtension[splitByExtension.length - 1];
            return ContentType.findContentType(fileType);
        }

        private String checkFileExtension(String uri) {
            if (!uri.contains(HttpTerms.DOT)) {
                uri += HttpTerms.FILE_EXTENSION_HTML;
            }
            return uri;
        }

        private String findResource(String uri) throws IOException {
            try {
                final URL resource = getClass().getClassLoader().getResource(HttpTerms.DIRECTORY_STATIC + uri);
                return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            } catch (NullPointerException e) {
                throw new PageNotFoundException("해당하는 정적 리소스 페이지가 없어요");
            }
        }
    }

    private HttpResponse(Builder builder) {
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

    public static String redirectTo(String uri) throws IOException {
        return HttpResponse
                .statusCode(StatusCode.FOUND)
                .addHeaders(HeaderType.LOCATION, uri)
                .responseResource(uri)
                .build();
    }
}
