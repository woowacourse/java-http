package nextstep.org.apache.coyote.http11;

import static nextstep.org.apache.coyote.http11.HttpUtil.createResponseBody;
import static nextstep.org.apache.coyote.http11.HttpUtil.selectFirstContentTypeOrDefault;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.org.apache.coyote.Processor;
import nextstep.org.apache.catalina.Context;
import nextstep.org.apache.catalina.servlet.Servlet;
import nextstep.org.apache.coyote.http11.request.Http11Request;
import nextstep.org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Context context;
    private final Socket connection;

    public Http11Processor(final Context context, final Socket connection) {
        this.context = context;
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                InputStream inputStream = connection.getInputStream();
                OutputStream outputStream = connection.getOutputStream()
        ) {
            Http11Request request = new Http11Request(inputStream);
            Http11Response response = new Http11Response(Status.OK);

            Servlet servlet = context.getServlet(request.getPathInfo());
            try {
                servlet.service(request, response);
            } catch (Exception e) {
                responseInternalServerError(request, response);
            }

            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void responseInternalServerError(Http11Request request, Http11Response response)
            throws IOException {
        String internalServerErrorPageBody = createResponseBody("/500.html")
                .orElse(Status.INTERNAL_SERVER_ERROR.getCodeMessage());
        String contentType = selectFirstContentTypeOrDefault(request.getHeader(HttpHeader.ACCEPT));
        response.setStatus(Status.INTERNAL_SERVER_ERROR)
                .setHeader(HttpHeader.CONTENT_TYPE, contentType + ";charset=utf-8")
                .setHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(
                        internalServerErrorPageBody.getBytes(
                                StandardCharsets.UTF_8).length))
                .setBody(internalServerErrorPageBody);
    }

    private void writeResponse(OutputStream outputStream, Http11Response response)
            throws IOException {
        outputStream.write(response.createResponseAsByteArray());
        outputStream.flush();
    }
}
