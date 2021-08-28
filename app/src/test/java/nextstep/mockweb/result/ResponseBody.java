package nextstep.mockweb.result;

public class ResponseBody {

    private static final String ENTER = "\r\n";

    private StringBuilder body = new StringBuilder();

    public void addBodyByLine(String lineCount) {
        body.append(lineCount);
        body.append(ENTER);
    }

    public String get() {
        return body.toString();
    }
}
