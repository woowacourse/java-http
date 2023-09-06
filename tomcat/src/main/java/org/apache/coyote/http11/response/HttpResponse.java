package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.MimeType;

public class HttpResponse {

    private static final String SEPARATOR = "\r\n";

    private final HttpStatus httpStatus;
    private final File file;

    public HttpResponse(HttpStatus httpStatus, File file) {
        this.httpStatus = httpStatus;
        this.file = file;
    }

    public String extractResponse() {
        return new StringBuilder()
                .append(convertStatusLine()).append(SEPARATOR)
                .append(convertHeaders())
                .toString();
    }

    private String convertStatusLine() {
        String statusLineFormat = "HTTP/1.1 %s %s ";

        return String.format(statusLineFormat, httpStatus.getStatusCode(), httpStatus.name());
    }

    private String convertHeaders() {
        String fileName = file.getName();

        if (httpStatus.equals(HttpStatus.FOUND)) {
            return redirectHeaders(fileName);
        }
        return headers(fileName);
    }

    private String redirectHeaders(String fileName) {
        return new StringBuilder()
                .append(convertContentType(MimeType.HTML)).append(SEPARATOR)
                .append(convertLocation(fileName)).append(SEPARATOR)
                .append(SEPARATOR)
                .toString();
    }

    private String convertContentType(MimeType mimeType) {
        String contentTypeFormat = "Content-Type: %s;charset=utf-8 ";

        return String.format(contentTypeFormat, mimeType.getContentType());
    }

    private String convertLocation(String fileName) {
        String locationFormat = "Location: %s.html ";
        String path = fileName.substring(1, fileName.lastIndexOf("."));

        return String.format(locationFormat, path);
    }

    private String headers(String fileName) {
        return new StringBuilder()
                .append(convertContentType(MimeType.from(fileName))).append(SEPARATOR)
                .append(convertContentLength()).append(SEPARATOR)
                .append(SEPARATOR)
                .append(responseBody())
                .toString();
    }

    private String convertContentLength() {
        String contentLengthFormat = "Content-Length: %d ";

        return String.format(contentLengthFormat, responseBody().getBytes().length);
    }

    private String responseBody() {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            return "";
        }
    }

}
