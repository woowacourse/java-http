package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.HandlerAdaptor;
import nextstep.jwp.http.HttpBody;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpUri;
import nextstep.jwp.http.HttpVersion;
import org.apache.coyote.Processor;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
                OutputStream outputStream = connection.getOutputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String startLine = br.readLine();
            if (startLine == null) {
                return;
            }

            String[] elements = startLine.split(" ");
            HttpMethod httpMethod = HttpMethod.from(elements[0]);
            HttpUri httpUri = HttpUri.from(elements[1]);
            HttpVersion httpVersion = HttpVersion.from(elements[2]);
            HttpHeaders httpHeaders = readHeaders(br);
            HttpBody httpBody = readBody(httpHeaders, br);

            HttpRequest request = new HttpRequest(httpHeaders, httpMethod, httpVersion, httpUri, httpBody);

            HttpResponse response = HandlerAdaptor.handle(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpBody readBody(HttpHeaders headers, BufferedReader br) throws IOException {
        if (!headers.containsKey("Content-Length")) {
            return HttpBody.from("");
        }

        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);

        return HttpBody.from(new String(buffer));
    }

    private HttpHeaders readHeaders(BufferedReader br) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;

        while (!"".equals(line = br.readLine())) {
            lines.add(line);
        }

        return HttpHeaders.from(lines);
    }

}
