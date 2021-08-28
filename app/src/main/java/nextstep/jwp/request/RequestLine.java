package nextstep.jwp.request;

public class RequestLine {

    private final String line;

    public RequestLine(String line) {
        this.line = line;
    }

    public String[] split(String delimiter) {
        return line.split(delimiter);
    }

    public String getLine() {
        return line;
    }
}
