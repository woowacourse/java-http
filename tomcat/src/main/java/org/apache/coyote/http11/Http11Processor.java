package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Paths;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_PATH =
            Paths.get("").toAbsolutePath() + "/tomcat/src/main/resources/static";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info(
                "connect host: {}, port: {}",
                connection.getInetAddress(),
                connection.getPort()
        );
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            String[] content = readFromInputStream(inputStream).split(" ");
            String responseBody = "Hello world!";

            if (!content[1].equals("/")) {
                responseBody = readForFilePath(DEFAULT_PATH + content[1]);
            }

            String contentType = "text/" + extractExtension(content[1]);

            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            log.info("response info {}", response);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readFromInputStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder content = new StringBuilder();
        String line;

        do {
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            content.append(line).append("\n");
        } while (!"".equals(line));

        return content.toString();
    }

    private String readForFilePath(String path) {
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            return readFileFromInputStream(fileInputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    private String readFileFromInputStream(FileInputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder fileContent = new StringBuilder();
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            fileContent.append(line).append("\n");
        }
        return fileContent.toString();
    }

    private String extractExtension(String path) {
        int lastIndexOfComma = path.lastIndexOf(".");

        if (lastIndexOfComma == -1) {
            return "html";
        }

        return path.substring(lastIndexOfComma + 1);
    }

}
