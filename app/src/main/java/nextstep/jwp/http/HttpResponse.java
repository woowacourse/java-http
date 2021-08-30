package nextstep.jwp.http;

import java.util.LinkedList;
import java.util.List;

public class HttpResponse {

    private final List<String> header = new LinkedList<>();
    private String body;

    public HttpResponse() {
    }

    public byte[] getBytes() {
        String response = String.join("\r\n", header);
        if (body != null) {
            response += body;
        }
        return response.getBytes();
    }

    public void addStartLine(String version, String statusCode, String statusMessage) {
        header.add(0, version + " " + statusCode + " " + statusMessage + " ");
    }

    public void addLocation(String location) {
        header.add("Location: " + location + " ");
    }

    public void addContentType(String contentType) {
        header.add("Content-Type: " + contentType + " ");
    }

    public void addContentLength(int contLength) {
        header.add("Content-Length: " + contLength + " ");
    }

    public void addBody(String body) {
        addContentLength(body.length());
        this.body = "\r\n\r\n" + body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
