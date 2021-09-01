package nextstep.jwp.webserver;

public class HttpResponse {

    private StatusCode statusCode = StatusCode._200_OK;
    private HttpHeaders headers = new HttpHeaders();
    private String body = "";

    public HttpResponse() {
    }

    public static void errorPage(BaseException baseException, HttpResponse response) {
        String body = readErrorPage(baseException);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "text/html;charset=utf-8");
        response.setHeaders(headers);
        response.setStatusCode(StatusCode._302_FOUND);
        response.setBody(body);
    }

    private static String readErrorPage(BaseException baseException) {
        if (baseException.getStatusCode() == 401) {
            return FileReader.readStaticFile("401.html");
        }
        if (baseException.getStatusCode() == 404) {
            return FileReader.readStaticFile("404.html");
        }
        return FileReader.readStaticFile("500.html");
    }

    private void setDefaultHeaders() {
        if (body != null && body.length() > 0) {
            headers.set("Content-Length", String.valueOf(body.getBytes().length));
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

    public void setBody(String body) {
        this.body = body;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }
}
