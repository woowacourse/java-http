package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.Processor;
import org.apache.coyote.Servlet;
import org.apache.coyote.config.TomcatConfig;
import org.apache.http.BasicHttpRequest;
import org.apache.http.HttpRequest;
import org.reflections.Reflections;
import org.richard.utils.CustomReflectionUtils;
import org.richard.utils.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_TOMCAT_CONFIG_FILE_NAME = "tomcat.yml";
    private static final List<Servlet> servlets;

    static {
        final var config
                = YamlUtils.readPropertyAsObject(DEFAULT_TOMCAT_CONFIG_FILE_NAME, TomcatConfig.class);
        final var basePackage = config.getServletBasePackage();

        servlets = new Reflections(basePackage)
                .getSubTypesOf(Servlet.class)
                .stream()
                .map(CustomReflectionUtils::newInstance)
                .collect(Collectors.toList());
    }

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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream();
                final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                final var bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        ) {
            final var requestHttpMessage = parseRequest(bufferedReader);
            final var httpRequest = BasicHttpRequest.from(requestHttpMessage);
            final var supportServlet = findSupportServlet(httpRequest);
            final var httpResponse = supportServlet.doService(httpRequest);
            final var responseHttpMessage = httpResponse.getResponseHttpMessage();

            bufferedWriter.write(responseHttpMessage);
            bufferedWriter.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequest(final BufferedReader bufferedReader) throws IOException {
        final var request = new StringBuilder();

        while (bufferedReader.ready()) {
            request.append(String.format("%s%s", bufferedReader.readLine(), System.lineSeparator()));
        }

        return request.toString();
    }

    private Servlet findSupportServlet(final HttpRequest httpRequest) {
        return servlets.stream()
                .filter(servlet -> servlet.support(httpRequest))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}
