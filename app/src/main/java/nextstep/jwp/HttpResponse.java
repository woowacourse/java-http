package nextstep.jwp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private final OutputStream outputStream;
    private Map<String, String> headers;
    private String responseLine;
    private String response;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.headers = new LinkedHashMap<>();
    }

    public void setStatus(int code) {
        this.responseLine = "HTTP/1.1 " + HttpStatus.convert(code) + " ";
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void write(String message) {
        if (response != null) {
            response += message;
            return;
        }
        String headers = this.headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
        response = String.join("\r\n",
                responseLine,
                headers,
                "",
                message);
    }

    public void flush() throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    public String getResponseLine() {
        return responseLine;
    }

    public String getResponse() {
        return response;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void sendRedirect(String url) throws IOException {
        response = String.join("\r\n",
                responseLine,
                "Location: " + url);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
