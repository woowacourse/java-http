package org.apache.coyote.http11;

import com.techcourse.FrontController;
import com.techcourse.exception.UncheckedServletException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            HttpRequest request = createRequest(inputStream);
            log.info("[REQUEST] = {}", request);
            HttpResponse response = createResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createRequest(InputStream inputStream) {
        try {
            byte[] bytes = new byte[18000]; // TODO refactor
            int readByteCount = inputStream.read(bytes);
            String requestString = new String(bytes, 0, readByteCount, StandardCharsets.UTF_8);
            return HttpRequestParser.parse(requestString);
        } catch (IOException e) {
            log.error("IO Exception occur during make request object");
        }
        return null;
    }

    private HttpResponse createResponse(HttpRequest request) throws IOException {
        /**
         * 지금은 모든 응답에 정적 페이지를 넣어주기 때문에 여기서 처리해도 될듯
         *  ~~또한 굳이 응답 객체를 컨트롤러에서 생성할 이유가 안보임~~
         *      -> 응답 코드 생성을 컨트롤러에서 처리해야기 때문에 응답 객체를 컨트롤러에서 생성해야함
         *
         *  응답 객체의 생성은 어디서 해야할까?
         *      -> Processor가 하는게 맞을 것 같다. 이유는 요청 객체도 여기서 생성하기 때문에
         *      요청과 응답 객체 생성의 책임을 한 곳으로 통일하는게 유지보수에 더 좋을 것 같기 떄문.
         */
        HttpResponse response = new HttpResponse();

        FrontController.service(request, response);

        if (response.isError()) { // TODO 중복 코드 리팩터링
            String responseBody = createResponseBody(ContentType.HTML, "/" + response.getCode() + ".html");
            response.setBody(responseBody);
            Map<String, String> responseHeader = createResponseHeader(ContentType.HTML, responseBody.getBytes().length);
            response.setHeaders(responseHeader);
            return response;
        }

        // 정적 파일을 응답
        String url = request.getUrl();
        ContentType contentType = ContentType.findByUrl(url);

        String responseBody = createResponseBody(contentType, url);
        Map<String, String> responseHeader = createResponseHeader(contentType, responseBody.getBytes().length);

        response.setHeaders(responseHeader);
        response.setBody(responseBody);

        return response;
    }

    private String createResponseBody(ContentType contentType, String url) throws IOException {
        String resourceUrl = getResourceUrl(contentType, url);
        URL resource = Http11Processor.class.getClassLoader().getResource(resourceUrl);
        File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    private String getResourceUrl(ContentType contentType, String rawUrl) {
        if (rawUrl.contains(".")) {
            return "static" + rawUrl;
        }
        return "static" + rawUrl + "." + contentType.getType();
    }

    private Map<String, String> createResponseHeader(ContentType contentType, int length) {
        Map<String, String> header = new LinkedHashMap<>();
        header.put("Content-Type", contentType.getValue() + ";" + "charset=utf-8 ");
        header.put("Content-Length", length + " ");
        return header;
    }
}
