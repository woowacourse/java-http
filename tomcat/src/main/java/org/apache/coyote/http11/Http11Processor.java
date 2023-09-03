package org.apache.coyote.http11;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();
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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            HttpRequest request = new HttpRequest(inputStream);
            String url = request.getUrl();
            if (Objects.equals(url, "/")) {
                String responseBody = "Hello world!";

                String response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
            } else {
                try (
                        FileInputStream fileStream = new FileInputStream(getStaticResourceURL(url).getFile())
                ) {
                    String path = getStaticResourceURL(url).getFile();
                    String extension = path.split("\\.")[1];
                    byte[] fileBytes = fileStream.readAllBytes();
                    outputStream.write(String.join("\r\n",
                            "HTTP/1.1 200 OK ",
                            "Content-Type: text/" + extension + ";charset=utf-8 ",
                            "Content-Length: " + fileBytes.length + " ",
                            "\r\n").getBytes());
                    outputStream.write(fileBytes);
                } catch (NullPointerException | FileNotFoundException e) {
                    outputStream.write(String.join("\r\n",
                            "HTTP/1.1 404 NOT_FOUND ",
                            "Content-Type: text/html;charset=utf-8",
                            "").getBytes());
                }
            }
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private URL getStaticResourceURL(String url) {
        return SYSTEM_CLASS_LOADER.getResource("static" + url);
    }
}
