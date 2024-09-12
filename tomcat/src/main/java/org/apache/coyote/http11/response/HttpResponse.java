package org.apache.coyote.http11.response;

import org.apache.coyote.http11.Http11Cookie;

public class HttpResponse {
    private String path;
    private String fileType;
    private HttpStatusCode httpStatusCode;
    private Http11Cookie http11Cookie;
    private String responseBody;

    public HttpResponse(String path, String fileType, HttpStatusCode httpStatusCode, Http11Cookie http11Cookie, String responseBody) {
        this.path = path;
        this.fileType = fileType;
        this.httpStatusCode = httpStatusCode;
        this.http11Cookie = http11Cookie;
        this.responseBody = responseBody;
    }

    public HttpResponse() {
        this.path = null;
        this.fileType = null;
        this.httpStatusCode = null;
        this.http11Cookie = null;
        this.responseBody = null;
    }

    @Override
    public String toString() {
        if (httpStatusCode.is2xxCode()) {
            return response2xx();
        }
        if (httpStatusCode.is3xxCode()) {
            return response3xx();
        }
        throw new IllegalArgumentException("Invalid Response");
    }

    private String response2xx() {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatusCode.getCode() + " " + httpStatusCode.getStatusPhrase(),
                includeCookie(),
                "Content-Type: text/" + fileType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String response3xx() {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatusCode.getCode() + " " + httpStatusCode.getStatusPhrase(),
                includeCookie(),
                "Location: http://localhost:8080" + path,
                "Content-Type: text/" + fileType + ";charset=utf-8 ");
    }

    private String includeCookie() {
        if (http11Cookie != null) {
            return "Set-Cookie:" + http11Cookie.toString() + " ";
        }
        return null;
    }

    public String getPath() {
        return path;
    }

    public String getFileType() {
        return fileType;
    }

    public Http11Cookie getHttp11Cookie() {
        return http11Cookie;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public void setHttp11Cookie(Http11Cookie http11Cookie) {
        this.http11Cookie = http11Cookie;
    }
}
