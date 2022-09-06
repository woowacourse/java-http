package org.apache.coyote.http11;

import static org.apache.coyote.Constants.CRLF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.db.Cookies;
import nextstep.jwp.db.HttpCookie;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.Servlet;
import servlet.ServletImpl;

public class Http11Processor implements Runnable, Processor {

    private static final Logger LOG = LoggerFactory.getLogger(Http11Processor.class);
    private final Servlet servlet = new ServletImpl();

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String request = readRequest(reader);
            String response = servlet.doService(reader, request);
            logIO(request, response);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void logIO(String request, String response) {
        LOG.info("\n\n###### ------------REQUEST------------ ###### \n\n" +
                request +
                "\n\n###### ------------RESPONSE------------ ###### \n\n" +
                response.substring(0, response.indexOf(CRLF + CRLF)) + "\n\n");
    }

    private String readRequest(final BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line = " ";
        while (!line.isEmpty()) {
            line = reader.readLine();
            lines.add(line);
        }
        return String.join(CRLF, lines);
    }
}
