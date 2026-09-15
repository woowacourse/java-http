package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
             // connection으로 받은 inputStream은 byteStream.  GET /index.html...이 바이트 형태로 저장된 상태임.
             // URL이 /index.html 인지 확인해야 하므로, 바이트를 문자로 변환해야 한다.
             final var inputStreamReader = new InputStreamReader(inputStream);

             // 버퍼링해서 받기.. (근데 아직 왜 써야하는지 와닿지가 않음)
             final var bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {

            // 모든 형태를 파싱하는 건 아직 어려우니, 첫 라인을 공백 단위로 split한다.
            String firstHeaderLine = bufferedReader.readLine();
            String[] tokens = firstHeaderLine.split(" ");

            // 1번 인덱스 요소가 index.html과 같으면, resources/static/index.html을 읽어서 HTTP Response 포맷에 맞게 바꿔서 내려준다.
            if(tokens[1].equals("/index.html")) {
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
