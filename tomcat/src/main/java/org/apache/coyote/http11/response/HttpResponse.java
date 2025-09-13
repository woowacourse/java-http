package org.apache.coyote.http11.response;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpResponse {

    private final OutputStream outputStream;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void sendFile(Path path) throws IOException {
        try(InputStream inputStream = Files.newInputStream(path)) {
            inputStream.transferTo(outputStream);
        }
    }

    public void sendResponse(String response) throws IOException {
        outputStream.write(response.getBytes(UTF_8));
        outputStream.flush();
    }

    public String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"
        );
    }

    public String buildRedirectHeaders(String location, String cookie) {
        return "HTTP/1.1 302 Found\r\n"
                + "Location: " + location + "\r\n"
                + "Content-Length: 0\r\n"
                + "Set-Cookie: " + cookie + "; Path=/; HttpOnly\r\n"
                + "\r\n";
    }

    public String buildRedirectHeaders(String location) {
        return "HTTP/1.1 302 Found\r\n"
                + "Location: " + location + "\r\n"
                + "Content-Length: 0\r\n"
                + "\r\n";
    }

    public String getResponse(Path path) throws IOException {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + Files.probeContentType(path) + ";charset=utf-8 ",
                "Content-Length: " + Files.size(path) + " ",
                "", "");
    }
}
