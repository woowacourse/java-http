package org.apache.coyote.http11;

public class HttpRequest {

    private final HttpStartLine startLine;
    private final String headers;
    private final String body;

    public HttpRequest(String request) {
        startLine = new HttpStartLine(parseStartLine(request));
        headers = parseHeaders(request);
        body = parseBody(request);
    }

    private String parseStartLine(String request) {
        String[] messages = request.split("\r\n");
        return messages[0];
    }

    private String parseHeaders(String request) {
        String[] messages = request.split("\r\n");
        StringBuilder headers = new StringBuilder();

        for (int i = 1; i < messages.length; i++) {
            if (messages[i].isEmpty()) {
                for (int j = i; j < messages.length; j++) {
                    headers.append(messages[i]).append("\r\n");
                }
            }
        }
        return headers.toString();
    }

    private String parseBody(String request) {
        String[] messages = request.split("\r\n");
        StringBuilder body = new StringBuilder();

        for (int i = 1; i < messages.length; i++) {
            if (messages[i].isEmpty()) {
                break;
            }
            body.append(messages[i]).append("\r\n");
        }
        return body.toString();
    }

    public String getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getPath() {
        return startLine.getPath();
    }
}
