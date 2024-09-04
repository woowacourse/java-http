package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.util.QueryStringParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            Http11Request request = Http11Request.from(inputStream);
            log.info("http request : {}", request);
            Http11Response response = new Http11Response();

            controll(request, response);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void controll(Http11Request request, Http11Response response) throws IOException {
        if (request.isStaticResourceRequest()) {
            getStaticResource(request, response);
            return;
        }
        String endpoint = request.getEndpoint();
        HttpMethod method = request.getMethod();
        if (method == HttpMethod.GET && "/login".equals(endpoint)) {
            viewLogin(response);
            return;
        }
        if (method == HttpMethod.POST && "/login".equals(endpoint)) {
            login(request, response);
            return;
        }
        if (method == HttpMethod.GET && "/register".equals(endpoint)) {
            viewRegist(response);
            return;
        }
        if (method == HttpMethod.POST && "/register".equals(endpoint)) {
            regist(request, response);
            return;
        }
        response.addBody("Hello world!");
    }

    private void getStaticResource(Http11Request request, Http11Response response) throws IOException {
        getView(request.getEndpoint(), response);

        String accept = request.getHeader("Accept")
                .orElse("text/html");
        response.addHeader("Accept", accept);
    }

    private void getView(String view, Http11Response response) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + view);
        if (resource == null) {
            throw new IllegalArgumentException("존재하지 않는 자원입니다.");
        }
        response.addBody(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
        response.addHeader("Accept", "text/html");
    }

    private void viewLogin(Http11Response response) throws IOException {
        getView("/login.html", response);
    }

    private void login(Http11Request request, Http11Response response) {
        String body = request.getBody();
        Map<String, List<String>> queryStrings = QueryStringParser.parseQueryString(body);
        String account = queryStrings.get("account").get(0);
        String password = queryStrings.get("password").get(0);

        try {
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));
            if (!user.checkPassword(password)) {
                throw new IllegalArgumentException("잘못된 비밀번호입니다.");
            }
            log.info("user {}", user);

            response.addCookie("JSESSIONID", UUID.randomUUID().toString());
            response.redirect("/index.html");
        } catch (IllegalArgumentException e) {
            response.redirect("/401.html");
        }
    }

    private void viewRegist(Http11Response response) throws IOException {
        getView("/register.html", response);
    }

    private void regist(Http11Request request, Http11Response response) {
        String body = request.getBody();
        Map<String, List<String>> queryStrings = QueryStringParser.parseQueryString(body);
        String account = queryStrings.get("account").get(0);
        String email = queryStrings.get("email").get(0);
        String password = queryStrings.get("password").get(0);

        User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
        response.redirect("/index.html");
    }
}
