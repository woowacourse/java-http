package org.apache.coyote.http11;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final FileExtension fileExtension;
    private final String responseBody;

    public HttpResponse(HttpStatus httpStatus, FileExtension fileExtension, String responseBody) {
        this.httpStatus = httpStatus;
        this.fileExtension = fileExtension;
        this.responseBody = responseBody;
    }


    public String extractResponse() {
        return new StringBuilder()
                .append(convertStatusLine()).append("\r\n")
                .append(convertContentType()).append("\r\n")
                .append(convertContentLength()).append("\r\n\r\n")
                .append(responseBody)
                .toString();
    }

    private String convertStatusLine() {
        String statusLineFormat = "HTTP/1.1 %s %s ";

        return String.format(statusLineFormat, httpStatus.getStatusCode(), httpStatus.name());
    }

    private String convertContentType() {
        String contentTypeFormat = "Content-Type: %s;charset=utf-8 ";

        return String.format(contentTypeFormat, fileExtension.getContentType());
    }

    private String convertContentLength() {
        String contentLengthFormat = "Content-Length: %d ";

        return String.format(contentLengthFormat, responseBody.getBytes().length);
    }
}
