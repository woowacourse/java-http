package org.apache.coyote.response;

public class ResponseBody implements Assemblable {

    private final String body;

    public ResponseBody(String body) {
        this.body = body;
    }

    public int length() {
        return body.getBytes().length;
    }

    @Override
    public void assemble(StringBuilder builder) {
        builder.append(body);
    }
}
