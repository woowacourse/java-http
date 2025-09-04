package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            List<String> httpRequestHeader = new ArrayList<>();

            // 첫 라인 가져오기 (ex. GET /index.html HTTP/1.1)
            String httpFirstLine = bufferedReader.readLine();

            String headerLine = null;
            while ((headerLine = bufferedReader.readLine()) != null) {
                if ("".equals(headerLine)) {
                    break;
                }
                httpRequestHeader.add(headerLine);
            }

            // 읽어온 헤더 파싱 및 저장
            Map<String, String> headerInfo = new HashMap<>();
            for (String requestPayload : httpRequestHeader) {
                String[] requestHeader = requestPayload.split(":");
                String headerKey = requestHeader[0].trim().toLowerCase();
                String headerValue = requestHeader[1].trim().toLowerCase();
                headerInfo.put(headerKey, headerValue);
            }

            // header에 content-length가 존재하면 body 저장
            String requestBody = null;

            if (headerInfo.containsKey("content-length")) {
                int contentLength = Integer.parseInt(headerInfo.get("content-length"));
                requestBody = new String(inputStream.readNBytes(contentLength));
            }

            // 첫 라인 공백 기준으로 split
            List<String> HttpFirstLineElements = Arrays.stream(httpFirstLine.split(" ")).toList();

            String httpMethod = HttpFirstLineElements.getFirst();
            String httpRequestPath = HttpFirstLineElements.get(1);
            int queryParameterStartIndex = httpRequestPath.indexOf('?');
            Map<String, String> queryParameter = new HashMap<>();

            if (queryParameterStartIndex != -1) {
                String queryParameterPayloadLine = httpRequestPath.substring(queryParameterStartIndex + 1);
                httpRequestPath = httpRequestPath.substring(0, queryParameterStartIndex);

                String[] queryParameterPayloads = queryParameterPayloadLine.split("&");
                for (String queryParameterPayload : queryParameterPayloads) {
                    String[] queryParameterKeyValue = queryParameterPayload.split("=");
                    String queryParameterKey = queryParameterKeyValue[0].trim();
                    String queryParameterValue = queryParameterKeyValue[1].trim();
                    queryParameter.put(queryParameterKey, queryParameterValue);
                }
            }

            // 특정 요청 구조인지 확인하고 실행
            if (HttpFirstLineElements.get(0).equals("GET") && HttpFirstLineElements.get(1).equals("/")) {
                helloWorld(outputStream);
            }
            if (HttpFirstLineElements.get(0).equals("GET") && HttpFirstLineElements.get(1).equals("/index.html")) {
                getIndexHtml(outputStream);
            }
            if (HttpFirstLineElements.get(0).equals("GET") && HttpFirstLineElements.get(1).equals("/css/styles.css")) {
                getCssStyles(outputStream);
            }
            if (HttpFirstLineElements.get(0).equals("GET") && HttpFirstLineElements.get(1).equals("/js/scripts.js")) {
                getJsScripts(outputStream);
            }
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void getJsScripts(final OutputStream outputStream) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource("static/js/scripts.js").toURI());

        final byte[] bytes = Files.readAllBytes(path);

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/js;charset=utf-8 ",
                "Content-Length: " + bytes.length + " ",
                "",
                new String(bytes)
        );

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void getCssStyles(final OutputStream outputStream) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource("static/css/styles.css").toURI());

        final byte[] bytes = Files.readAllBytes(path);

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: " + bytes.length + " ",
                "",
                new String(bytes)
        );

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void getIndexHtml(final OutputStream outputStream) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource("static/index.html").toURI());

        final byte[] bytes = Files.readAllBytes(path);

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + bytes.length + " ",
                "",
                new String(bytes)
        );

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void helloWorld(final OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
