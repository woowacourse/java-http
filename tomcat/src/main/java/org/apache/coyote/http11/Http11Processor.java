package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
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
        log.atInfo().log("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             final var bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest request = new HttpRequest(bufferedReader);
            final HttpResponse response = HttpResponse.empty();

            final Controller controller = RequestMapping.getInstance().getController(request);
            controller.service(request, response);

            final StringBuilder headerBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : response.getHeaders().entrySet()){
                headerBuilder.append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append(" \r\n");
            }

            outputStream.write((response.getStatusLine().toString() + " \r\n").getBytes(StandardCharsets.UTF_8));
            outputStream.write(headerBuilder.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
            outputStream.write(response.getBody());
            outputStream.flush();

        } catch (IOException e) {
            log.atDebug().log("Connection closed by client: {}", e.getMessage());
        } catch (Exception e) {
            log.atError().log(e.getMessage(), e);
        }
    }
}
