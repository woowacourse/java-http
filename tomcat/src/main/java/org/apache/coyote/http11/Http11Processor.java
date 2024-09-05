package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.request.Request;
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
            String response = getResponseString(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> getPlainRequest(BufferedReader requestBufferedReader) throws IOException {
        List<String> plainRequest = new ArrayList<>();
        while(requestBufferedReader.ready()) {
            String line = requestBufferedReader.readLine();
            plainRequest.add(line);
        }
        return Collections.unmodifiableList(plainRequest);
    }

    private String getResponseString(Request request) throws IOException {
        if (request.getMethod() == Method.GET) {
            String content = getContent(request);

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + content.getBytes().length + " ",
                    "",
                    content);
        }
        return null;
    }

    private String getContent(Request request) throws IOException {
        if (request.getTarget().equals("/")) {
            return "Hello world!";
        }
        String name = "static" + request.getTarget();
        URL resource = getClass().getClassLoader().getResource(name);
        return new String(Files.readAllBytes(new File(resource.getPath()).toPath()));
    }
}
