package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.constant.MediaType;
import org.apache.coyote.Processor;
import org.apache.coyote.support.HttpRequest;
import org.apache.coyote.support.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.IoUtils;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private Socket connection;
    private Router router;

    public Http11Processor() {
    }

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.router = new Router();
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));) {

            final HttpRequest httpRequest = new HttpRequest(inputStream);

            router.route(httpRequest, outputStream, bufferedWriter);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
