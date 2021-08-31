package nextstep.jwp.httpmessage;

import static nextstep.jwp.httpmessage.HttpMessageReader.SP;

public class RequestLine {

    private final String line;
    private final String[] splitLine;

    public RequestLine(String line) {
        this(line, line.split(SP));
    }

    public RequestLine(String line, String[] splitLine) {
        this.line = line;
        this.splitLine = splitLine;
    }

    public String getLine() {
        return line;
    }

    public HttpMethod getMethod() {
        return HttpMethod.findBy(splitLine[0]);
    }

    public String getPath() {
        return splitLine[1];
    }

    public String getVersionOfTheProtocol() {
        return splitLine[2];
    }
}
