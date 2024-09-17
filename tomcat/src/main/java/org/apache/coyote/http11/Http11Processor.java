package org.apache.coyote.http11;

import com.techcourse.RequestMapping;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.catalina.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.data.ContentType;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;
import org.apache.coyote.http11.data.HttpStatusCode;
import org.apache.coyote.http11.data.HttpVersion;
import org.apache.coyote.http11.data.MediaType;
import org.apache.coyote.http11.parser.HttpRequestParser;
import org.apache.coyote.http11.parser.HttpResponseParser;
import org.apache.catalina.session.InvalidSessionRemover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             OutputStream outputStream = connection.getOutputStream()) {
            HttpResponse httpResponse = null;
            try {
                HttpRequest httpRequest = HttpRequestParser.parse(bufferedReader);
                httpResponse = requestResponse(httpRequest);
            } catch (IllegalArgumentException e) {
                httpResponse = new HttpResponse(HttpVersion.HTTP_1_1)
                        .setHttpStatusCode(HttpStatusCode.BAD_REQUEST)
                        .setResponseBody(e.getMessage())
                        .setContentType(new ContentType(MediaType.HTML, null));
            }
            String response = HttpResponseParser.parse(httpResponse);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse requestResponse(HttpRequest request) throws IOException {
        HttpResponse response = InvalidSessionRemover.remove(request);
        if (response != null) {
            return response;
        }

        Controller controller = RequestMapping.getController(request);
        if (controller == null) {
            return new HttpResponse(HttpVersion.HTTP_1_1)
                    .setHttpStatusCode(HttpStatusCode.NOT_FOUND);
        }

        response = new HttpResponse(HttpVersion.HTTP_1_1);
        controller.service(request, response);
        return response;
    }
}
