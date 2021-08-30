package nextstep.jwp.httpmessage;

public class RequestLine {

    private static final String LINE_DELIMITER = " ";

    private final String line;
    private final String[] splitLine;

    public RequestLine(String line) {
        this(line, line.split(LINE_DELIMITER));
    }

    public RequestLine(String line, String[] splitLine) {
        this.line = line;
        this.splitLine = splitLine;
    }

    public String getLine() {
        return line;
    }

    public HttpMethod getMethod() {
        return HttpMethod.find(splitLine[0]);
    }

    public String getPath() {
        return splitLine[1];
    }

    public String getVersionOfTheProtocol() {
        return splitLine[2];
    }
}
