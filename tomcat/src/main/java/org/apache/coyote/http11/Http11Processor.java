package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
             final var outputStream = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            // 요청 라인 분리, 헤더 리스트 생성
            String[] requestLine = bufferedReader.readLine().split(" ");
            List<String> header = new ArrayList<>();
            while(true) {
                String line = bufferedReader.readLine();
                if(line == null || line.isEmpty()) break;
                header.add(line);
            }

            // GET /css/styles.css HTTP/1.1 각각 분리
            final String httpMethod = requestLine[0];
            final String url = requestLine[1];
            final String httpVersion = requestLine[2];

            // 경로 없음 -> Hello world!
            // 경로 존재하면 파일 읽기
            String responseBody;
            if(url.equals("/")) {
                responseBody = "Hello world!";
            } else {
                URL resource = getClass().getClassLoader().getResource("static" + url);
                responseBody = Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
            }

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
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
