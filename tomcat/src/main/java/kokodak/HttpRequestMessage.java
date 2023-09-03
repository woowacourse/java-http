package kokodak;

import static kokodak.Constants.CRLF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestMessage {

    private static final int HTTP_REQUEST_MESSAGE_START_LINE = 0;

    private String startLine;

    private List<String> headers;

    private List<String> body;

    private Map<String, String> queryString;

    public HttpRequestMessage(final List<String> primitiveRequest) {
        headers = new ArrayList<>();
        body = new ArrayList<>();
        queryString = new HashMap<>();

        boolean isBody = false;
        startLine = primitiveRequest.get(HTTP_REQUEST_MESSAGE_START_LINE);
        validateStartLine(startLine);

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

        final String requestTarget = startLine.split(" ")[1];
        final String[] split = requestTarget.split("\\?");
        if (split.length > 1) {
            final String[] queries = split[1].split("&");
            for (final String query : queries) {
                final String[] keyValue = query.split("=");
                queryString.put(keyValue[0], keyValue[1]);
            }
        }
    }

    private void validateStartLine(final String startLine) {
        if (startLine.split(" ").length != 3) {
            throw new IllegalArgumentException("Invalid HTTP Request Message");
        }
    }

    public boolean hasQueryString() {
        return !queryString.isEmpty();
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

    public Map<String, String> getQueryString() {
        return queryString;
    }
}
