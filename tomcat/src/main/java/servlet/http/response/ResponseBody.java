package servlet.http.response;

public class ResponseBody implements Assemblable {

    private static final String EMPTY_BODY = "";

    private String body;

    protected ResponseBody() {
        this(EMPTY_BODY);
    }

    private ResponseBody(String body) {
        this.body = body;
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
