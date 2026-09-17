package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 소켓의 InputStream은 클라이언트가 보낸 HTTP 요청을 바이트로 읽고,
 * OutputStream은 상태 줄, 헤더, 빈 줄, 본문으로 구성된 HTTP 응답을 클라이언트에게 전달한다.
 * HTTP 요청 줄은 "메서드 요청대상 HTTP버전" 순서이며, /index.html은 요청 본문이 아니라 요청 대상 경로이다.
 * 요청 줄과 헤더 같은 텍스트는 Reader로 변환해 해석할 수 있고, 본문은 Content-Type에 따라
 * 텍스트는 문자 인코딩을 적용하고 이미지 등의 바이너리는 바이트 그대로 처리해야 한다.
 * 문자열을 바이트로 인코딩하고 다시 문자로 디코딩할 때는 UTF-8처럼 동일한 인코딩을 명시해야 한다.
 * 파일을 응답할 때 Content-Length에는 문자열의 글자 수가 아니라 실제 응답 본문의 바이트 길이를 사용한다.
 * src/main/resources의 파일은 빌드 시 build/resources/main으로 복사되어 클래스패스에 포함되므로,
 * 실행 위치에 따라 달라지는 상대경로 대신 ClassLoader.getResourceAsStream()으로 읽을 수 있다.
 * BufferedInputStream과 BufferedOutputStream은 기존 스트림을 감싸 버퍼링 기능을 추가하며,
 * flush()는 출력 버퍼의 데이터를 전달하고 close()는 감싼 스트림까지 닫는다.
 * 파일이나 소켓 스트림은 자원 누수를 막기 위해 try-with-resources로 닫아야 한다.
 */
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

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String requestFirstLine = bufferedReader.readLine();
            String[] firstLineParts = requestFirstLine.trim().split("\\s+");
            String fileName = firstLineParts[1];

            if (fileName.equals("/")) {
                final var responseBody = "Hello world!";
                final var response = createResponse(responseBody);

                writeResponse(outputStream, response);
                return;
            }

            InputStream resourceAsStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream("static" + fileName);

            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(resourceAsStream)) {

                final var responseBody = new String(bufferedInputStream.readAllBytes(), StandardCharsets.UTF_8);

                final var response = createResponse(responseBody);

                writeResponse(outputStream, response);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String createResponse(String responseBody) {
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return response;
    }

    private static void writeResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
