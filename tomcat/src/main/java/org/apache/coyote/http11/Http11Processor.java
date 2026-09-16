package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
        try (final var inputStream = connection.getInputStream();
             // step 1-1 connection으로 받은 inputStream은 byteStream.  GET /index.html...이 바이트 형태로 저장된 상태임.
             // step 1-1 URL이 /index.html 인지 확인해야 하므로, 바이트를 문자로 변환해야 한다.
             final var inputStreamReader = new InputStreamReader(inputStream);

             // step 1-1 버퍼링해서 받기.. (근데 아직 왜 써야하는지 와닿지가 않음)
             final var bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {

            // step 1-1 모든 형태를 파싱하는 건 아직 어려우니, 첫 라인을 공백 단위로 split한다.
            String firstHeaderLine = bufferedReader.readLine();
            String[] tokens = firstHeaderLine.split(" ");
            log.info("요청 헤더: {}, 요청 URL: {}", firstHeaderLine, tokens[1]);

            // step 1-1 1번 인덱스 요소가 index.html과 같으면, resources/static/index.html을 읽어서 HTTP Response 포맷에 맞게 바꿔서 내려준다.
            if(tokens[1].startsWith("/index.html")) {
                // 빌드 기준 산출물 위치가 달라지므로, 상대 경로를 사용해야 한다.
                URL url = getClass().getClassLoader().getResource("static/index.html");
                byte[] contents = Files.readAllBytes(new File(url.getFile()).toPath());

                final var responseBody = new String(contents);

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if(tokens[1].startsWith("/login")) {
                // 빌드 기준 산출물 위치가 달라지므로, 상대 경로를 사용해야 한다.
                URL url = getClass().getClassLoader().getResource("static/login.html");
                byte[] contents = Files.readAllBytes(new File(url.getFile()).toPath());

                final var responseBody = new String(contents);

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();


                // 1-3 계정 찾아서 출력하기
                // 쿼리 파라미터 확인하기
                int questionMarkIndex = tokens[1].lastIndexOf("?");
                String queries = tokens[1].substring(questionMarkIndex+1);
                String[] query = queries.split("&");
                Map<String, String> queryMap = new HashMap<>();
                for (String s : query) {
                    String[] keyToken = s.split("=");
                    queryMap.put(keyToken[0], keyToken[1]);
                }

                String account = queryMap.get("account");
                String password = queryMap.get("password");

                Optional<User> user = InMemoryUserRepository.findByAccount(account);
                if(user.isPresent()){
                    User user1 = user.get();
                    if(user1.checkPassword(password)){
                        log.info("{}", user1);
                    }
                }
                return;
            }

            // step 1-2 경로가 /css/style.css인 경우, css 파일도 내려준다.
            if(tokens[1].startsWith("/css/styles.css")){
                URL url = getClass().getClassLoader().getResource("static/css/styles.css");
                byte[] contents = Files.readAllBytes(new File(url.getFile()).toPath());

                final var responseBody = new String(contents);

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            // step 1-2 경로가 /css/style.css인 경우, css 파일도 내려준다.
            if(tokens[1].startsWith("/assets/chart-bar.js")){
                URL url = getClass().getClassLoader().getResource("static/assets/chart-bar.js");
                byte[] contents = Files.readAllBytes(new File(url.getFile()).toPath());

                final var responseBody = new String(contents);

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/js;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
            // step 1-2 경로가 /css/style.css인 경우, css 파일도 내려준다.
            if(tokens[1].startsWith("/js/scripts.js")){
                URL url = getClass().getClassLoader().getResource("static/js/scripts.js");
                byte[] contents = Files.readAllBytes(new File(url.getFile()).toPath());

                final var responseBody = new String(contents);

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/js;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
            // step 1-2 경로가 /css/style.css인 경우, css 파일도 내려준다.
            if(tokens[1].startsWith("/assets/chart-pie.js")){
                URL url = getClass().getClassLoader().getResource("static/assets/chart-pie.js");
                byte[] contents = Files.readAllBytes(new File(url.getFile()).toPath());

                final var responseBody = new String(contents);

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/js;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            final var responseBody = "Hello world!";

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
