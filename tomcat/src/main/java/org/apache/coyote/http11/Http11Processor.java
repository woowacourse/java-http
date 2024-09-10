package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.ErrorController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.FileTypeChecker;
import org.apache.coyote.util.IdGenerator;
import org.apache.coyote.util.RequestBodyParser;
import org.apache.coyote.util.SessionManager;
import org.apache.coyote.util.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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

            HttpRequest request = HttpRequest.from(inputStream);
            log.info("http request : {}", request);
            HttpResponse response = HttpResponse.from(request);

            try {
                if (request.getMethod().isGet()) {
                    resolveGetHttpMethod(request, response);
                }
                if (request.getMethod().isPost()) {
                    resolvePostHttpMethod(request, response);
                }
            } catch (Exception exception) {
                ErrorController errorController = new ErrorController();
                errorController.handle(response, exception);
            }
            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException exception) {
            log.error("요청 처리 과정 중 에러 발생 : {}", exception.getMessage());
        }
    }

    private void resolveGetHttpMethod(HttpRequest request, HttpResponse response) {
        if (request.getTargetPath().equals("/")) {
            ViewResolver.resolveView("index.html", response);
        }
        if (request.getTargetPath().equals("/login")) {
            resolveAuthUser(request, response);
        }
        if (request.getTargetPath().equals("/register")) {
            ViewResolver.resolveView("register.html", response);
        }
        resolveStaticResource(request, response);
    }

    private void resolveAuthUser(HttpRequest request, HttpResponse response) {
        if (request.hasSession()) {
            String sessionId = request.getSessionId();
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.findSession(sessionId);
            User user = session.getUser();
            redirectHomeAuthUser(response, user);
            return;
        }
        ViewResolver.resolveView("login.html", response);
    }

    private void redirectHomeAuthUser(HttpResponse response, User user) {
        if (user != null) {
            response.sendRedirect("/index.html");
        }
    }

    private void resolveStaticResource(HttpRequest request, HttpResponse response) {
        String targetPath = request.getTargetPath();
        if (FileTypeChecker.isSupported(targetPath)) {
            ViewResolver.resolveView(targetPath, response);
        }
    }

    private void resolvePostHttpMethod(HttpRequest request, HttpResponse response) {
        Map<String, String> formData = RequestBodyParser.parseFormData(request.getBody());
        if (request.getTargetPath().equals("/login")) {
            resolvePostLogin(request, response, formData);
        }
        if (request.getTargetPath().equals("/register")) {
            User user = new User(formData.get("account"), formData.get("email"), formData.get("password"));
            InMemoryUserRepository.save(user);
            log.info("{} - 회원 가입 성공", user);
            response.sendRedirect("/index.html");
        }
    }

    private void resolvePostLogin(HttpRequest request, HttpResponse response, Map<String, String> formData) {
        String account = formData.get("account");
        String password = formData.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseGet(() -> {
                    log.debug("{} - 존재하지 않는 회원의 로그인 요청", account);
                    response.updateHttpStatus(HttpStatus.UNAUTHORIZED);
                    throw new IllegalArgumentException("존재하지 않는 회원 입니다.");
                });
        validatePassword(request, response, user, password);
        log.info("{} - 회원 로그인 성공", user);
        resolveNotAuthUser(request, response, user);
        response.sendRedirect("/index.html");
    }

    private void resolveNotAuthUser(HttpRequest request, HttpResponse response, User user) {
        if (!request.hasSession()) {
            String sessionId = IdGenerator.generateUUID();
            response.addCookie("JSESSIONID", sessionId);
            Session session = new Session(sessionId);
            session.setUser(user);
            SessionManager sessionManager = SessionManager.getInstance();
            sessionManager.add(session);
        }
    }


    private void validatePassword(HttpRequest request, HttpResponse response, User user, String password) {
        if (!user.checkPassword(password)) {
            log.debug("회원과 일치하지 않는 비밀번호 - 회원 정보 : {}, 입력한 비밀번호 {}", user, password);
            response.updateHttpStatus(HttpStatus.UNAUTHORIZED);
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
    }
}
