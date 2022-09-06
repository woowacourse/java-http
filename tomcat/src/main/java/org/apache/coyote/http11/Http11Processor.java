package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestFactory;
import org.apache.coyote.http11.request.QueryParams;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.util.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String WELCOME_MESSAGE = "Hello world!";

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {

            HttpResponse httpResponse = getResponse(HttpRequestFactory.create(reader));
            String response = httpResponse.parseToString();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getResponse(HttpRequest httpRequest) throws IOException {
        try {
            if (httpRequest.isStaticFileRequest()) {
                return getStaticResourceResponse(httpRequest);
            }
            return getDynamicResourceResponse(httpRequest);
        } catch (RuntimeException e) {
            return new HttpResponse().addProtocol(httpRequest.getRequestLine().getProtocol())
                    .addStatus(HttpStatus.NOT_FOUND)
                    .addResponseBody("페이지를 찾을 수 없습니다.", ContentType.TEXT_HTML_CHARSET_UTF_8);
        }
    }

    private HttpResponse getStaticResourceResponse(HttpRequest httpRequest) {
        Optional<String> extension = httpRequest.getExtension();
        if (extension.isPresent()) {
            ContentType contentType = ContentType.from(extension.get());
            return new HttpResponse().addProtocol(httpRequest.getRequestLine().getProtocol())
                    .addStatus(HttpStatus.OK)
                    .addResponseBody(getStaticResourceResponse(httpRequest.getRequestLine().getUri().getPath()),
                            contentType);
        }
        return new HttpResponse().addProtocol(httpRequest.getRequestLine().getProtocol())
                .addStatus(HttpStatus.NOT_FOUND)
                .addResponseBody("페이지를 찾을 수 없습니다.", ContentType.TEXT_HTML_CHARSET_UTF_8);
    }

    private String getStaticResourceResponse(String resourcePath) {
        return FileReader.readStaticFile(resourcePath, this.getClass());
    }

    private HttpResponse getDynamicResourceResponse(HttpRequest httpRequest) {
        String path = httpRequest.getRequestLine().getUri().getPath();
        if (path.equals("/")) {
            return new HttpResponse().addProtocol(httpRequest.getRequestLine().getProtocol())
                    .addStatus(HttpStatus.OK)
                    .addResponseBody(WELCOME_MESSAGE, ContentType.TEXT_HTML_CHARSET_UTF_8);
        }
        if (path.equals("/login") && httpRequest.getRequestLine().hasQueryParams()) {
            return getLoginResponseWithQueryParams(httpRequest);
        }
        if (path.equals("/login") && httpRequest.hasRequestBody()) {
            return getLoginResponseWithRequestBody(httpRequest);
        }
        if (path.equals("/register") && httpRequest.hasRequestBody()) {
            return getRegisterResponseWithRequestBody(httpRequest);
        }
        String responseBody = getStaticResourceResponse(path + ".html");
        return new HttpResponse().addProtocol(httpRequest.getRequestLine().getProtocol())
                .addStatus(HttpStatus.OK)
                .addResponseBody(responseBody, ContentType.TEXT_HTML_CHARSET_UTF_8);
    }

    private HttpResponse getLoginResponseWithQueryParams(HttpRequest httpRequest) {
        QueryParams queryParams = httpRequest.getRequestLine().getUri().getQueryParams();
        Optional<User> user = InMemoryUserRepository.findByAccount(queryParams.getParameterValue("account"));
        if (user.isPresent()) {
            if (user.get().checkPassword(queryParams.getParameterValue("password"))) {
                return new HttpResponse().addProtocol(httpRequest.getRequestLine().getProtocol())
                        .addStatus(HttpStatus.FOUND)
                        .addLocation("/index.html");
            }
        }
        return new HttpResponse().addProtocol(httpRequest.getRequestLine().getProtocol())
                .addStatus(HttpStatus.FOUND)
                .addLocation("/401.html");
    }

    private HttpResponse getLoginResponseWithRequestBody(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();
        Optional<User> user = InMemoryUserRepository.findByAccount(requestBody.getValue("account"));
        if (user.isPresent()) {
            if (user.get().checkPassword(requestBody.getValue("password"))) {
                return new HttpResponse().addProtocol(httpRequest.getRequestLine().getProtocol())
                        .addStatus(HttpStatus.FOUND)
                        .addLocation("/index.html")
                        .addCookie("JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
            }
        }
        return new HttpResponse().addProtocol(httpRequest.getRequestLine().getProtocol())
                .addStatus(HttpStatus.FOUND)
                .addLocation("/401.html");
    }

    private HttpResponse getRegisterResponseWithRequestBody(HttpRequest httpRequest) {
        String account = httpRequest.getRequestBody().getValue("account");
        String password = httpRequest.getRequestBody().getValue("password");
        String email = httpRequest.getRequestBody().getValue("email");

        InMemoryUserRepository.save(new User(account, password, email));
        return new HttpResponse().addProtocol(httpRequest.getRequestLine().getProtocol())
                .addStatus(HttpStatus.FOUND)
                .addLocation("/index.html");
    }
}
