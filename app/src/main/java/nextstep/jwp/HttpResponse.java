package nextstep.jwp;

import java.util.LinkedList;
import java.util.List;

public class HttpResponse {

    List<String> header = new LinkedList<>();
    String body;

    public HttpResponse(String version, String statusCode, String statusMessage) {
        header.add(version + " " + statusCode + " " + statusMessage + " ");
    }

    public byte[] getBytes() {
        String response = String.join("\r\n", header) + body;
        return response.getBytes();
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
