package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.PathResponse;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.StaticResponse;
import org.apache.coyote.response.StringResponse;
import org.apache.front.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private final Proxy proxy;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.proxy = new Proxy();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            Request request = Request.from(inputStream);
            Response response = proxy.process(request);
            writeResponse(outputStream, response);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeResponse(OutputStream outputStream, Response response) throws IOException {
        if (response instanceof StaticResponse) {
            final BufferedReader fileReader = findView(response.getPath());
            final String responseField = writeResponse(response, fileReader);

            outputStream.write(responseField.getBytes());
            outputStream.flush();
        }

        if (response instanceof PathResponse) {
            final BufferedReader fileReader = findView(response.getPath());
            final String responseField = writeResponse(response, fileReader);

            outputStream.write(responseField.getBytes());
            outputStream.flush();
        }

        if(response instanceof StringResponse){
            final String responseField = makeResponse(response, response.getBodyString());

            outputStream.write(responseField.getBytes());
            outputStream.flush();
        }
    }

    private static String writeResponse(Response response, BufferedReader fileReader) {
        final StringBuilder actual = new StringBuilder();
        fileReader.lines()
                .forEach(br -> actual.append(br)
                        .append(System.lineSeparator()));
        final var responseBody = actual.toString();

        return makeResponse(response, responseBody);
    }

    private static String makeResponse(Response response, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + response.getFileType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private BufferedReader findView(String viewPath) throws FileNotFoundException {
        final URL resource = getClass().getClassLoader().getResource("static" + viewPath);
        final Path path = Paths.get(resource.getPath());
        return new BufferedReader(new FileReader(path.toFile()));
    }
}
