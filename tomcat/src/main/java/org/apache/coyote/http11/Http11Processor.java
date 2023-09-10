package org.apache.coyote.http11;

import static org.apache.coyote.header.HttpHeaders.CONTENT_LENGTH;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.Processor;
import org.apache.coyote.ProtocolVersion;
import org.apache.coyote.header.HttpHeaders;
import org.apache.coyote.header.HttpMethod;
import org.apache.coyote.http11.adaptor.ControllerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ControllerAdaptor controllerAdaptor;

    public Http11Processor(Socket connection, ControllerAdaptor controllerAdaptor) {
        this.connection = connection;
        this.controllerAdaptor = controllerAdaptor;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             var writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {

            String[] rawStartLine = reader.readLine().split(" ");
            HttpMethod method = HttpMethod.from(rawStartLine[0]);
            String requestUrl = rawStartLine[1];
            ProtocolVersion version = ProtocolVersion.from(rawStartLine[2]);
            HttpHeaders headers = readHeaders(reader);
            String requestBody = readBody(headers, reader);
            HttpRequest httpRequest = new HttpRequest(method, requestUrl, version, headers, requestBody);
            HttpResponse httpResponse = new HttpResponse();

            controllerAdaptor.handle(httpRequest, httpResponse);

            writer.write(httpResponse.toString());
            writer.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpHeaders readHeaders(BufferedReader reader) throws IOException {
        HttpHeaders headers = new HttpHeaders();

        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            String[] split = line.split(": ");
            headers.add(split[0], split[1]);
        }
        return headers;
    }

    private String readBody(HttpHeaders headers, BufferedReader reader) throws IOException {
        if (headers.contains(CONTENT_LENGTH)) {
            int contentLength = Integer.parseInt(headers.getValue(CONTENT_LENGTH));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);

            return requestBody;
        }
        return "";
    }
}
