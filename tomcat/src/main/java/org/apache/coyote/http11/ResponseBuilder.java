package org.apache.coyote.http11;

public class ResponseBuilder {
    private final String contentType;
    private final String fileContent;
    private final HttpStatus httpStatus;
    private String location = "";

    public ResponseBuilder(final String acceptType, final String fileContent, final HttpStatus httpStatus) {
        String contentType = "text/html";
        if (acceptType.startsWith("text/css")) {
            contentType = "text/css";
        }
        this.contentType = contentType;
        this.fileContent = fileContent;
        this.httpStatus = httpStatus;
    }

    public ResponseBuilder location(final String location) {
        this.location = location;
        return this;
    }

    public String build() {
        if (!location.isEmpty()) {
            return String.join("\r\n",
                    "HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.name() + " ",
                    "Location: " + location + " ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + fileContent.getBytes().length + " ",
                    "",
                    fileContent);
        }
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.name() + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + fileContent.getBytes().length + " ",
                "",
                fileContent);
    }
}
