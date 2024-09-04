package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.RequestStartLine;
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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            RequestStartLine startLine = readStartLine(reader);
            log.info("start line = {}", startLine);

            String response = makeDefaultResponse();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestStartLine readStartLine(BufferedReader reader) throws IOException {
        String[] startLine = readLine(reader).split(" ");
        if (startLine.length != 3) {
            throw new UncheckedServletException("요청 시작 라인의 형식이 일치하지 않습니다.");
        }

        return new RequestStartLine(startLine[0], startLine[1], startLine[2]);
    }

    private String readLine(BufferedReader reader) throws IOException {
        String startLine = reader.readLine();
        if (startLine == null) {
            throw new UncheckedServletException("HTTP 형식이 올바르지 않습니다");
        }
        return startLine;
    }

    private String makeDefaultResponse() {
        String responseBody = "Hello world!";

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
