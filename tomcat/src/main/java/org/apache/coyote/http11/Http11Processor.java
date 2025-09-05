package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

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
        /*
         * 1. inputStream -> Request resource 읽어오기
         *      첫 번째 줄 파싱 (method 뒤에 명시된 파일 명 기반으로 찾기)
         * 2. resource 찾기 (with ClassLoader)
         * 3. 찾은 파일 내용을 읽어서 outputStream 에 전달 (바이트 코드로)
         */
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            // 1. request resource 읽어오기
            // 1-1. inputStream 에서 request 정보 읽어오기 위한 준비 - 라인별 텍스트 처리를 위해 BufferedReader 사용
            final var reader = new BufferedReader(new InputStreamReader(inputStream));
            final var request = reader.readLine();

            // 1-2. request url 에서 리소스명 추출
            final var split = Arrays.stream(request.split("\\s+")).toList(); // 공백 문자를 기준으로 파싱
            if (split.isEmpty()) {
                // TODO: 404 Bad Request
                throw new IllegalArgumentException("요청 정보가 올바르지 않습니다.");
            }
            final var target = split.get(1);

            // 3. 파일 내용 읽기
            String responseBody = readStaticFileByName(target);

            // 파일 내용을 바이트로 변환하여 응답
            final var response = getHttp200Response(responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * get content by static file name
     * @param target target file name
     * @return target html file's text content
     * @throws IOException occurs when there are invalid bytes in file
     */
    private String readStaticFileByName(final String target) throws IOException {
        if (target.equals("/")) {
            return "Hello world!";
        }

        final var resource = getStaticResource(target);
        if (resource == null) {
            return readNotFoundFile();
        }
        return readContent(resource);
    }

    /**
     * get '404 not found' html content
     * @return not found html file's text content
     * @throws IOException occurs when there are invalid bytes in file
     */
    private String readNotFoundFile() throws IOException {
        final var notfoundResource = getStaticResource("/404.html");
        return readContent(notfoundResource);
    }

    /**
     *
     * @param url resource's URL
     * @return resource's text content
     * @throws IOException occurs when there are invalid bytes in file
     */
    private String readContent(final URL url) throws IOException {
        final var result = new StringBuilder();
        final var readLines = Files.readAllLines(Path.of(url.getPath()));
        for (String line : readLines) {
            result.append(line);
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * find static resource path url
     * @param target target file name
     * @return target resource path url
     */
    private URL getStaticResource(final String target) {
        final var loader = ClassLoader.getSystemClassLoader();
        return loader.getResource("static" + target);
    }

    /**
     * get HTTP 200 OK response
     * @param responseBody response body
     * @return 200 response body text
     */
    private String getHttp200Response(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
