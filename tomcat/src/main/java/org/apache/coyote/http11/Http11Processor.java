package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

            // 2. Request Line(첫 줄)을 읽음 (예: "GET /index.html HTTP/1.1")
            final String requestLine = reader.readLine();
            log.info("request line 첫 줄: {}", requestLine);
            if (requestLine == null) {
                log.warn("클라이언트로부터 전달된 RequestLine이 존재하지 않습니다.");
                return;
            }

            // 3. 헤더의 끝(빈 줄 "")이 나올 때까지 헤더들을 읽어 소진 (무한 루프 방지)
            String line;
            while ((line = reader.readLine()) != null && !"".equals(line)) {
                // 헤더 내용 소진
            }

            // 4. Request Line에서 공백으로 문자열을 분리하여 URI 경로를 추출
            final String uri = requestLine.split(" ")[1];
            String path = uri;
            String queryString = "";
            log.info("path: {}", path);

            if (uri.contains("?")) {
                int index = uri.indexOf("?");
                path = uri.substring(0, index);  // 순수 경로, 예: /login
                queryString = uri.substring(index + 1);  // 쿼리 스트링, 예: account=moving@naver.com&password=password
            }

            // 5. /login 요청 시 Query String 파싱 및 회원 조회
            if ("/login".equals(path)) {
                String account = "";
                String password = "";

                // "account=gugu&password=password" -> ["account=gugu", "password=password"]
                for (String param : queryString.split("&")) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        if ("account".equals(keyValue[0])) {
                            account = keyValue[1];
                        }
                        if ("password".equals(keyValue[0])) {
                            password = keyValue[1];
                        }
                    }
                }

                final var user = InMemoryUserRepository.findByAccount(account);
                log.info("조회된 회원: {}", user);

                // 응답해 줄 실제 정적 파일 경로 지정
                path = "/login.html";
            }

            // 6. ClassLoader를 통해 resources/static 디렉토리 내의 파일 데이터를 바이트 배열로 읽는다.
            byte[] body;
            final String contentType; // 기본 값
            final var resourceUrl = getClass().getClassLoader().getResource("static" + path);

            // 자원이 존재하고, 디렉터리가 아닌 파일인 경우
            if (resourceUrl != null && !Files.isDirectory(Path.of(resourceUrl.toURI()))) {
                body = Files.readAllBytes(Path.of(resourceUrl.toURI()));
                // 확장자에 따른 raw한 if-else 조건 처리
                if (path.endsWith(".css")) {
                    contentType = "text/css;charset=utf-8";
                } else if (path.endsWith(".js")) {
                    contentType = "application/javascript;charset=utf-8";
                } else if (path.endsWith(".ico")) {
                    contentType = "image/x-icon";
                } else {
                    contentType = "text/html;charset=utf-8";
                }
            } else {
                // '/' 요청이나 static에 파일이 없는 경우 기본 Hello world!
                body = "Hello world!".getBytes(StandardCharsets.UTF_8);
                contentType = "text/html;charset=utf-8";
            }
            
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: "+contentType+" ",
                    "Content-Length: " + body.length + " ",
                    "",
                    "");

            // 7. 헤더 텍스트와 파일 바이트(본문)를 순서대로 전송한다.
            // HTTP 응답은 규격상 무조건 [헤더 텍스트] + [빈 줄(\r\n)] + [바디 데이터] 순서로 날아간다.
            outputStream.write(response.getBytes(StandardCharsets.UTF_8));  // 헤더 텍스트
            outputStream.write(body);  // 바디 데이터
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
