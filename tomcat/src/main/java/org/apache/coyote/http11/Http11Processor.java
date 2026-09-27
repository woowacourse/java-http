package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
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
            String jsessionId = request.getJsessionId();
            if (jsessionId == null) {
                jsessionId = UUID.randomUUID().toString();
                response.addCookie("JSESSIONID", jsessionId);
            }

            Controller controller = RequestMapping.getController(request.getPath());
            controller.service(request, response);

            response.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
