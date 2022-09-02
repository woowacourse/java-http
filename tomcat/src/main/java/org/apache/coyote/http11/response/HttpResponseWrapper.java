package org.apache.coyote.http11.response;

import static org.apache.coyote.Constants.CRLF;
import static org.apache.coyote.Constants.CSS;
import static org.apache.coyote.Constants.HTML;
import static org.apache.coyote.Constants.IMG;
import static org.apache.coyote.Constants.JS;
import static org.apache.coyote.Constants.ROOT;
import static org.apache.coyote.http11.response.element.HttpMethod.GET;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NoUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequestWrapper;
import org.apache.coyote.http11.response.element.HttpMethod;
import org.apache.coyote.http11.response.element.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseWrapper {

    private static final Logger log = LoggerFactory.getLogger(HttpResponseWrapper.class);

    private HttpResponseHeader header;
    private HttpResponseBody body;


    public HttpResponseWrapper(HttpRequestWrapper request) {
        try {
            parseResponse(request);
        } catch (NoSuchElementException e) {
            log.error(e.getMessage(), e);
            setResponse(HttpResponseBody.of(ROOT + "/404.html"), HttpStatus.NOT_FOUND, "text/html");
        } catch (NoUserException e) {
            log.error(e.getMessage(), e);
            setResponse(HttpResponseBody.of(ROOT + "/401.html"), HttpStatus.UNAUTHORIZED, "text/html");
        }
    }

    private void parseResponse(HttpRequestWrapper request) {
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        String path = request.getPath();

        saveBody(method == GET && List.of("/", "").contains(path), "text/html", "Hello world!");
        saveBodyIfLogin(method, path);

        saveBodyOfPath(method == GET && HTML.contains(path), "text/html", path);
        saveBodyOfPath(method == GET && JS.contains(path), "text/plain", path);
        saveBodyOfPath(method == GET && CSS.contains(path), "text/css", path);
        saveBodyOfPath(method == GET && IMG.contains(path), "image/svg+xml", path);

        if (header == null || body == null) {
            throw new NoSuchElementException("해당 페이지를 찾을 수 없습니다: " + path);
        }
    }

    private void saveBody(boolean condition, String contentType, String responseBody) {
        if (condition) {
            setResponse(new HttpResponseBody(responseBody), HttpStatus.OK, contentType);
        }
    }

    private void saveBodyOfPath(boolean condition, String contentType, String filePath) {
        if (condition) {
            setResponse(HttpResponseBody.of(ROOT + filePath), HttpStatus.OK, contentType);
        }
    }

    private void setResponse(HttpResponseBody responseBody, HttpStatus status, String contentType) {
        body = responseBody;
        header = new HttpResponseHeader(status.getValue())
                .addContentType(contentType)
                .addContentLength(body.getContentLength());
    }

    private void saveBodyIfLogin(HttpMethod method, String uri) {
        if (method != GET || !uri.split("\\?")[0].equals("/login")) {
            return;
        }
        setResponse(HttpResponseBody.of(ROOT + "/login.html"), HttpStatus.OK, "text/html");

        int index = uri.indexOf("?");
        if (index == -1) {
            return;
        }
        String queryString = uri.substring(index + 1);

        Map<String, String> queryMap = mapQuery(queryString);
        User user = InMemoryUserRepository.findByAccount(queryMap.get("account"))
                .orElseThrow(() -> new NoUserException("해당 유저가 없습니다."));
        log.info("userLogin: " + CRLF + user.toString() + CRLF);
    }

    private Map<String, String> mapQuery(String queryString) {
        Map<String, String> result = new HashMap<>();
        String[] query = queryString.split("&");
        for (String element : query) {
            String[] split = element.split("=");
            result.put(split[0], split[1]);
        }
        return result;
    }

    public String getHeader() {
        return header.getHeaders();
    }

    public String getResponse() {
        return String.join(CRLF, getHeader(), "", body.getBodyContext());
    }
}
