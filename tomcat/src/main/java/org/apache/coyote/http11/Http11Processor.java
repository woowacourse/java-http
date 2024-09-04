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
import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.Processor;
import org.apache.coyote.ResponseContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_PAGE = "Hello world!";
    private static final String HTML_PREFIX = "static/";
    private static final String CONTENT_TYPE_HTML = "text/html";

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
            List<String> headerSentences = new ArrayList<>();
            String sentence = reader.readLine();
            while(sentence != null && !sentence.isBlank()) {
                headerSentences.add(sentence);
                sentence = reader.readLine();
            }
            if (headerSentences.isEmpty()) {
                throw new IllegalArgumentException("요청 header가 존재하지 않습니다.");
            }
            checkHttpMethodAndLoad(headerSentences);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void checkHttpMethodAndLoad(List<String> sentences) {
        String firstHeaderSentence = sentences.getFirst();
        if (firstHeaderSentence.startsWith("GET")) {
            loadGetHttpMethod(sentences);
        }
    }

    private void loadGetHttpMethod(List<String> sentences) {
        try (final OutputStream outputStream = connection.getOutputStream()) {
            ResponseContent responseContent = getHtmlResponseContent(sentences.getFirst());

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: "+ responseContent.getContentType() +";charset=utf-8 ",
                    "Content-Length: " + responseContent.getContentLength() + " ",
                    "",
                    responseContent.getBody());

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private ResponseContent getHtmlResponseContent(String firstSentence) {
        String[] s = firstSentence.split(" ");
        if (s[1].equals("/")) {
            return new ResponseContent(CONTENT_TYPE_HTML, DEFAULT_PAGE);
        }
        return new ResponseContent(CONTENT_TYPE_HTML, getResponseBodyByFileName(s[1]));
    }

    private String getResponseBodyByFileName(String fileName) {
        String changedFileName = fileName.replace("/", HTML_PREFIX);
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
