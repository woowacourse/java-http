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

            String uri = request.getUri();
            ContentType contentType = ContentType.from(uri);
            String responseBody = getBody(request.getUri());

            String response = getResponse(contentType, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
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
