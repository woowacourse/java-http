package servlet.http.response;

import servlet.http.StatusCode;

public class ResponseLine implements Assemblable {

    private static final StatusCode DEFAULT_STATUS_CODE = StatusCode.OK;

    private StatusCode statusCode;

    protected ResponseLine() {
        this(DEFAULT_STATUS_CODE);
    }

    private ResponseLine(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    protected void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void assemble(StringBuilder builder) {
        builder.append("HTTP/1.1 ");
        statusCode.assemble(builder);
    }
}
