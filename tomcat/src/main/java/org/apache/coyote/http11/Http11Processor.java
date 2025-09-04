package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static final int REQUEST_URL_INDEX = 1;

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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            final String requestURL = parseRequestURL(inputStream);
            sendResponse(requestURL, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequestURL(final InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = reader.readLine();
        return requestLine.split(" ")[REQUEST_URL_INDEX];
    }

    private void sendResponse(final String requestURL, final OutputStream outputStream) throws IOException {
        final URL resource = getStaticResource(requestURL);
        final String responseBody = new String(Files.readAllBytes(Paths.get(resource.getFile())));
        final String response = createHttpResponse(responseBody);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private URL getStaticResource(final String requestURL) {
        if (requestURL.equals("/")) {
            return getStaticResource("static/index.html");
        }

        URL resource = getClass().getClassLoader().getResource(String.format("static%s", requestURL));
        if (resource == null) {
            return getClass().getClassLoader().getResource("static/404.html");
        }
        return resource;
    }

    private static String createHttpResponse(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
