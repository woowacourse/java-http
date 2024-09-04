package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String DEFAULT_PAGE = "Hello world!";

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
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String firstHeaderSentence = reader.readLine();
            if (firstHeaderSentence == null) {
                throw new IllegalArgumentException("요청 header가 존재하지 않습니다.");
            }
            checkHttpMethodAndLoad(firstHeaderSentence);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void checkHttpMethodAndLoad(String firstHeaderSentence) {
        if (firstHeaderSentence.startsWith("GET")) {
            loadGetHttpMethod(firstHeaderSentence);
        }
    }

    private void loadGetHttpMethod(String firstHeaderSentence) {
        try (final OutputStream outputStream = connection.getOutputStream()) {
            String responseBody = findResponseBody(firstHeaderSentence);

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

    private String findResponseBody(String firstSentence) {
        String[] s = firstSentence.split(" ");
        if (s[1].equals("/")) {
            return DEFAULT_PAGE;
        }
        return getResponseBodyByFileName(s[1]);
    }

    private String getResponseBodyByFileName(String fileName) {
        String changedFileName = fileName.replace("/", "static/");
        URL resource = getClass().getClassLoader().getResource(changedFileName);
        if (resource == null) {
            throw new RuntimeException("페이지를 찾을 수 없습니다.");
        }
        Path path = new File(resource.getPath()).toPath();

        try {
            return Files.readString(path);
        } catch (IOException | UncheckedServletException e) {
            throw new RuntimeException("파일을 String으로 변환하는데 오류가 발생했습니다.");
        }
    }
}
