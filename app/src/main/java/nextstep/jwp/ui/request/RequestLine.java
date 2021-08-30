package nextstep.jwp.ui.request;

public class RequestLine {
    private final String[] splitRequestLine;

    public RequestLine(String requestLine) {
        this.splitRequestLine = requestLine.split(" ");
    }

    public String getMethod() {
        return splitRequestLine[0];
    }

    public String getRequestURI() {
        return splitRequestLine[1];
    }

    public String getQueryString() {
        String requestURI = splitRequestLine[1];
        int index = requestURI.indexOf("?");
        if (index != -1) {
            return requestURI.substring(index + 1);
        }
        return null;
    }

    public String getPath() {
        String requestURI = getRequestURI();
        int index = requestURI.indexOf("?");
        if (index != -1) {
            return requestURI.substring(0, index);
        }
        return requestURI;
    }
}
