package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ResourceFileLoader;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String request = br.readLine();
            StringTokenizer st = new StringTokenizer(request);

            String requestMethodType = st.nextToken();
            String requestPath = st.nextToken();
            String contentType = "";
            log.info("request: " + request);

            if (requestPath.equals("/")) {
                requestPath = "/index.html";
            }

            String extension = requestPath.split("\\.")[1];
            if(extension.equals("html")){
                contentType = "text/html";
            }
            if (extension.equals("css")){
                contentType = "text/css";
            }
            if (extension.equals("js")) {
                contentType = "text/javascript";
            }

            String responseBody = ResourceFileLoader.loadFileToString("static" + requestPath);
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
