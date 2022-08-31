package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.ContentType;
import nextstep.jwp.util.FileNameUtil;
import nextstep.jwp.util.ResourcesUtil;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String BLANK = " ";
    private static final int REQUEST_LINE_COUNT = 3;
    private static final int REQUEST_LINE_URI_INDEX = 1;
    private static final String URI_QUERY_PARAM_DELIMITER = "?";
    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String QUERY_PARAM_VALUE_DELIMITER = "=";
    private static final String EMPTY_QUERY_PARAMETER = "";
    private static final String DEFAULT_STATIC_EXTENSION = ".html";
    private static final String STATIC_EXTENTION_DOT = ".";

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
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line = reader.readLine();
            checkNullRequest(line);
            String uri = getUriByRequestLine(line);
            String path = getPath(uri);
            String queryParameter = getQueryParameter(uri);

            final Map<String, String> queryParams = getQueryParam(queryParameter);
            log.info("request uri : {}. query param : {}", uri, queryParams);

            final var responseBody = ResourcesUtil.readResource(path, this.getClass());
            final var contentType = ContentType.fromExtension(FileNameUtil.getExtension(path));

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType.getType() + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getPath(final String uri) {
        if (uri.contains(URI_QUERY_PARAM_DELIMITER)) {
            String path = uri.substring(0, uri.lastIndexOf(URI_QUERY_PARAM_DELIMITER));
            return addDefaultExtensionInPath(path);
        }
        return addDefaultExtensionInPath(uri);
    }

    private String addDefaultExtensionInPath(final String path) {
        if (path.contains(STATIC_EXTENTION_DOT)) {
            return path;
        }
        return path + DEFAULT_STATIC_EXTENSION;
    }

    private String getQueryParameter(final String uri) {
        if (uri.contains(URI_QUERY_PARAM_DELIMITER)) {
            return uri.substring(uri.lastIndexOf(URI_QUERY_PARAM_DELIMITER) + 1);
        }
        return EMPTY_QUERY_PARAMETER;
    }

    private void checkNullRequest(final String line) {
        if (line == null) {
            throw new UncheckedServletException("요청이 정상적으로 들어오지 않았습니다.");
        }
    }
    
    private String getUriByRequestLine(final String line) {
        String[] requests = line.split(BLANK);
        if (requests.length != REQUEST_LINE_COUNT) {
            throw new UncheckedServletException("요청의 포맷이 잘못되었습니다.");
        }
        return requests[REQUEST_LINE_URI_INDEX];
    }

    private Map<String, String> getQueryParam(final String queryParameter) {
        Map<String, String> queryParams = new ConcurrentHashMap<>();
        if (queryParameter.equals(EMPTY_QUERY_PARAMETER)) {
            return queryParams;
        }
        String[] queryParamUris = queryParameter.split(QUERY_PARAM_DELIMITER);
        for (String queryParamUri : queryParamUris) {
            String[] param = queryParamUri.split(QUERY_PARAM_VALUE_DELIMITER);
            queryParams.put(param[0], param[1]);
        }
        return queryParams;
    }
}
