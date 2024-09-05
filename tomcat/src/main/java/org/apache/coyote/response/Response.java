package org.apache.coyote.response;

public class Response {

    private final ResponseLine responseLine;

    private final ResponseHeaders headers;

    private final ResponseBody body;

    public Response(ResponseLine responseLine, ResponseHeaders headers, ResponseBody body) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.body = body;
    }

    public String build() {
        StringBuilder builder = new StringBuilder();
        responseLine.assemble(builder);
        headers.assemble(builder);
        body.assemble(builder);
        return builder.toString();
    }
}
