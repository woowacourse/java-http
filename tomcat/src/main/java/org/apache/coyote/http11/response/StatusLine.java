package org.apache.coyote.http11.response;

public class StatusLine { // HTTP/1.1 200 OK

    private final String protocol; // HTTP/1.1
    private HttpStatus httpStatus; // 200 OK

    public StatusLine() { // TODO 2025. 9. 12. 05:14: 200 일 때를 위해 만들어놓은 것이 아니라, 그냥 new StatusLine을 진행했을 때 200 Status가 저장되어 있을 것이라 생각 못할 듯
        this.protocol = "HTTP/1.1";
        this.httpStatus = HttpStatus.OK;
    }

    public StatusLine(HttpStatus httpStatus) {
        this.protocol = "HTTP/1.1";
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return String.join(" ",
                protocol,
                httpStatus.toString());
    }
}
