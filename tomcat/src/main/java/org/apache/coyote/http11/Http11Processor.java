package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.support.Controller;
import org.apache.coyote.support.RequestMapping;
import org.apache.coyote.util.HttpRequestUtils;
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
        try (InputStream inputStream = connection.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
            service(bufferedReader, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void service(final BufferedReader bufferedReader, final BufferedWriter outputStream) throws Exception {
        RequestMapping requestMapping = new RequestMapping();
        HttpRequest request = HttpRequestUtils.newHttpRequest(bufferedReader);
        Controller controller = requestMapping.getController(request.getPath());
        controller.service(request, new HttpResponse(outputStream));
    }
}
