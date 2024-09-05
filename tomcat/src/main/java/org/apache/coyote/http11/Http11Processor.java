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
import java.util.Optional;

import org.apache.coyote.Processor;
import org.apache.coyote.ResponseContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_PAGE = "Hello world!";
    private static final String RESOURCE_PATH_PREFIX = "static";
    private static final String CONTENT_TYPE_HTML = "text/html";
    public static final String ACCEPT = "Accept:";

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
            List<String> headerSentences = readRequestHeader(reader);
            checkHttpMethodAndLoad(headerSentences);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> readRequestHeader(BufferedReader reader) {
        List<String> headerSentences = new ArrayList<>();
        try {
            String sentence = reader.readLine();
            while (sentence != null && !sentence.isBlank()) {
                headerSentences.add(sentence);
                sentence = reader.readLine();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        if (headerSentences.isEmpty()) {
            throw new IllegalArgumentException("요청 header가 존재하지 않습니다.");
        }

        return headerSentences;
    }

    private void checkHttpMethodAndLoad(List<String> sentences) {
        String firstHeaderSentence = sentences.getFirst();
        if (firstHeaderSentence.startsWith("GET")) {
            loadGetHttpMethod(sentences);
        }
    }

    private void loadGetHttpMethod(List<String> sentences) {
        try (final OutputStream outputStream = connection.getOutputStream()) {
            ResponseContent responseContent = checkFileType(sentences);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + responseContent.getContentType() + ";charset=utf-8 ",
                    "Content-Length: " + responseContent.getContentLength() + " ",
                    "",
                    responseContent.getBody());

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private ResponseContent checkFileType(List<String> sentences) {
        String accept = sentences.stream()
                .filter(sentence -> sentence.startsWith(ACCEPT))
                .findAny()
                .orElse(ACCEPT + " " + CONTENT_TYPE_HTML)
                .split(" ")[1]
                .split(",")[0];

        String headerFirstLine = sentences.getFirst().split(" ")[1];
        return new ResponseContent(accept, getHtmlResponseContent(headerFirstLine));
    }

    private String getHtmlResponseContent(String url) {
        if (url.equals("/")) {
            return DEFAULT_PAGE;
        }
        if (url.contains("?")) {
            return getResponseBodyUsedQuery(url);
        }
        return getResponseBodyByFileName(url);
    }

    private String getResponseBodyUsedQuery(String url) {
        String path = url.split("\\?")[0];
        String[] queryString = url.split("\\?")[1].split("&");
        if (path.startsWith("/login")) {
            if (queryString[0].startsWith("account=") && queryString[1].startsWith("password=")) {
                checkAuth(queryString[0].split("=")[1], queryString[1].split("=")[1]);
            }
            return getResponseBodyByFileName("/login.html");
        }
        throw new RuntimeException("정의되지 않은 URL 입니다.");
    }

    private void checkAuth(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("user : " + user.get());
        }
    }

    private String getResponseBodyByFileName(String fileName) {
        URL resource = getClass().getClassLoader().getResource(RESOURCE_PATH_PREFIX + fileName);
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
