package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.HttpRequestMessage;
import org.apache.HttpResponseMessage;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            HttpRequestMessage httpRequestMessage = new HttpRequestMessage(inputStream);

//            if (httpRequestMessage.isStaticResourceRequest()) {
//                String body = Files.readString(
//                        Paths.get("./tomcat/src/main/resources/static" + httpRequestMessage.getUri())
//                );
//                Map<String, String> headerMap = new HashMap<>();
//                headerMap.put("Content-Type", "text/" + httpRequestMessage.getStaticResourceType() + ";charset=utf-8");
//                headerMap.put("Content-Length", String.valueOf(body.getBytes().length));
//                HttpResponseMessage httpResponseMessage = new HttpResponseMessage(
//                        "HTTP/1.1", "200 OK", headerMap, body
//                );
//                outputStream.write(httpResponseMessage.generateMessage().getBytes());
//                outputStream.flush();
//
//                log.info("1");
//            } else if (httpRequestMessage.getUri().equals("/")) {
//                String body = "Hello world!";
//                Map<String, String> headerMap = new HashMap<>();
//                headerMap.put("Content-Type", "text");
//                headerMap.put("Content-Length", String.valueOf(body.getBytes().length));
//                HttpResponseMessage httpResponseMessage = new HttpResponseMessage(
//                        "HTTP/1.1", "200 OK", headerMap, body
//                );
//                outputStream.write(httpResponseMessage.generateMessage().getBytes());
//                outputStream.flush();
//
//                log.info("2");
//            }

            String body = "Hello world!";
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "text/html;charset=utf-8");
            headerMap.put("Content-Length", String.valueOf(body.getBytes().length));
            HttpResponseMessage httpResponseMessage = new HttpResponseMessage(
                    "HTTP/1.1", "200 OK", headerMap, body
            );

            System.out.println("###");
            System.out.println(httpResponseMessage.generateMessage());

            outputStream.write(httpResponseMessage.generateMessage().getBytes());
            outputStream.flush();

            log.info("3");
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
