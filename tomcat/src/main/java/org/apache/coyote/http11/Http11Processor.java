package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.view.ModelAndView;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            HttpRequest httpRequest = getHttpRequest(bufferedReader);

            HandlerResponse handlerResponse = HandlerManager.getUriResponse(httpRequest);

            String http11Response = getHttp11Response(handlerResponse);

            outputStream.write(http11Response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest getHttpRequest(BufferedReader bufferedReader) throws IOException {
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null && !line.equals("")) {
            stringBuilder.append(line);
            stringBuilder.append("\r\n");
        }

        return HttpRequest.of(stringBuilder.toString());
    }

    private String getHttp11Response(HandlerResponse handlerResponse) throws IOException {
        ModelAndView modelAndView = ModelAndView.of(handlerResponse);
        HttpResponse httpResponse= HttpResponse.of(modelAndView);
        return httpResponse.toString();
    }
}
