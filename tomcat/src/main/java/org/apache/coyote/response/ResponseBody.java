package org.apache.coyote.response;

public class ResponseBody implements Assemblable {

    private static final String EMPTY_BODY = "";

    private String body;

    private ResponseBody(String body) {
        this.body = body;
    }

    protected static ResponseBody create() {
        return new ResponseBody(EMPTY_BODY);
    }

    protected void setBody(String body) {
        this.body = body;
    }

    protected int length() {
        return body.getBytes().length;
    }

    @Override
    public void assemble(StringBuilder builder) {
        builder.append(body);
    }
}
