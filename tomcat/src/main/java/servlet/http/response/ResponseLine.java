package servlet.http.response;

import servlet.http.StatusCode;

public class ResponseLine implements Assemblable {

    private StatusCode statusCode;

    private ResponseLine(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    protected static ResponseLine create() {
        return new ResponseLine(StatusCode.OK);
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
