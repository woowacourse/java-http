package org.apache.coyote.http11;

public class HttpResponse {
    private final String version;
    private final String status;
    private final String contentType;
    private final String location;
    private final String responseBody;

    public static HttpResponse withoutLocation(String version, String status, String uri, String responseBody) {
        return new HttpResponse(version, status, uri, "", responseBody);
    }

    public static HttpResponse withLocation(String version, String status, String uri, String location,
        String responseBody) {
        return new HttpResponse(version, status, uri, location, responseBody);
    }

    private HttpResponse(String version, String status, String uri, String location, String responseBody) {
        this.version = version;
        this.status = status;
        this.responseBody = responseBody;
        this.location = location;

        if (uri.endsWith("css")) {
            this.contentType = "text/css,*/*;q=0.1";
        } else {
            this.contentType = "text/html;charset=utf-8";
        }
    }

    public String toResponseString() {
        StringBuilder response = new StringBuilder();

        response.append(version + " " + status + " \r\n");
        response.append("Content-Type: " + contentType + " \r\n");
        response.append("Content-Length: " + responseBody.getBytes().length + " \r\n");
        if (!location.equals("")) {
            response.append("Location: " + location + "\r\n");
        }
        response.append("\r\n");
        response.append(responseBody);

        return response.toString();
    }
}
