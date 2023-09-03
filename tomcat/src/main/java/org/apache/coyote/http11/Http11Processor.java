package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

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

    // /login -> 로그인 페이지를 보여준다. -> get /login
    // /login? eee -> 로그인 요청을 하는것
    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final StringBuilder requestBuilder = new StringBuilder();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                requestBuilder.append(line).append("\r\n");
            }

            String request = requestBuilder.toString();
            System.out.println("request: "+request);

            String[] requestLines = request.split("\\s+");

            if (requestLines.length < 2) {
                throw new UncheckedServletException(new Exception("예외"));
            }

            String resourcePath = requestLines[1];
            String requestMethod = requestLines[0];

            HttpResponse httpResponse = new HttpResponse(StatusCode.OK,ContentType.from(resourcePath),new String(getResponseBodyBytes(resourcePath), UTF_8));

            // 로그인 처리
            System.out.println("resourcePath: "+resourcePath);
            if (resourcePath.equals("/login") && requestMethod.equals("GET")) {
                httpResponse = new HttpResponse(StatusCode.OK,ContentType.from(resourcePath),new String(getResponseBodyBytes(resourcePath), UTF_8));
            }

            if (resourcePath.contains("/login") && requestMethod.equals("POST")) {
                // 쿼리스트링을 확인하여 로그인 확인한다.
                LoginHandler loginHandler = new LoginHandler();
                if(loginHandler.login(resourcePath)){
                    httpResponse = new HttpResponse(StatusCode.FOUND,ContentType.from("/index.html"),new String(getResponseBodyBytes("/index.html"), UTF_8));
                } else{
                    // 이거까진 됨..
                    httpResponse = new HttpResponse(StatusCode.OK,ContentType.from("/401.html"),new String(getResponseBodyBytes("/401.html"), UTF_8));
                }
            }

            outputStream.write(httpResponse.getResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
    private byte[] getResponseBodyBytes(String resourcePath) throws IOException {
        final URL fileUrl = this.getClass().getClassLoader().getResource("static"+resourcePath);
        return Files.readAllBytes(Paths.get(fileUrl.getPath()));
    }

}
