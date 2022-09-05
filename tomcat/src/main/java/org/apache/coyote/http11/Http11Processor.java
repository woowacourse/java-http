package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.service.LoginService;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.util.HttpStartLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String HTML_MIME_TYPE = "text/html";
    private static final String RESOURCES_PREFIX = "static";

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String startLine = bufferedReader.readLine();
            HttpStartLineParser httpStartLineParser = new HttpStartLineParser(startLine);

            HttpMethod httpMethod = httpStartLineParser.getHttpMethod();
            String httpUrl = httpStartLineParser.getHttpUrl();
            Map<String, String> queryParams = httpStartLineParser.getQueryParams();

            if (httpUrl.equals("/favicon.ico")) {
                return;
            }

            String response = getResponse(httpMethod, httpUrl, queryParams);
            byte[] bytes = response.getBytes();
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(HttpMethod httpMethod, String httpUrl, Map<String, String> queryParams)
            throws URISyntaxException, IOException {
        if (httpUrl.equals("/")) {
            String responseBody = "Hello world!";
            return toResponse(responseBody, HTML_MIME_TYPE);
        }

        if (HttpMethod.GET.equals(httpMethod) && httpUrl.startsWith("/login")) {
            LoginService loginService = new LoginService();
            loginService.login(queryParams);
        }

        String fileName = ViewResolver.convert(httpUrl);
        File file = getFile(fileName);
        String responseBody = toResponseBody(file);
        FileType fileType = FileType.from(fileName);
        return toResponse(responseBody, fileType.getMimeType());
    }

    private File getFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(RESOURCES_PREFIX + fileName);

        return Paths.get(resource.toURI()).toFile();
    }

    private String toResponseBody(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();

        String nextLine;
        while ((nextLine = bufferedReader.readLine()) != null) {
            stringBuilder.append(nextLine);
            stringBuilder.append(System.lineSeparator());
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    private String toResponse(String responseBody, String mimeType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + mimeType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
