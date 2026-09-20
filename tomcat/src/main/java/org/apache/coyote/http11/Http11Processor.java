package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

            // 1. InputStream을 문자열 단위로 읽기 위해 BufferedReader로 감싼다.
            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            // 2. Request Line 파싱(예: GET /index.html HTTP/1.1)
            final String requestLine = reader.readLine();
            log.info("request line 첫 줄: {}", requestLine);
            if (requestLine == null) {
                log.warn("클라이언트로부터 전달된 RequestLine이 존재하지 않습니다.");
                return;
            }

            final String[] requestLineParts = requestLine.split(" ");
            final String method = requestLineParts[0]; // GET, POST
            final String uri = requestLineParts[1];    // /login?account=gugu 등

            // path와 queryString 분리
            final String path;
            final String queryString;
            if (uri.contains("?")) {
                int index = uri.indexOf("?");
                path = uri.substring(0, index);
                queryString = uri.substring(index + 1);
            } else {
                path = uri;
                queryString = "";
            }

            // 2. Header 파싱 (Content-Length)
            String line;
            String rawCookie = "";
            int contentLength = 0;
            while ((line = reader.readLine()) != null && !"".equals(line)) {
                if (line.startsWith("Content-Length: ")) {
                    contentLength = Integer.parseInt(line.split(": ")[1]);
                }
                if(line.startsWith("Cookie: ")) {
                    rawCookie = line.substring(8);  // "Cookie: " 이후 문자열
                }
            }

            final HttpCookie cookies = new HttpCookie(rawCookie);
            String jsessionId = cookies.getCookie("JSESSIONID");
            boolean needSetCookie = false;

            if(jsessionId==null) {
                jsessionId = UUID.randomUUID().toString();
                needSetCookie = true;
            }

            // 3. Body 파싱 (POST 방식 데이터)
            String requestBody = "";
            if (contentLength > 0) {
                char[] buffer = new char[contentLength];
                reader.read(buffer, 0, contentLength);
                requestBody = new String(buffer);
            }

            // 4. POST 로그인 처리
            if ("POST".equals(method) && "/login".equals(path)) {
                String account = parseParam(requestBody, "account");
                String password = parseParam(requestBody, "password");

                final Optional<User> user = InMemoryUserRepository.findByAccount(account);

                // 회원 정보가 존재하고 비밀번호가 일치하는 경우
                if (user.isPresent() && user.get().checkPassword(password)) {
                    send302Redirect(outputStream, "/index.html", jsessionId, needSetCookie);
                } else {
                    send302Redirect(outputStream, "/401.html", jsessionId, needSetCookie);
                }
                return; // 302 응답 후 종료
            }

            // 5. POST 회원가입 처리
            if ("POST".equals(method) && "/register".equals(path)) {
                // Long id = Long.parseLong(parseParam(requestBody, "id"));   // 일단 id는 null 처리로 user 생성
                String account = parseParam(requestBody, "account");
                String password = parseParam(requestBody, "password");
                String email = parseParam(requestBody, "email");

                // 회원 저장
                User user = new User(account, password, email);
                InMemoryUserRepository.save(user);

                send302Redirect(outputStream, "/index.html", jsessionId, needSetCookie);
                return; // 302 응답 후 종료
            }

            // 6. GET 정적 파일 응답 (200 OK)
            String targetPath = "/".equals(path) ? "/index.html" : path;
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

            List<String> headers = new ArrayList<>();
            headers.add("HTTP/1.1 200 OK ");
            if (needSetCookie) {
                headers.add("Set-Cookie: JSESSIONID=" + jsessionId + " ");
            }
            headers.add("Content-Type: " + contentType + " ");
            headers.add("Content-Length: " + body.length + " ");
            headers.add("");
            headers.add("");

            final var response = String.join("\r\n", headers);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.write(body);
            outputStream.flush();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    // key=vaule 형태에서 value 추출
    private String parseParam(final String requestBody, final String key) {
        for(String param : requestBody.split("&")) {
            String[] keyValue= param.split("=");
            if(keyValue.length == 2 && key.equals(keyValue[0])) {
                return keyValue[1];
            }
        }
        return "";
    }

    // 302 리다이렉트 응답 전송
    private void send302Redirect(OutputStream outputStream, String redirectPath, String jsessionId, boolean needSetCookie) throws IOException {
        List<String> headers = new ArrayList<>();
        headers.add("HTTP/1.1 302 Found ");
        headers.add("Location: " + redirectPath + " ");
        if (needSetCookie) {
            headers.add("Set-Cookie: JSESSIONID=" + jsessionId + " ");
        }
        headers.add("");
        headers.add("");

        final var response = String.join("\r\n", headers);
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
