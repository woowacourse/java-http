package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.StaticFile;
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
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            List<String> httpRequestLines = getHttpRequestLines(inputStream);
            HttpRequest request = HttpRequest.from(httpRequestLines);

            handle(request, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handle(HttpRequest request, OutputStream outputStream) throws IOException {
        String path = request.path();
        if (path.contains(".")) {
            handleStatic(path, outputStream);
            return;
        }
        handleApi(path, outputStream);
    }

    private void handleStatic(String path, OutputStream outputStream) throws IOException {
        ContentType contentType = ContentType.from(path);
        String responseBody = getBody(path);
        String response = getResponse(contentType, responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void handleApi(String path, OutputStream outputStream) throws IOException {
        if (path.equals("/login")) {
            handleStatic("/login.html", outputStream);
            return;
        }
        handleStatic("/404.html", outputStream);
    }

    private List<String> getHttpRequestLines(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> collect = new ArrayList<>();
        while (bufferedReader.ready()) {
            collect.add(bufferedReader.readLine());
        }
        return collect;
    }

    private String getBody(String uri) throws IOException {
        return new String(Files.readAllBytes(StaticFile.findByUrl(uri).toPath()));
    }

    private String getResponse(ContentType contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.value() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
