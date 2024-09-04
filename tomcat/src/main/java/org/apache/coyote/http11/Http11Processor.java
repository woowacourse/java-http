package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.StringTokenizer;

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
            String line = reader.readLine();

            // TODO : 한 줄이 아닌 전체로 읽도록 수정
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);  // 라인 단위로 출력
//            }

            if (reader.readLine() == null) {
                throw new IOException("http request is empty");
            }

            StringTokenizer tokenizer = new StringTokenizer(line);
            String method = tokenizer.nextToken();
            String pattern = tokenizer.nextToken();
            String httpVersion = tokenizer.nextToken();

            if (!httpVersion.equals("HTTP/1.1")) {
                throw new IOException("not http1.1 request");
            }

            var responseBody = "Hello world!";

            if (method.equals("GET") && !pattern.equals("/")) {
                final URL resource = getClass().getClassLoader().getResource("static" + pattern);
                responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()), StandardCharsets.UTF_8);
            }

            String fileContent = responseBody.replaceAll("\r\n", "\n");

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + fileContent.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);



            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
