package nextstep.jwp.handler;

import static nextstep.jwp.http.response.HttpStatus.OK;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpResponseBody;
import nextstep.jwp.resolver.DataResolver;
import nextstep.jwp.resolver.HtmlResolver;
import nextstep.jwp.resolver.StaticResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final List<DataResolver> dataResolvers;

    public RequestHandler(Socket connection) {
        this(connection, List.of(new StaticResourceResolver(), new HtmlResolver()));
    }

    public RequestHandler(Socket connection,
                          List<DataResolver> dataResolvers) {
        this.connection = Objects.requireNonNull(connection);
        this.dataResolvers = dataResolvers;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = HttpRequest.of(inputStream);
            log.debug(httpRequest.toString());
            String url = httpRequest.url();

            HttpResponse httpResponse =
                new HttpResponse
                    .Builder(httpRequest.protocol(), OK)
                    .build();

            if (url.equals("/login") && httpRequest.method() == HttpMethod.POST) {
                LoginController loginController = new LoginController();
                loginController.doPost(httpRequest, httpResponse);
            }

            if (url.equals("/registerz") && httpRequest.method() == HttpMethod.POST) {
                RegisterController registerController = new RegisterController();
                registerController.doPost(httpRequest, httpResponse);
            }

            if (httpResponse.status() == OK) {
                HttpResponseBody responseBody = resolveData(url,
                    httpRequest.header("Accept").list());
                httpResponse.replaceResponseBody(responseBody);
            }
            log.debug("\r\n");
            log.debug(httpResponse.toResponseFormat());
            outputStream.write(httpResponse.toResponseFormat().getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private HttpResponseBody resolveData(String filePath, List<String> acceptType)
        throws IOException {
        if (acceptType.isEmpty()) {
            acceptType = List.of("*/*");
        }
        return findProperMimeTypeData(filePath, acceptType);
    }

    private HttpResponseBody findProperMimeTypeData(String filePath, List<String> acceptTypes)
        throws IOException {
        for (DataResolver dataResolver : dataResolvers) {
            if (dataResolver.isSuitable(acceptTypes) &&
                dataResolver.isExist(filePath)
            ) {
                return dataResolver.resolve(filePath);
            }
        }
        throw new NotFoundException(
            String.format("해당 타입의 파일이 없습니다. 요청받은 accept -> %s, filepath -> %s", acceptTypes,
                filePath));
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
