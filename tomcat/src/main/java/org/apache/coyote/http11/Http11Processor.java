package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.RegisterAsServlet;
import org.apache.coyote.Servlet;
import org.apache.http.BasicHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String BASE_PACKAGE_FOR_SERVLETS = "spring.servlet";

    private final Socket connection;
    private final List<Servlet> servlets;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.servlets = findAllClassesUsingClassLoader(BASE_PACKAGE_FOR_SERVLETS)
                .stream()
                .filter(classInBasePackage -> classInBasePackage.isAnnotationPresent(RegisterAsServlet.class))
                .map(servletClasses -> (Object) servletClasses)
                .map(servletObjects -> (Servlet) servletObjects)
                .collect(Collectors.toList());
    }

    private static Set<Class> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private static Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
            final var httpMessageRequest = parseRequest(bufferedReader);
            final var httpRequest = BasicHttpRequest.from(httpMessageRequest);
//            final var httpResponse = mainController.doService(httpRequest);
//            final var httpMessageResponse = httpResponse.getResponseHttpMessage();

            bufferedWriter.write("");
            bufferedWriter.flush();
        } catch (IOException | UncheckedServletException e) {
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
}
