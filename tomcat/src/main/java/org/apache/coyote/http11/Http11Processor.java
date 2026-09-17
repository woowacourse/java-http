package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String requestLine = reader.readLine();
            String[] tokens = requestLine.split(" ");

            String method = tokens[0];
            String path = tokens[1];           

            Map<String, String> headerMap = new HashMap<>();

            String header;
            while(!(header=reader.readLine()).isEmpty()){
                int index=header.indexOf(":");
                String name=header.substring(0, index);
                String value=header.substring(index+1).trim();
                headerMap.put(name, value);
            }
            
            if (method.equals("GET") && path.equals("/index.html")) {
                final var fileStream=getClass()
                        .getClassLoader()
                        .getResourceAsStream("static/index.html");
                final var fileBytes=fileStream.readAllBytes();
                final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + fileBytes.length + " ",
                    "",
                    new String(fileBytes));

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
            
            if (method.equals("GET") && path.equals("/css/styles.css")) {
                final var fileStream = getClass()
                        .getClassLoader()
                        .getResourceAsStream("static/css/styles.css");

                final var fileBytes = fileStream.readAllBytes();

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css;charset=utf-8 ",
                        "Content-Length: " + fileBytes.length + " ",
                        "",
                        new String(fileBytes));

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            final var responseBody = "Hello world!";

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
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
