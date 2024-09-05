package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (var inputStream = connection.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader requestBufferedReader = new BufferedReader(inputStreamReader);
             var outputStream = connection.getOutputStream()) {

            Request request = Request.parseFrom(getPlainRequest(requestBufferedReader));
            log.info("request : {}", request);
            String response = getResponseString(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> getPlainRequest(BufferedReader requestBufferedReader) throws IOException {
        List<String> plainRequest = new ArrayList<>();
        while (requestBufferedReader.ready()) {
            String line = requestBufferedReader.readLine();
            plainRequest.add(line);
        }
        return Collections.unmodifiableList(plainRequest);
    }

    private String getResponseString(Request request) throws IOException {
        if (request.getMethod() == Method.GET) {
            Content content = getContent(request);

            return String.join("\r\n",
                    String.format("%s 200 OK ", request.getHttpVersion()),
                    String.format("Content-Type: %s;charset=utf-8 ", content.getContentType()),
                    "Content-Length: " + content.getContent().getBytes().length + " ",
                    "",
                    content.getContent());
        }
        return null;
    }

    private Content getContent(Request request) throws IOException {
        if (request.getTarget().equals("/")) {
            return new Content("index.html");
        }
        return new Content(request.getTarget());
    }
}
