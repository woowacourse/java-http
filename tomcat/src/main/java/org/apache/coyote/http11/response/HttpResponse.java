package org.apache.coyote.http11.response;

import org.apache.coyote.http11.Http11Cookie;

public class HttpResponse {
    private final String path;
    private final String fileType;
    private final HttpStatusCode httpStatusCode;
    private final Http11Cookie http11Cookie;
    private String responseBody;

    public HttpResponse(String path, String fileType, HttpStatusCode httpStatusCode, Http11Cookie http11Cookie, String responseBody) {
        this.path = path;
        this.fileType = fileType;
        this.httpStatusCode = httpStatusCode;
        this.http11Cookie = http11Cookie;
        this.responseBody = responseBody;
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
                "Content-Type: text/" + fileType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String response3xx() {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatusCode.getCode() + " " + httpStatusCode.getStatusPhrase(),
                includeCookie(),
                "Location: " + path,
                "Content-Type: text/" + fileType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String includeCookie() {
        if (http11Cookie != null) {
            return "Set-Cookie:" + http11Cookie.toString() + " ";
        }
        return "";
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
}
