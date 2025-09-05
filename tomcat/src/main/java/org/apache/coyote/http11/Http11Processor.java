package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("GET /index.html HTTP/1.1")) {
                        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("static/index.html");
                        if (resourceAsStream == null) {
                            return ;
                        }

                        BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
                        StringBuilder st = new StringBuilder();
                        String line2;
                        while ((line2 = reader.readLine()) != null) {
                            st.append(line2).append("\r\n");
                        }

                        String responseBody = st.toString();
                        String response = String.join("\r\n",
                                "HTTP/1.1 200 OK ",
                                "Content-Type: text/html;charset=utf-8 ",
                                "Content-Length: " + responseBody.getBytes().length + " ",
                                "Accept: */*",
                                responseBody);
                        outputStream.write(response.getBytes());
                        outputStream.flush();

                        return ;
                    }

                    else {
                        final var responseBody = "Hello world!";

                        final var response = String.join("\r\n",
                                "HTTP/1.1 200 OK ",
                                "Content-Type: text/html;charset=utf-8 ",
                                "Content-Length: " + responseBody.getBytes().length + " ",
                                "",
                                responseBody);

                        outputStream.write(response.getBytes());
                        outputStream.flush();
                    }
                }
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
