package nextstep.jwp.framework.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.framework.controller.Controller;
import nextstep.jwp.framework.controller.standard.StaticResourceController;
import nextstep.jwp.framework.infrastructure.exception.WebServerException;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequestBody;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequestHeader;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.mapping.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    public static final StaticResourceController STATIC_RESOURCE_CONTROLLER =
        new StaticResourceController();

    private final Socket connection;
    private final RequestMapping requestMapping;

    public RequestHandler(Socket connection, RequestMapping requestMapping) {
        this.connection = connection;
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream()) {
            HttpResponse httpResponse = parseResponse(inputStream);
            outputStream.write(httpResponse.writeResponseMessage().getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception Socket");
        } finally {
            close();
        }
    }

    private HttpResponse parseResponse(InputStream inputStream) {
        try {
            BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(inputStream));
            List<String> httpRequestHeaders = readRestHttpRequestHeaderLines(bufferedReader);
            HttpRequestHeader httpRequestHeader = HttpRequestHeader.from(httpRequestHeaders);
            HttpRequestBody httpRequestBody = parseHttpRequestBody(
                httpRequestHeader,
                bufferedReader
            );
            HttpRequest httpRequest = new HttpRequest(httpRequestHeader, httpRequestBody);
            Controller controller = requestMapping.findController(httpRequest);
            return controller.doService(httpRequest);
        } catch (WebServerException e) {
            HttpRequest errorRequest = HttpRequest.ofStaticFile(e.getHttpStatus());
            return STATIC_RESOURCE_CONTROLLER.doService(errorRequest);
        } catch (IOException | RuntimeException e) {
            log.error("Exception stream or Parsing error", e);
            HttpRequest errorRequest = HttpRequest.ofStaticFile("/500.html");
            return STATIC_RESOURCE_CONTROLLER.doService(errorRequest);
        }
    }

    private List<String> readRestHttpRequestHeaderLines(
        BufferedReader bufferedReader
    ) throws IOException {
        List<String> lines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            lines.add(line);
            line = bufferedReader.readLine();
            if (Objects.isNull(line)) {
                break;
            }
        }
        return lines;
    }

    private HttpRequestBody parseHttpRequestBody(
        HttpRequestHeader httpRequestHeader,
        BufferedReader bufferedReader
    ) {
        int contentLength = Integer.parseInt(httpRequestHeader.getContentLength());
        if (contentLength == 0) {
            return new HttpRequestBody(null);
        }
        try {
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return new HttpRequestBody(new String(buffer));
        } catch (IOException exception) {
            log.error("Exception stream", exception);
            return new HttpRequestBody(null);
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
