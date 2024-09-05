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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.catalina.response.HttpStatus;
import org.apache.catalina.response.ResponseContent;
import org.apache.coyote.Processor;
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
    public static final String ACCEPT_PREFIX = "Accept: ";
    public static final String QUERY_SEPARATOR = "\\?";
    public static final String PARAM_SEPARATOR = "&";
    public static final String PARAM_ASSIGNMENT = "=";

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
            String response = checkFileType(sentences).responseToString();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private ResponseContent checkFileType(List<String> sentences) {
        String accept = sentences.stream()
                .filter(sentence -> sentence.startsWith(ACCEPT_PREFIX))
                .map(sentence -> sentence.substring(ACCEPT_PREFIX.length(), sentence.length() - 1).split(",")[0])
                .findAny()
                .orElse(CONTENT_TYPE_HTML);

        String url = sentences.getFirst().split(" ")[1];
        return getHtmlResponseContent(url, accept);
    }

    private ResponseContent getHtmlResponseContent(String url, String accept) {
        if (url.equals("/")) {
            return new ResponseContent(HttpStatus.OK, accept, DEFAULT_PAGE);
        }
        if (url.contains("?")) {
            return getResponseBodyUsedQuery(url, accept);
        }
        return new ResponseContent(HttpStatus.OK, accept, getResponseBodyByFileName(url));
    }

    private ResponseContent getResponseBodyUsedQuery(String url, String accept) {
        String[] separationUrl = url.split(QUERY_SEPARATOR);
        String path = separationUrl[0];
        String[] queryString = separationUrl[1].split(PARAM_SEPARATOR);
        boolean validateQuery = Arrays.stream(queryString)
                .anyMatch(query -> query.split(PARAM_ASSIGNMENT).length != 2);
        if (validateQuery) {
            return new ResponseContent(HttpStatus.BAD_REQUEST, accept, getResponseBodyByFileName("/400.html"));
        }

        if (path.startsWith("/login")) {
            return login(queryString, accept);
        }
        throw new RuntimeException("'" + url + "'은 정의되지 않은 URL 입니다.");
    }

    private ResponseContent login(String[] queryString, String accept) {
        if (queryString.length < 2) {
            return new ResponseContent(HttpStatus.BAD_REQUEST, accept, getResponseBodyByFileName("/400.html"));
        }
        String accountParam = queryString[0];
        String passwordParam = queryString[1];
        if (!accountParam.startsWith("account=") || !passwordParam.startsWith("password=")) {
            return new ResponseContent(HttpStatus.BAD_REQUEST, accept, getResponseBodyByFileName("/400.html"));
        }

        if (checkAuth(accountParam.split(PARAM_ASSIGNMENT)[1], passwordParam.split(PARAM_ASSIGNMENT)[1])) {
            return new ResponseContent(HttpStatus.FOUND, accept, getResponseBodyByFileName("/index.html"));
        }
        return new ResponseContent(HttpStatus.UNAUTHORIZED, accept, getResponseBodyByFileName("/401.html"));
    }


    private boolean checkAuth(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("user : " + user.get());
            return true;
        }
        return false;
    }

    private String getResponseBodyByFileName(String fileName) {
        URL resource = getClass().getClassLoader().getResource(RESOURCE_PATH_PREFIX + fileName);
        if (resource == null) {
            throw new RuntimeException("'" + fileName + "'이란 페이지를 찾을 수 없습니다.");
        }
        Path path = new File(resource.getPath()).toPath();

        try {
            return Files.readString(path);
        } catch (IOException | UncheckedServletException e) {
            throw new RuntimeException("'" + fileName + "파일을 문자열로 변환하는데 오류가 발생했습니다.");
        }
    }
}
