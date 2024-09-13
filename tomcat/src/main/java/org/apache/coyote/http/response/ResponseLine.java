package org.apache.coyote.http.response;

import org.apache.coyote.http.StatusCode;

public class ResponseLine implements Assemblable {

    private static final StatusCode DEFAULT_STATUS_CODE = StatusCode.OK;

    private StatusCode statusCode;

    protected ResponseLine() {
        this.statusCode = DEFAULT_STATUS_CODE;
    }

    protected void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    protected boolean isRedirect() {
        return StatusCode.FOUND.equals(statusCode);
    }

    @Override
    public void assemble(StringBuilder builder) {
        builder.append("HTTP/1.1 ");
        statusCode.assemble(builder);
    }
}
