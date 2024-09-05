package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;

import org.apache.catalina.io.FileReader;
import org.apache.catalina.request.RequestReader;
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
            Map<String, String> headerSentences = RequestReader.readHeaders(reader);
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
        if (sentences.get("IsQueryParam").equals("true")) {
            return getResponseBodyUsedQuery(sentences);
        }

        Optional<ResponsePage> responsePage = ResponsePage.fromUrl(url);
        if (responsePage.isPresent()) {
            ResponsePage page = responsePage.get();
            return new ResponseContent(page.getStatus(), accept, FileReader.loadFileContent(page.getFileName()));
        }

        return new ResponseContent(HttpStatus.OK, accept, FileReader.loadFileContent(url));
    }

    private ResponseContent getResponseBodyUsedQuery(Map<String, String> sentences) {
        String url = sentences.get("Url");
        if (url.equals("/login")) {
            return login(sentences);
        }
        throw new RuntimeException("'" + url + "'은 정의되지 않은 URL 입니다.");
    }


    private ResponseContent login(Map<String, String> queryString) {
        String accept = queryString.get("Accept");
        if (Integer.parseInt(queryString.get("QueryParamSize")) < 2) {
            return new ResponseContent(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent("/400.html"));
        }

        String accountParam = queryString.get("account");
        String passwordParam = queryString.get("password");
        if (accountParam == null || passwordParam == null) {
            return new ResponseContent(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent("/400.html"));
        }
        if (checkAuth(accountParam, passwordParam)) {
            return new ResponseContent(HttpStatus.FOUND, accept, FileReader.loadFileContent("/index.html"));
        }
        return new ResponseContent(HttpStatus.UNAUTHORIZED, accept, FileReader.loadFileContent("/401.html"));
    }

    private boolean checkAuth(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            String userToString = "user : " + user.get();
            log.info(userToString);
            return true;
        }
        return false;
    }

    private void loadPostHttpMethod(BufferedReader reader, Map<String, String> sentences) {
        int contentLength = Integer.parseInt(sentences.get("Content-Length"));
        Map<String, String> body = RequestReader.readBody(reader, contentLength);

        try (final OutputStream outputStream = connection.getOutputStream()) {
            String response = getResponseContentForUrl(sentences, body).responseToString();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static ResponseContent getResponseContentForUrl(Map<String, String> sentences, Map<String, String> body) {
        String url = sentences.get("Url");
        String accept = sentences.get("Accept");
        if (url.equals("/register")) {
            return signUp(body, accept);
        }
        return new ResponseContent(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent("/404.html"));
    }

    private static ResponseContent signUp(Map<String, String> body, String accept) {
        if (InMemoryUserRepository.findByAccount(body.get("account")).isPresent()) {
            return new ResponseContent(HttpStatus.BAD_REQUEST, accept, FileReader.loadFileContent("/400.html"));
        }
        InMemoryUserRepository.save(new User(body.get("account"), body.get("email"), body.get("password")));
        return new ResponseContent(HttpStatus.CREATED, accept, FileReader.loadFileContent("/index.html"));
    }
}
