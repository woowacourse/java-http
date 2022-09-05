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

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.RequestAssembler;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.ResponseAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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

    public void get(Http11Request request, OutputStream outputStream) throws IOException {
        Http11Response response = getMapping(request);

        outputStream.write(response.toMessage().getBytes());
        outputStream.flush();
    }

    public void post(Http11Request request, OutputStream outputStream) throws IOException {
        Http11Response response = postMapping(request);

        outputStream.write(response.toMessage().getBytes());
        outputStream.flush();
    }

    private Http11Response postMapping(Http11Request request) {
        Map<String, String> bodyParams = request.getBody();

        if (request.getUrl().equals("/register")) {
            return register(bodyParams);
        }

        if (request.getUrl().equals("/login")) {
            return login(bodyParams);
        }

        return responseAssembler.redirectResponse("/404.html");
    }

    private Http11Response login(Map<String, String> bodyParams) {
        String account = Objects.requireNonNull(bodyParams.get("account"), "계정이 입력되지 않았습니다.");
        String password = Objects.requireNonNull(bodyParams.get("password"), "비밀번호가 입력되지 않았습니다.");

        return checkUserInformation(account, password);
    }

    private Http11Response register(Map<String, String> bodyParams) {
        String account = Objects.requireNonNull(bodyParams.get("account"), "계정이 입력되지 않았습니다.");
        String password = Objects.requireNonNull(bodyParams.get("password"), "비밀번호가 입력되지 않았습니다.");
        String email = Objects.requireNonNull(bodyParams.get("email"), "이메일이 입력되지 않았습니다.");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return responseAssembler.redirectResponse("/index.html");
    }

    private Http11Response getMapping(Http11Request request) {
        if (request.isResource()) {
            return responseAssembler.resourceResponse(request.getUrl(), HttpStatus.OK);
        }
        if (request.getUrl().equals("/login")) {
            return responseAssembler.resourceResponse("/login.html", HttpStatus.OK);
        }
        if (request.getUrl().equals("/register")) {
            return responseAssembler.resourceResponse("/register.html", HttpStatus.OK);
        }
        return generateDefaultResponse();
    }

    private Http11Response checkUserInformation(String account, String password) {
        Optional<User> userByAccount = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (userByAccount.isPresent()) {
            log.info("로그인 성공!" + " 아이디: " + userByAccount.get().getAccount());
            return responseAssembler.redirectResponse("/index.html");
        }

        return responseAssembler.redirectResponse("/401.html");
    }

    private Http11Response generateDefaultResponse() {
        final var responseBody = "Hello world!";
        return responseAssembler.rawStringResponse(responseBody);
    }

    @Override
    public void process(final Socket connection) {
        try (
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                OutputStream outputStream = connection.getOutputStream();
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        ) {
            Http11Request request = requestAssembler.makeRequest(bufferedReader);
            executeMethod(outputStream, request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeMethod(OutputStream outputStream, Http11Request request) throws IOException {
        if (request.getMethod() == HttpMethods.GET) {
            get(request, outputStream);
        }

        if (request.getMethod() == HttpMethods.POST) {
            post(request, outputStream);
        }
    }
}
