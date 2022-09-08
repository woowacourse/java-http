package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.coyote.Processor;
import org.apache.coyote.Servlet;
import org.apache.coyote.config.TomcatConfig;
import org.apache.http.BasicHttpRequest;
import org.apache.http.HttpRequest;
import org.apache.http.info.HttpHeaderName;
import org.reflections.Reflections;
import org.richard.utils.CustomReflectionUtils;
import org.richard.utils.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_TOMCAT_CONFIG_FILE_NAME = "tomcat.yml";
    private static final String WHITE_SPACE = " ";
    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND_MARK = "&";
    private static final String EQUALS_MARK = "=";
    private static final String HEADER_DELIMITER = ": ";
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
                final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
                final var bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8));
        ) {
            final var httpRequest = parseRequest(bufferedReader);
            log.info("request : {}", httpRequest);
            final var supportServlet = findSupportServlet(httpRequest);
            final var httpResponse = supportServlet.doService(httpRequest);
            final var responseHttpMessage = httpResponse.getResponseHttpMessage();
            log.info("response : {}", httpResponse);

            bufferedWriter.write(responseHttpMessage);
            bufferedWriter.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseRequest(final BufferedReader bufferedReader) throws IOException {
        final var startLine = parseStartLine(bufferedReader);
        final var queryParams = parseQueryParameters(startLine.split(WHITE_SPACE)[1]);
        final var headers = parseHeaders(bufferedReader);
        final var body = parseBody(headers.getOrDefault(HttpHeaderName.CONTENT_LENGTH.getName(), "0"), bufferedReader);

        return BasicHttpRequest.of(startLine, queryParams, headers, body);
    }

    private String parseStartLine(final BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine();
    }

    private Map<String, String> parseQueryParameters(final String requestUri) {
        if (!requestUri.contains(QUESTION_MARK)) {
            return new HashMap<>();
        }

        final var queryParameterUri = requestUri.substring(requestUri.indexOf(QUESTION_MARK) + 1);
        final String[] queryParameterPairs = queryParameterUri.split(AMPERSAND_MARK);

        final var result = new HashMap<String, String>();
        for (String queryParameterPair : queryParameterPairs) {
            addParameter(result, queryParameterPair);
        }

        return result;
    }

    private void addParameter(final Map<String, String> queryParameters, final String queryParameterPair) {
        final var splitQueryParameterPair = queryParameterPair.split(EQUALS_MARK);
        final var queryParameterKey = splitQueryParameterPair[0];
        final var queryParameterValue = splitQueryParameterPair[1];

        queryParameters.put(queryParameterKey, queryParameterValue);
    }

    private Map<String, String> parseHeaders(final BufferedReader bufferedReader) throws IOException {
        final var headers = new ArrayList<String>();

        String line;
        while ((line = parseStartLine(bufferedReader)) != null && !line.isBlank()) {
            headers.add(line);
        }

        return headers.stream()
                .map(header -> {
                    final String[] split = header.split(HEADER_DELIMITER);
                    final String headerKey = split[0];
                    final String headerValue = split[1];
                    return Map.entry(headerKey, headerValue);
                })
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private Map<String, String> parseBody(final String contentLength, final BufferedReader bufferedReader)
            throws IOException {
        final var length = Integer.parseInt(contentLength);
        final char[] chars = new char[length];
        final int charsRead = bufferedReader.read(chars, 0, length);

        if (charsRead == -1) {
            return new HashMap<>();
        }

        final var rawBody = URLDecoder.decode(new String(chars, 0, charsRead), UTF_8);
        return parseFormUrlEncodedBody(rawBody);
    }

    private Map<String, String> parseFormUrlEncodedBody(final String rawBody) {
        final var split = rawBody.split(AMPERSAND_MARK);

        return Arrays.stream(split)
                .map(Http11Processor::parseQueryParamPair)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private static Entry<String, String> parseQueryParamPair(final String keyValue) {
        final var split = keyValue.split(EQUALS_MARK);
        var value = "";
        if (split.length > 1) {
            value = split[1];
        }

        return Map.entry(split[0], value);
    }

    private Servlet findSupportServlet(final HttpRequest httpRequest) {
        return servlets.stream()
                .filter(servlet -> servlet.support(httpRequest))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}
