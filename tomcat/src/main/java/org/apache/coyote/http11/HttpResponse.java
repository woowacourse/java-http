package org.apache.coyote.http11;

public class HttpResponse {

    private final HttpRequest request;
    private final String version;
    private final StatusCode statusCode;
    private final String responseBody;
    private final String location;

    public HttpResponse(HttpRequest request, StatusCode statusCode, String responseBody) {
        this.request = request;
        this.version = request.getHttpVersion();
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.location = "";
    }

    public HttpResponse(HttpRequest request, StatusCode statusCode, String responseBody, String location) {
        this.request = request;
        this.version = request.getHttpVersion();
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.location = location;
    }

    public static HttpResponse redirect(HttpRequest request, String location) {
        return new HttpResponse(request, StatusCode.FOUND, "", location);
    }

    public String getResponse() {
        if (!location.equals("")) {
            return String.join("\r\n",
                version + " " + statusCode.getCode() + " " + statusCode.name() + " ",
                "Location: " + location,
                parseContentType()
                ,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        }

        return String.join("\r\n",
            version + " " + statusCode.getCode() + " " + statusCode.name() + " ",
            parseContentType()
            ,
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

    public String parseContentType() {
        if (request.containsHeader("Accept") && request.getHeaderValue("Accept").contains("text/css")) {
            return "Content-Type: text/css;";
        }
        return "Content-Type: text/html;charset=utf-8 ";
    }

    public byte[] getBytes() {
        return getResponse().getBytes();
    }
}
