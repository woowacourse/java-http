package org.apache.coyote.response;

public class ResponseBody implements Assemblable {

    private final String body;

    protected ResponseBody(String body) {
        this.body = body;
    }

    @Override
    public void assemble(StringBuilder builder) {
        builder.append(body);
    }
}
