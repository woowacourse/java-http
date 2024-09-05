package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.catalina.io.FileReader;
import org.apache.catalina.request.RequestHeaderReader;
import org.apache.catalina.response.HttpStatus;
import org.apache.catalina.response.ResponseContent;
import org.apache.catalina.response.ResponsePage;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_PAGE = "Hello world!";
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
            Map<String, String> headerSentences = RequestHeaderReader.readHeaders(reader);
            checkHttpMethodAndLoad(reader, headerSentences);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void checkHttpMethodAndLoad(BufferedReader reader, Map<String, String> sentences) {
        String httpMethod = sentences.get("HttpMethod");
        if (httpMethod.equals("GET")) {
            loadGetHttpMethod(sentences);
        }
        if (httpMethod.equals("POST")) {
            loadPostHttpMethod(reader, sentences);
        }
    }

    private void loadGetHttpMethod(Map<String, String> sentences) {
        try (final OutputStream outputStream = connection.getOutputStream()) {
            String response = getResponseContentForUrl(sentences).responseToString();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private ResponseContent getResponseContentForUrl(Map<String, String> sentences) {
        String url = sentences.get("Url");
        String accept = sentences.get("Accept");
        if (url.equals("/")) {
            return new ResponseContent(HttpStatus.OK, accept, DEFAULT_PAGE);
        }
        if (url.contains("?")) {
            return getResponseBodyUsedQuery(url, accept);
        }

        Optional<ResponsePage> responsePage = ResponsePage.fromUrl(url);
        if (responsePage.isPresent()) {
            ResponsePage page = responsePage.get();
            return new ResponseContent(page.getStatus(), accept, FileReader.loadFileContent(page.getFileName()));
        }

        return new ResponseContent(HttpStatus.OK, accept, FileReader.loadFileContent(url));
    }

    private ResponseContent getResponseBodyUsedQuery(String url, String accept) {
        String[] separationUrl = url.split(QUERY_SEPARATOR);
        String path = separationUrl[0];
        String[] queryString = separationUrl[1].split(PARAM_SEPARATOR);
        if (!isValidateQuery(queryString)) {
            return new ResponseContent(HttpStatus.BAD_REQUEST, accept,
                    FileReader.loadFileContent("/400.html"));
        }
        if (path.startsWith("/login")) {
            return login(queryString, accept);
        }
        throw new RuntimeException("'" + url + "'은 정의되지 않은 URL 입니다.");
    }

    private static boolean isValidateQuery(String[] queryString) {
        return Arrays.stream(queryString)
                .noneMatch(query -> query.split(PARAM_ASSIGNMENT).length < 2);
    }

    private ResponseContent login(String[] queryString, String accept) {
        if (queryString.length < 2) {
            return new ResponseContent(HttpStatus.BAD_REQUEST, accept,
                    FileReader.loadFileContent("/400.html"));
        }
        String accountParam = queryString[0];
        String passwordParam = queryString[1];
        if (!accountParam.startsWith("account=") || !passwordParam.startsWith("password=")) {
            return new ResponseContent(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent("/400.html"));
        }

        if (checkAuth(accountParam.split(PARAM_ASSIGNMENT)[1], passwordParam.split(PARAM_ASSIGNMENT)[1])) {
            return new ResponseContent(HttpStatus.FOUND, accept, FileReader.loadFileContent("/index.html"));
        }
        return new ResponseContent(HttpStatus.UNAUTHORIZED, accept, FileReader.loadFileContent("/401.html"));
    }

    private boolean checkAuth(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("user : " + user.get());
            return true;
        }
        return false;
    }

    private void loadPostHttpMethod(BufferedReader reader, Map<String, String> sentences) {
        String url = sentences.get("Url");
        String fileType = sentences.get("Accept");
        int contentLength = Integer.parseInt(sentences.get("Content-Length"));

        Map<String, String> body = getRequestBody(reader, contentLength);

        try (final OutputStream outputStream = connection.getOutputStream()) {
            String response = "";
            if (url.equals("/register")) {
                response = signUp(body, fileType).responseToString();
            }
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> getRequestBody(BufferedReader reader, int contentLength) {
        char[] buffer = new char[contentLength];
        try {
            reader.read(buffer, 0, contentLength);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String body = new String(buffer);

        String[] values = body.split("&");
        return Arrays.stream(values)
                .map(s -> s.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> parts[0],
                        parts -> parts[1]
                ));
    }

    private ResponseContent signUp(Map<String, String> body, String accept) {
        if (InMemoryUserRepository.findByAccount(body.get("account")).isPresent()) {
            return new ResponseContent(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent("/400.html"));
        }
        InMemoryUserRepository.save(new User(body.get("account"), body.get("email"), body.get("password")));
        return new ResponseContent(HttpStatus.CREATED, accept, FileReader.loadFileContent("/index.html"));
    }
}
