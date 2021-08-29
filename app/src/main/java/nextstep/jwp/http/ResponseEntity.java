package nextstep.jwp.http;

public class ResponseEntity {

    private final StatusCode statusCode;
    private final String responseBody;

    private ResponseEntity(StatusCode statusCode, String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    private ResponseEntity(StatusCode statusCode) {
        this(statusCode, "");
    }

    private ResponseEntity(String responseBody) {
        this(StatusCode.OK, responseBody);
    }

    public static ResponseEntity statusCode(StatusCode statusCode) {
        return new ResponseEntity(statusCode);
    }

    public static ResponseEntity responseBody(String responseBody) {
        return new ResponseEntity(responseBody);
    }

    public String build() {
        return String.join("\r\n",
                "HTTP/1.1 " + this.statusCode.getStatusCode() + " " + this.statusCode.getStatus() + " ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
