package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.domain.User;
import com.techcourse.model.dto.UserRegistration;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http.cookie.HttpCookie;
import org.apache.coyote.http.cookie.HttpCookies;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.util.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final SessionManager sessionManager = SessionManager.getInstance();
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            performProcess(inputStream, outputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void performProcess(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        try {
            HttpRequest httpRequest = new HttpRequest(inputStream);

            final String method = httpRequest.getHttpMethod().name();
            final String requestURL = httpRequest.getPath();

            if (method.equals(HttpMethod.GET.name())) {
                doGet(httpRequest, requestURL, outputStream);
            }

            if (method.equals(HttpMethod.POST.name())) {
                doPost(httpRequest, requestURL, outputStream);
            }
        } catch (IOException | IllegalArgumentException | URISyntaxException e) {
            log.error(e.getMessage(), e);
            handleException(outputStream);
        }
    }

    private void handleException(OutputStream outputStream) {
        try {
            sendHttpResponse("/500.html", outputStream, HttpStatus.INTERNAL_SERVER_ERROR, "");
        } catch (IOException | URISyntaxException e) {
            log.error("Failed to send error response", e);
        }
    }

    private void doPost(final HttpRequest httpRequest,
                        final String requestURL, final OutputStream outputStream) throws IOException, URISyntaxException {

        Map<String, String> requestBody = httpRequest.getRequestBody().getValues();
        if (requestURL.equals("/login")) {
            login(httpRequest.getRequestHeader(), outputStream, requestBody);
        }

        if (requestURL.equals("/register")) {
            boolean isregistered = register(requestBody);
            if (isregistered) {
                redirect("", "/", outputStream);
                return;
            }
            sendHttpResponse("/register.html", outputStream, HttpStatus.BAD_REQUEST, "");
        }
    }

    private void login(final RequestHeader httpRequestHeaders, final OutputStream outputStream, final Map<String, String> requestBody) throws IOException {
        String cookies = httpRequestHeaders.getCookies();

        Optional<Session> httpSession = loginPost(requestBody);
        String[] result = loginGet(httpSession.isPresent());
        String httpStatus = result[0];
        HttpCookies httpCookies = HttpCookies.from(cookies);
        String cookieHeader = "";

        if (httpSession.isPresent() && (!httpCookies.hasJsessionId() || sessionManager.findSession(httpCookies.getJsessionId()) == null)) {
            HttpCookie cookie = new HttpCookie("JSESSIONID", httpSession.get().getId().toString());
            httpCookies.addCookie(cookie);
            cookieHeader = setCookieHeader(cookie.getValue());
        }

        if (httpStatus.equals("302 FOUND")) {
            redirect(cookieHeader, "/", outputStream);
            return;
        }

        redirect(cookieHeader, "/401", outputStream);
    }

    private String setCookieHeader(String uuid) {
        return "JSESSIONID=" + uuid + " ";
    }

    private boolean register(Map<String, String> requestBody) {
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        String email = requestBody.get("email");

        return validateRegistration(account, password, email);
    }

    private boolean validateRegistration(final String account, final String password, final String email) {
        try {
            InMemoryUserRepository.findByAccount(account)
                    .ifPresentOrElse(
                            user -> {
                                throw new IllegalArgumentException("이미 존재하는 계정명입니다.");
                            },
                            () -> {
                                UserRegistration userRegisterInfo = new UserRegistration(account, password, email);
                                User newUser = InMemoryUserRepository.save(userRegisterInfo);
                                log.info("user: {}의 회원가입이 완료되었습니다.", newUser.getAccount());
                            }
                    );
            return true;
        } catch (IllegalArgumentException e) {
            log.info("오류 발생: {}", e.getMessage());
            return false;
        }
    }

    private void doGet(final HttpRequest httpRequest, final String requestURL,
                       final OutputStream outputStream) throws IOException, URISyntaxException {
        String cookies = httpRequest.getRequestHeader().getHeaders().get("Cookie");
        String sessionId = "";
        HttpCookies httpCookies = HttpCookies.from(cookies);

        if (httpCookies.hasJsessionId()) {
            sessionId = httpCookies.getJsessionId();
        }

        if (sessionManager.findSession(sessionId) != null && requestURL.equals("/login")) {
            redirect("", "/", outputStream);
        }

        HttpStatus httpStatus = HttpStatus.OK;

        sendHttpResponse(requestURL, outputStream, httpStatus, "");
    }

    private Optional<Session> loginPost(Map<String, String> requestBody) {
        Map<String, String> userInfo = parseUserInfo(requestBody);
        String uuid = null;

        try {
            uuid = validateUser(userInfo);
        } catch (IllegalArgumentException e) {
            log.info("오류 발생: {}", e.getMessage());
        }

        if (uuid != null) {
            return Optional.ofNullable(SessionManager.getInstance().findSession(uuid));
        }

        return Optional.empty();
    }

    private String[] loginGet(boolean isLogined) {
        if (!isLogined) {
            return new String[]{"401 UNAUTHORIZED", "/401"};
        }
        return new String[]{"302 FOUND", "/index"};
    }

    private Map<String, String> parseUserInfo(Map<String, String> requestBody) {
        Map<String, String> userInfo = new HashMap<>();
        if (requestBody.get("account").isEmpty() || requestBody.get("password").isEmpty()) {
            throw new IllegalArgumentException("필수 입력값이 비어 있습니다.");
        }

        userInfo.put("account", requestBody.get("account"));
        userInfo.put("password", requestBody.get("password"));

        return userInfo;
    }

    private String validateUser(Map<String, String> userInfo) {
        String account = userInfo.get("account");
        String password = userInfo.get("password");

        Optional<User> loginUser = InMemoryUserRepository.findByAccount(account);

        if (loginUser.isEmpty()) {
            log.info(account + "는(은) 등록되지 않은 계정입니다.");
            return null;
        }

        User user = loginUser.get();

        String uuid = sessionManager.findSessionId(user);

        if (user.checkPassword(password)) {
            if (uuid == null) {
                uuid = createNewSession(user);
            }
            log.info("로그인 성공. user : {}", user);
            return uuid;
        }

        log.info(account + "의 비밀번호가 잘못 입력되었습니다.");
        return null;
    }

    private String createNewSession(final User user) {
        String uuid = UUID.randomUUID().toString();

        Session session = new Session(uuid);
        session.setAttribute("user", user);

        sessionManager.add(session);

        return uuid;
    }

    private void redirect(String cookieHeader, String path, OutputStream outputStream) throws IOException {
        try {
            byte[] values = FileReader.read(path);

            HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND, values);

            httpResponse.setMimeType(MimeType.HTML);
            httpResponse.setLocation(path);
            httpResponse.setCookie(cookieHeader);

            byte[] response = httpResponse.toByte();
            writeHttpResponse(response, outputStream);
        } catch (IOException | URISyntaxException exception) {
            log.info("redirection 실패: " + path);
        }
    }

    private void sendHttpResponse(final String path, final OutputStream outputStream, HttpStatus httpStatus,
                                  final String cookie) throws IOException, URISyntaxException {

        byte[] response = createHttpResponse(httpStatus, cookie, path);
        writeHttpResponse(response, outputStream);
    }

    private byte[] createHttpResponse(HttpStatus httpStatus, String cookieHeader, final String path) throws IOException, URISyntaxException {
        byte[] values;

        try {
            values = FileReader.read(path);

        } catch (IOException | URISyntaxException | NullPointerException exception) {
            values = FileReader.read("/404.html");
            httpStatus = HttpStatus.NOT_FOUND;
        }

        HttpResponse httpResponse = new HttpResponse(httpStatus, values);

        httpResponse.setMimeType(MimeType.from(path));
        httpResponse.setCookie(cookieHeader);

        return httpResponse.toByte();
    }

    private void writeHttpResponse(byte[] response, OutputStream outputStream) throws IOException {
        outputStream.write(response);
        outputStream.flush();
    }
}
