package nextstep.jwp.webserver;

import java.util.Objects;

public class HttpResponse {

    private StatusCode statusCode = StatusCode._200_OK;
    private HttpHeaders headers = new HttpHeaders();
    private String body = "";

    public static void errorPage(BaseException baseException, HttpResponse response) {
        String body = readErrorPage(baseException);

        response.addHeaders("Content-Type", "text/html;charset=utf-8");
        response.setStatusCode(StatusCode._302_FOUND);
        response.setBody(body);
    }

    private static String readErrorPage(BaseException baseException) {
        int statusCode = baseException.getStatusCode();

        if (statusCode == 401) {
            return FileReader.readStaticFile("401.html");
        }
        if (statusCode == 404 || statusCode == 405) {
            return FileReader.readStaticFile("404.html");
        }
        return FileReader.readStaticFile("500.html");
    }

    public String readAsString() {
        setContentLength();

        return String.join("\r\n",
                "HTTP/1.1 " + statusCode.getString(),
                headers.getString(),
                "",
                Objects.requireNonNullElse(body, ""));
    }

    private void setContentLength() {
        if (hasBody()) {
            headers.set("Content-Length", String.valueOf(body.getBytes().length));
        }
    }

    private boolean hasBody() {
        return Objects.nonNull(body) && body.length() > 0;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void addHeaders(String key, String value) {
        headers.set(key, value);
    }
}
