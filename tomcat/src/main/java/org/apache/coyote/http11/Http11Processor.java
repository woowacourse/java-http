package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.Socket;

// Connector가 전달해 준 클라이언트 소켓을 받아 실제 HTTP 요청 데이터를 읽고 HTTP 응답 메시지를 만들어 돌려줌
// 브라우저가 index.html을 받아온 뒤 HTML을 위에서부터 파싱하다가
// <link rel="stylesheet" href="...">, <script src="...">, <img> 같은 태그를 발견하면,
// 해당 자원을 가져오기 위해 서버로 추가적인 HTTP GET 요청을 자동으로 다시 보낸다.
public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    // Connector에서 new Thread(processor).start()가 실행되면 호출된다.
    @Override
    public void run() {
        // 클라이언트의 IP/포트 정보
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            // 1. 요청 파싱 및 응답 객체 생성
            final HttpRequest request = new HttpRequest(inputStream);
            final HttpResponse response = new HttpResponse(outputStream);

            // 2. 쿠키 및 세션 ID 처리
            String jsessionId = request.getHttpCookies().getCookie("JSESSIONID");
            if (jsessionId == null) {
                jsessionId = UUID.randomUUID().toString();
                response.addCookie("JSESSIONID", jsessionId);
            }

            final String method = request.getMethod();
            final String path = request.getPath();

            // POST 로그인 처리
            if ("POST".equals(method) && "/login".equals(path)) {
                String account = request.getParameters().get("account");
                String password = request.getParameters().get("password");

                final Optional<User> user = InMemoryUserRepository.findByAccount(account);

                // 회원 정보가 존재하고 비밀번호가 일치하는 경우
                if (user.isPresent() && user.get().checkPassword(password)) {
                    // 기존 세션을 찾거나 없으면 신규 세션 생성 후 저장 (조회+생성을 원자적으로 수행)
                    Session session = SessionManager.getOrCreate(jsessionId);
                    if (session == null) {
                        session = new Session(jsessionId);
                        SessionManager.add(session);
                    }
                    session.setAttribute("user", user.get());
                    response.sendRedirect("/index.html");
                } else {
                    response.sendRedirect("/401.html");
                }
                response.flush();
                return;
            }

            // POST 회원가입 처리
            if ("POST".equals(method) && "/register".equals(path)) {
                String account = request.getParameters().get("account");
                String password = request.getParameters().get("password");
                String email = request.getParameters().get("email");

                User user = new User(account, password, email);
                InMemoryUserRepository.save(user);

                response.sendRedirect("/index.html");
                response.flush();
                return;
            }

            // GET 로그인에 접근할 때, 세션에 user가 들어있는지 검사하여 이미 로그인했다면 index.html로 리다이렉트
            if ("GET".equals(method) && ("/login".equals(path) || "/login.html".equals(path))) {
                final Session session = SessionManager.findSession(jsessionId);
                if (session != null && session.getAttribute("user") != null) {
                    response.sendRedirect("/index.html");
                    response.flush();
                    return;
                }
            }

            // GET 정적 파일 응답 (200 OK)
            String targetPath = path;
            if ("/".equals(targetPath)) {
                targetPath = "/index.html";
            } else if ("/login".equals(targetPath)) {
                targetPath = "/login.html";
            } else if("/register".equals(targetPath)) {
                targetPath = "/register.html";
            }
            byte[] body;
            final String contentType;
            final var resourceUrl = getClass().getClassLoader().getResource("static" + targetPath);

            if (resourceUrl != null && !Files.isDirectory(Path.of(resourceUrl.toURI()))) {
                body = Files.readAllBytes(Path.of(resourceUrl.toURI()));
                if (targetPath.endsWith(".css")) {
                    contentType = "text/css;charset=utf-8";
                } else if (targetPath.endsWith(".js")) {
                    contentType = "application/javascript;charset=utf-8";
                } else {
                    contentType = "text/html;charset=utf-8";
                }
            } else {
                body = "Hello world!".getBytes(StandardCharsets.UTF_8);
                contentType = "text/html;charset=utf-8";
            }
            response.writeBody(body, contentType);
            response.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
