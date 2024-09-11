package org.apache.coyote.http11.response;

import org.apache.coyote.http11.Http11Cookie;

public class HttpResponse {
    private final String path;
    private final String fileType;
    private final Integer statusCode;
    private final String statusPhrase;
    private final Http11Cookie http11Cookie;
    private String responseBody;

    public HttpResponse(String path, String fileType, Integer statusCode, String statusPhrase,
                        Http11Cookie http11Cookie, String responseBody) {
        this.path = path;
        this.fileType = fileType;
        this.statusCode = statusCode;
        this.statusPhrase = statusPhrase;
        this.http11Cookie = http11Cookie;
        this.responseBody = responseBody;
    }

    public String response2xx() {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " " + statusPhrase,
                "Content-Type: text/" + fileType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    public String response3xx() {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " " + statusPhrase,
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

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getStatusPhrase() {
        return statusPhrase;
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
