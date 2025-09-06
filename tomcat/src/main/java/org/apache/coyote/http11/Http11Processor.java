package org.apache.coyote.http11;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (var input = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
             var out = new BufferedOutputStream(connection.getOutputStream())) {

            String requestLine = input.readLine();
            if (requestLine == null) {
                return;
            }

            String[] tokens = requestLine.split(" ");
            String path = tokens[1];

            log.info(path);

            if ("/".equals(path) || "/index.html".equals(path)) {
                serveStatic(out, "static/index.html", "text/html; charset=UTF-8");
                return;
            }

            if ("/css/styles.css".equals(path)) {
                serveStatic(out, "static/css/styles.css", "text/css; charset=UTF-8");
                return;
            }

            sendError(out, 404, "Not Found");
        } catch (Exception e) {
            try (var out = new BufferedOutputStream(connection.getOutputStream())) {
                log.error(e.getMessage());
                sendError(out, 500, "Internal Server Error");
            } catch (IOException ioException) {
                log.error(ioException.getMessage());
            }
        }
    }

    private void serveStatic(BufferedOutputStream out, String resourcePath, String contentType) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                sendError(out, 404, "Not Found");
                return;
            }
            byte[] body = in.readAllBytes();
            write(out, 200, "OK", contentType, body, false);
        }
    }

    private void write(BufferedOutputStream out, int status, String reason,
                       String contentType, byte[] body, boolean keepAlive) throws IOException {
        String head = ""
                + "HTTP/1.1 " + status + " " + reason + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + (keepAlive ? "Connection: keep-alive\r\n" : "Connection: close\r\n")
                + "\r\n";
        out.write(head.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    private void sendError(BufferedOutputStream out, int status, String reason) {
        String errorResource = "static/" + status + ".html";

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(errorResource)) {
            byte[] errorBody = getErrorBody(status, reason, inputStream);

            String header = String.format("""
                            HTTP/1.1 %d %s\r
                            Content-Type: text/html; charset=UTF-8\r
                            Content-Length: %d\r
                            Connection: keep-alive\r
                            \r
                            """,
                    status,
                    reason,
                    errorBody.length
            );

            out.write(header.getBytes(StandardCharsets.UTF_8));
            out.write(errorBody);
            out.flush();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private byte[] getErrorBody(int status, String reason, InputStream inputStream) throws IOException {
        if (inputStream != null) {
            return inputStream.readAllBytes();
        }
        return ("<h1>" + status + " " + reason + "</h1>").getBytes(StandardCharsets.UTF_8);
    }
}
