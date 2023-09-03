package kokodak;

import static kokodak.Constants.CRLF;

import java.util.ArrayList;
import java.util.List;

public class HttpRequestMessage {

    private static final int HTTP_REQUEST_MESSAGE_START_LINE = 0;

    private String startLine;

    private List<String> headers;

    private List<String> body;

    public HttpRequestMessage(final List<String> primitiveRequest) {
        boolean isBody = false;
        startLine = primitiveRequest.get(HTTP_REQUEST_MESSAGE_START_LINE);
        validateStartLine(startLine);
        headers = new ArrayList<>();
        body = new ArrayList<>();

        for (int i = 1; i < primitiveRequest.size(); i++) {
            final String request = primitiveRequest.get(i);
            if (isBody) {
                body.add(request);
            } else if (request.equals(CRLF.getValue())) {
                isBody = true;
            } else {
                headers.add(request);
            }
        }
    }

    private void validateStartLine(final String startLine) {
        if (startLine.split(" ").length != 3) {
            throw new IllegalArgumentException("Invalid HTTP Request Message");
        }
    }

    public String getHttpMethod() {
        return startLine.split(" ")[0];
    }

    public String getRequestTarget() {
        return startLine.split(" ")[1];
    }

    public String getHttpVersion() {
        return startLine.split(" ")[2];
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<String> getBody() {
        return body;
    }
}
