package nextstep.jwp;

public class RequestLine {
    public static final String SPACE = " ";
    private final String method;
    private final String path;

    public RequestLine(String line) {
        String[] splitRequestLine = parseRequestLine(line);
        this.method = splitRequestLine[0];
        this.path = splitRequestLine[1];
    }

    private String[] parseRequestLine(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Request Line이 존재하지 않습니다.");
        }
        return line.split(SPACE);
    }

    public boolean isGet() {
        return "GET".equals(method);
    }

    public boolean isPost() {
        return "POST".equals(method);
    }

    public String getPath() {
        return path;
    }
}
