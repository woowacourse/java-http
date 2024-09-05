package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ResourceProcessor resourceProcessor;
    private final ApiProcessor apiProcessor;

    public Http11Processor(final Socket connection) {
        this.resourceProcessor = new ResourceProcessor();
        this.apiProcessor = new ApiProcessor();
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String request = br.readLine();
            StringTokenizer st = new StringTokenizer(request);

            String requestMethodType = st.nextToken();
            String requestPath = st.nextToken();
            if (requestPath.equals("/")) {
                requestPath = "/index.html";
            }
            RequestPathType requestPathType = RequestPathType.reqeustPathToRequestPathType(requestPath);

            log.debug("request: " + request);
            log.debug("requestPathType: " + requestPathType.toString());
            if (requestPathType.isAPI()) {
                apiProcessor.process(connection, requestPath);
            }
            if (requestPathType.isResource()) {
                resourceProcessor.process(connection, requestPath);
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
