package nextstep.jwp;

public class HttpResponse {

    private int statusCode;
    private String statusMessage;
    private String body;

    public HttpResponse() {
        this.statusCode = 200;
        this.statusMessage = "OK";
        this.body = "";
    }

    public void statusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void statusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void body(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return String.join("\r\n",
            "HTTP/1.1 " + statusCode + " " + statusMessage + " ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + body.getBytes().length + " ",
            "",
            body);
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }
}
