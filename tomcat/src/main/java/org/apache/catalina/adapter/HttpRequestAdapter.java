package org.apache.catalina.adapter;

import static org.apache.coyote.HttpStatus.BAD_REQUEST;
import static org.apache.coyote.HttpStatus.METHOD_NOT_ALLOWED;

import com.techcourse.exception.UnauthorizedException;
import java.util.NoSuchElementException;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.mapper.RequestMapping;
import org.apache.catalina.request.ServletRequest;
import org.apache.catalina.response.ServletResponse;
import org.apache.coyote.HttpHeaderName;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestAdapter {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestAdapter.class);

    private final RequestMapping requestMapping;

    public HttpRequestAdapter() {
        this.requestMapping = new RequestMapping();
    }

    public HttpResponse service(HttpRequest httpRequest, String protocol) {
        final ServletRequest request = new ServletRequest(httpRequest);
        final ServletResponse response = new ServletResponse(protocol);

        try {
            final Controller controller = requestMapping.getControllerOrDefault(request);
            controller.service(request, response);
            checkSessionCreated(request, response);
        } catch (IllegalArgumentException e) {
            updateResponseWithError(BAD_REQUEST, response, e);
            log.warn("잘못된 요청 형식: {}", e.getMessage());
        } catch (UnauthorizedException e) {
            response.sendRedirect("401.html");
            log.warn(e.getMessage());
        } catch (NoSuchElementException e) {
            response.sendRedirect("404.html");
            log.warn("존재하지 않는 리소스: {}", e.getMessage());
        } catch (UnsupportedOperationException e) {
            updateResponseWithError(METHOD_NOT_ALLOWED, response, e);
            log.warn("지원하지 않는 HTTP 메서드: {}", e.getMessage());
        } catch (Exception e) {
            response.sendRedirect("500.html");
            log.error("예상치 못한 서버 오류 발생", e);
        }

        return response.toHttpResponse();
    }

    private void updateResponseWithError(HttpStatus status, ServletResponse response, Exception e) {
        response.setStatus(status);
        response.setBody(e.getMessage());
        response.setHeader(HttpHeaderName.CONTENT_TYPE.getValue(), "text/plain;charset=utf-8");
    }

    private void checkSessionCreated(ServletRequest request, ServletResponse response) {
        if (request.isSessionCreated()) {
            response.setCookie("JSESSIONID", request.getSession(false).getId());
        }
    }
}
