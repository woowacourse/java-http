package nextstep.jwp.http.reponse;

public class HttpStatusLine {

    private static final String PROTOCOL = "HTTP/1.1";

    private final HttpStatus httpStatus;

    public HttpStatusLine(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getValue() {
        return PROTOCOL + " " + httpStatus.getCode() + " " + httpStatus.getMessage() + " ";
    }
}
