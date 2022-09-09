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
import nextstep.jwp.exception.LoginFailException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.RegisterService;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.HttpResponse.HttpResponseBuilder;
import org.apache.coyote.http11.util.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String RESOURCES_PREFIX = "static";
    private static final String INDEX_REDIRECT_PAGE = "/index.html";
    private static final String ERROR_REDIRECT_PAGE = "/401.html";
    private static final String JSESSIONID = "JSESSIONID";
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
            HttpRequest httpRequest = new HttpRequest(bufferedReader);
            String response = getResponse(httpRequest);

            byte[] bytes = response.getBytes();
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(HttpRequest httpRequest) throws URISyntaxException, IOException {
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        String httpUrl = httpRequest.getHttpUrl();
        HttpCookie httpCookie = new HttpCookie();

        if (HttpMethod.GET.equals(httpMethod) && httpUrl.equals("/favicon.ico")) {
            return "";
        }

        if (HttpMethod.GET.equals(httpMethod) && httpUrl.equals("/")) {
            String responseBody = "Hello world!";
            return toResponse(HttpStatus.OK, httpCookie, FileType.HTML, responseBody);
        }

        if (HttpMethod.POST.equals(httpMethod) && httpUrl.startsWith("/login")) {
            Map<String, String> requestBody = httpRequest.getRequestBody();
            LoginService loginService = new LoginService();

            try {
                loginService.login(requestBody);
                String identifier = UUIDGenerator.generate();
                httpCookie.addCookie(JSESSIONID, identifier);
                return toRedirectResponse(httpCookie, INDEX_REDIRECT_PAGE);
            } catch (LoginFailException e) {
                return toRedirectResponse(httpCookie, ERROR_REDIRECT_PAGE);
            }
        }

        if (HttpMethod.POST.equals(httpMethod) && httpUrl.startsWith("/register")) {
            Map<String, String> requestBody = httpRequest.getRequestBody();
            RegisterService registerService = new RegisterService();

            try {
                registerService.register(requestBody);
                return toRedirectResponse(httpCookie, INDEX_REDIRECT_PAGE);
            } catch (LoginFailException e) {
                return toRedirectResponse(httpCookie, ERROR_REDIRECT_PAGE);
            }
        }

        String fileName = ViewResolver.convert(httpUrl);
        File file = getFile(fileName);
        String responseBody = toResponseBody(file);
        FileType fileType = FileType.from(fileName);
        return toResponse(HttpStatus.OK, httpCookie, fileType, responseBody);
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

    private String toResponse(HttpStatus status, HttpCookie cookie, FileType fileType, String responseBody) {
        return new HttpResponseBuilder()
                .status(status)
                .cookie(cookie)
                .mimeType(fileType.getMimeType())
                .responseBody(responseBody)
                .toResponse();
    }

    private String toRedirectResponse(HttpCookie cookie, String redirectUrl) {
        return new HttpResponseBuilder()
                .status(HttpStatus.FOUND)
                .cookie(cookie)
                .location(redirectUrl)
                .toRedirectResponse();
    }
}
