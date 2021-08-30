package nextstep.jwp.http;

public class HttpResponse {
    private final String response;

    public HttpResponse(final HttpStatus status, final String contentType, final String responseBody) {
        this.response = String.join("\r\n",
                "HTTP/1.1 " + status.toString(),
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    public String getResponse() {
        return response;
    }
}
