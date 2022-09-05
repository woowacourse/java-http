package org.apache.coyote.http11;

public class HttpResponse {

    private final HttpRequest request;
    private final String version;
    private final StatusCode statusCode;
    private final String responseBody;
    private final String location;
    private String response;

    public HttpResponse(HttpRequest request, StatusCode statusCode, String responseBody) {
        this.request = request;
        this.version = request.getHttpVersion();
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.location = "";
        this.response = getResponse();
    }

    public HttpResponse(HttpRequest request, StatusCode statusCode, String responseBody, String location) {
        this.request = request;
        this.version = request.getHttpVersion();
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.location = location;
        this.response = getResponse();
    }

    public static HttpResponse redirect(HttpRequest request, String location) {
        return new HttpResponse(request, StatusCode.FOUND, "", location);
    }

    public String getResponse() {
        String headers = makeHeaders();
        if (!"".equals(location)) {
            headers = String.join("\r\n",
                headers,
                "Location: " + location);
        }

        return String.join("\r\n",
            headers,
            "",
            responseBody);
    }

    public String getResponse(String header) {
        String headers = makeHeaders();
        if (!"".equals(location)) {
            headers = String.join("\r\n",
                headers,
                "Location: " + location);
        }

        if (!"".equals(header)) {
            headers = String.join("\r\n",
                headers,
                "Set-Cookie: " + header);
        }

        return String.join("\r\n",
            headers,
            "",
            responseBody);
    }

    private String makeHeaders() {
        return String.join("\r\n",
            version + " " + statusCode.getCode() + " " + statusCode.name() + " ",
            parseContentType()
            ,
            "Content-Length: " + responseBody.getBytes().length + " ");
    }

    public String parseContentType() {
        if (request.containsHeader("Accept") && request.getHeaderValue("Accept").contains("text/css")) {
            return "Content-Type: text/css;";
        }
        return "Content-Type: text/html;charset=utf-8 ";
    }

    public byte[] getBytes() {
        return this.response.getBytes();
    }

    public void setCookie(HttpCookie cookie) {
        this.response = getResponse(cookie.getCookieValue("JSESSIONID"));
    }
}
