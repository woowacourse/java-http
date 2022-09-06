package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.constant.HttpMethods;
import org.apache.coyote.http11.constant.HttpStatus;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestAssembler;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseAssembler;
import org.apache.coyote.http11.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    private final Socket connection;
    private final ResponseAssembler responseAssembler;
    private final RequestAssembler requestAssembler;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        responseAssembler = new ResponseAssembler();
        requestAssembler = new RequestAssembler();
    }

    @Override
    public void run() {
        process(connection);
    }

    public HttpResponse get(HttpRequest request, OutputStream outputStream) throws IOException {
        if (request.isResource()) {
            return responseAssembler.resourceResponse(request.getUrl(), HttpStatus.OK);
        }
        if (request.getUrl().equals("/login")) {
            return generateLoginPage(request);
        }
        if (request.getUrl().equals("/register")) {
            return responseAssembler.resourceResponse("/register.html", HttpStatus.OK);
        }
        return generateDefaultResponse();
    }

    private HttpResponse generateLoginPage(HttpRequest request) {
        if (request.getCookies().hasCookie("JSESSIONID")) {
            return responseAssembler.redirectResponse("/index.html");
        }
        return responseAssembler.resourceResponse("/login.html", HttpStatus.OK);
    }

    public HttpResponse post(HttpRequest request, OutputStream outputStream) throws IOException {
        Map<String, String> bodyParams = request.getBody();

        if (request.getUrl().equals("/register")) {
            return register(bodyParams);
        }

        if (request.getUrl().equals("/login")) {
            return login(request.getCookies(), bodyParams);
        }

        return responseAssembler.redirectResponse("/404.html");
    }


    private HttpResponse login(Cookie cookies, Map<String, String> bodyParams) {
        String account = Objects.requireNonNull(bodyParams.get("account"), "계정이 입력되지 않았습니다.");
        String password = Objects.requireNonNull(bodyParams.get("password"), "비밀번호가 입력되지 않았습니다.");

        return checkUserInformation(cookies, account, password);
    }

    private HttpResponse register(Map<String, String> bodyParams) {
        String account = Objects.requireNonNull(bodyParams.get("account"), "계정이 입력되지 않았습니다.");
        String password = Objects.requireNonNull(bodyParams.get("password"), "비밀번호가 입력되지 않았습니다.");
        String email = Objects.requireNonNull(bodyParams.get("email"), "이메일이 입력되지 않았습니다.");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return responseAssembler.redirectResponse("/index.html");
    }

    private HttpResponse checkUserInformation(Cookie cookie, String account, String password) {
        Optional<User> userByAccount = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (userByAccount.isPresent()) {
            log.info("로그인 성공!" + " 아이디: " + userByAccount.get().getAccount());
            HttpResponse response = responseAssembler.redirectResponse("/index.html");
            processCookie(cookie, response, userByAccount.get());
            return response;
        }

        return responseAssembler.redirectResponse("/401.html");
    }

    private void processCookie(Cookie cookie, HttpResponse response, User user) {
        if (!cookie.hasCookie(SESSION_COOKIE_NAME)) {
            String jSessionId = UUID.randomUUID().toString();
            response.addCookie(SESSION_COOKIE_NAME, jSessionId);
            Session.put(jSessionId, user);
        }
    }

    private HttpResponse generateDefaultResponse() {
        final var responseBody = "Hello world!";
        return responseAssembler.rawStringResponse(responseBody);
    }

    @Override
    public void process(final Socket connection) {
        try (
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                OutputStream outputStream = connection.getOutputStream();
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            HttpRequest request = requestAssembler.makeRequest(bufferedReader);
            execute(outputStream, request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void execute(OutputStream outputStream, HttpRequest request) throws IOException {
        HttpResponse response = processByMethod(outputStream, request);

        outputStream.write(response.toMessage().getBytes());
        outputStream.flush();
    }

    private HttpResponse processByMethod(OutputStream outputStream, HttpRequest request) throws IOException {
        if (request.getMethod() == HttpMethods.GET) {
            return get(request, outputStream);
        }

        if (request.getMethod() == HttpMethods.POST) {
            return post(request, outputStream);
        }

        throw new IllegalArgumentException("지원하지 않는 Http Method입니다.");
    }
}
