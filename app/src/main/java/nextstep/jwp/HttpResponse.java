package nextstep.jwp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private final OutputStream outputStream;
    private String responseLine;
    private String response;
    private HttpHeaders headers;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.headers = new HttpHeaders(new LinkedHashMap<>());
    }

    public void setStatus(int code) {
        responseLine = "HTTP/1.1 " + HttpStatus.convert(code) + " ";
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void write(String message) throws IOException {
        if (response != null) {
            response += message;
            return;
        }
        response = String.join("\r\n",
                responseLine,
                headers.convertToLines(),
                "",
                message);
        outputStream.write(response.getBytes());
    }

    public void flush() throws IOException {
        outputStream.flush();
    }

    public String getResponseLine() {
        return responseLine;
    }

    public String getResponse() {
        return response;
    }

    public Map<String, String> getHeaders() {
        return headers.getHeaders();
    }

    public void sendRedirect(String url) throws IOException {
        sendRedirect(url, 302);
    }

    public void sendRedirect(String url, int code) throws IOException {
        setStatus(code);
        response = String.join("\r\n",
                responseLine,
                "Location: " + url);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    public void forward(String url) throws IOException {
        ResourceFile resourceFile = new ResourceFile(url);
        String content = resourceFile.getContent();
        setStatus(200);
        addHeader("Content-Type", resourceFile.getContentType());
        addHeader("Content-Length", String.valueOf(content.getBytes().length));
        write(content);
        flush();
    }
}
