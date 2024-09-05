package org.apache.coyote.http11;

public class Response {

    private int statusCode;
    private String sc;
    private String contentType;
    private int contentLength;
    private String location;
    private String sourceCode;
    private String cookie;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(final int statusCode) {
        this.statusCode = statusCode;
    }

    public String getSc() {
        return sc;
    }

    public void setSc(final String sc) {
        this.sc = sc;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(final int contentLength) {
        this.contentLength = contentLength;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(final String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(final String cookie) {
        this.cookie = cookie;
    }

    public String toHttpResponse() {
        if (location == null && cookie == null) {
            return String.join("\r\n", "HTTP/1.1 " + statusCode + " " + sc + " ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + contentLength + " ",
                    "",
                    sourceCode);
        }
        if (location != null && cookie == null) {
            return String.join("\r\n", "HTTP/1.1 " + statusCode + " " + sc + " ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + contentLength + " ",
                    "Location: " + location,
                    "",
                    sourceCode);
        }
        if (location == null) {
            return String.join("\r\n", "HTTP/1.1 " + statusCode + " " + sc + " ",
                    "Set-Cookie: " + cookie + " ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + contentLength + " ",
                    "",
                    sourceCode);
        }
        return String.join("\r\n", "HTTP/1.1 " + statusCode + " " + sc + " ",
                "Set-Cookie: " + cookie + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + contentLength + " ",
                "Location: " + location,
                "",
                sourceCode);
    }

    @Override
    public String toString() {
        return "Response{" +
               "statusCode=" + statusCode +
               ", sc='" + sc + '\'' +
               ", contentType='" + contentType + '\'' +
               ", contentLength=" + contentLength +
               ", location='" + location + '\'' +
               '}';
    }
}
