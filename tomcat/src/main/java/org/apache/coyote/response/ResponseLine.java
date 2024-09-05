package org.apache.coyote.response;

public class ResponseLine implements Assemblable {

    private final StatusCode statusCode;

    public ResponseLine(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void assemble(StringBuilder builder) {
        builder.append("HTTP/1.1 ");
        statusCode.assemble(builder);
    }
}
