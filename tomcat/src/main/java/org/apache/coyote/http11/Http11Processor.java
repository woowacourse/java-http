package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.view.UserOutput;
import org.apache.coyote.Processor;
import org.apache.coyote.common.Charset;
import org.apache.coyote.common.FileExtension;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.MediaType;
import org.apache.coyote.common.request.Request;
import org.apache.coyote.common.response.Response;
import org.apache.coyote.common.response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import org.apache.coyote.common.request.parser.RequestLineParser;

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
             final var outputStream = connection.getOutputStream()) {

            final String requestStartLine = getRequestStartLine(inputStream);
            final Request request = new Request(requestStartLine);
            final Function<Request, Response> handler = HandlerMapper.of(request);
            final String response = handler.apply(request)
                    .getResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getRequestStartLine(final InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        final StringBuilder actual = new StringBuilder();
        String line;
        while(true) {
             line = bufferedReader.readLine();
            if (line.equals("")) {
                break;
            }
            actual.append(line)
                    .append("\r\n");
        }
        while(bufferedReader.ready()) {
            char a = (char)bufferedReader.read();
            actual.append(a);
        }
        System.out.println(actual.toString());
        return actual.toString().split("\r\n")[0];
    }
}
