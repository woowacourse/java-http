package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Queries;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Set<String> endpoints = Set.of(
            "login",
            "register",
            "index"
    );

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                lines.add(line);
            }
            log.info("HTTP Request: " + lines);
            HttpRequest request = HttpRequest.of(lines);
            HttpResponse response = getResponse(request);
            String formattedResponse = response.toResponse();

            outputStream.write(formattedResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getResponse(HttpRequest request) throws IOException {
        String requestPath = request.getPath();

        if (requestPath.equals("/login")) {
            return login(request);
        }

        try {
            URL resource = getClass().getClassLoader().getResource("static/" + requestPath);
            if (resource == null) {
                throw new IllegalArgumentException("존재하지 않는 자원입니다.");
            }
            ResponseFile responseFile = ResponseFile.of(resource);
            return HttpResponse.createFileResponse(responseFile);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HttpResponse.createRedirectResponse(
                    HttpStatus.FOUND,
                    "/404.html"
            );
        }
    }

    private HttpResponse login(HttpRequest request) throws IOException {
        if (request.isQueriesEmpty()) {
            URL resource = getClass().getClassLoader().getResource("static/login.html");
            ResponseFile responseFile = ResponseFile.of(resource);

            return HttpResponse.createFileResponse(responseFile);
        }
        Queries queries = request.getQueries();
        String account = queries.get("account");
        String password = queries.get("password");
        if (isValidUser(account, password)) {
            return HttpResponse.createRedirectResponse(
                    HttpStatus.FOUND,
                    "/index.html"
            );
        }
        return HttpResponse.createRedirectResponse(
                HttpStatus.FOUND,
                "/401.html"
        );
    }

    private boolean isValidUser(String account, String password) {
        if (account == null || password == null) {
            return false;
        }
        return InMemoryUserRepository.findByAccount(account)
                .map(user -> authenticateUser(user, password))
                .orElse(false);
    }

    private boolean authenticateUser(User user, String password) {
        if (user.checkPassword(password)) {
            log.info("User authenticated: " + user);
            return true;
        } else {
            log.warn("Authentication failed for user: " + user.getAccount());
            return false;
        }
    }
}
