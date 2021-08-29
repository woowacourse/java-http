package nextstep.jwp.webserver;

public class HttpResponse {

    private StatusCode statusCode;
    private HttpHeaders headers;
    private String body;

    public HttpResponse(StatusCode statusCode, HttpHeaders headers, String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse(String body) {
        this(StatusCode._200_OK, HttpHeaders.EMPTY_HEADERS, body);
    }

    public static HttpResponse ok(String body) {
        return new HttpResponse(StatusCode._200_OK, HttpHeaders.EMPTY_HEADERS, body);
    }

    public static HttpResponse redirect(String redirectUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Location", redirectUrl);
        return new HttpResponse(StatusCode._302_FOUND, headers, "");
    }

    private void setDefaultHeaders() {
        if (body != null && body.length() > 0) {
            headers.set("Content-Length", String.valueOf(body.getBytes().length));
            headers.set("Content-Type", "text/html;charset=utf-8");
        }
    }

    public byte[] toBytes() {
        setDefaultHeaders();

        String response = String.join("\r\n",
                "HTTP/1.1 " + statusCode.getString(),
                headers.getString());

        if (body != null && body.length() > 0) {
            response = String.join("\r\n", response, "", body);
        }

        return response.getBytes();
    }
}
