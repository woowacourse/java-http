package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.TomcatController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final TomcatController tomcatController;

    public Http11Processor(final Socket connection, TomcatController tomcatController) {
        this.connection = connection;
        this.tomcatController = tomcatController;
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

            final var requestData = getRequestData(inputStream);
            log.info("requestData : {}", requestData);
            tomcatController.handleRequest(requestData);
            final var response = getResponse(requestData);

            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestData getRequestData(InputStream inputStream) throws IOException {
        final var inputReader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> rawHttpRequest = new ArrayList<>();
        String line;
        while ((line = inputReader.readLine()) != null && !line.isBlank()) {
            rawHttpRequest.add(line);
        }
        if (rawHttpRequest.isEmpty()) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
        return RequestData.of(rawHttpRequest);
    }

    private String getResponse(RequestData requestData) {
        try {
            final String staticFilePath = getStaticFilePath(requestData);
            final var responseBody = readFile(staticFilePath);
            return String.join("\r\n",
                    requestData.getHttpVersion().getResponseHeader() + " " + HttpStatus.OK.getResponseLabel(),
                    HttpContentType.getByFileName(staticFilePath).getResponseHeader(),
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        } catch (UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
        throw new IllegalArgumentException("응답 생성 중에 오류가 발생했습니다.");
    }

    private String getStaticFilePath(RequestData requestData) {
        String resource = requestData.getResource();
        String staticFilePath = "static" + resource;
        if (resource.isBlank() || resource.equals("/")) {
            staticFilePath = "static/index.html";
        }
        if (requestData.getHttpContentType() == HttpContentType.HTML && !staticFilePath.endsWith(".html")) {
            staticFilePath += ".html";
        }
        return staticFilePath;
    }

    private String readFile(String staticFilePath) {
        try {
            final URL resourceUrl = getClass().getClassLoader().getResource(staticFilePath);
            if (resourceUrl == null) {
                throw new IllegalArgumentException("존재하지 않는 리소스입니다.: " + staticFilePath);
            }
            String path = resourceUrl.getPath();
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            log.error("파일을 불러오는데 실패했습니다. : {}", e);
        }
        throw new IllegalArgumentException("파일을 불러오는데 실패했습니다.: " + staticFilePath);
    }
}
