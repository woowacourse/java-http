package org.apache.catalina.core.servlet;

import static org.apache.coyote.http11.common.MimeType.HTML;
import static org.apache.coyote.http11.common.Status.FOUND;
import static org.apache.coyote.http11.common.Status.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.http11.common.Status.METHOD_NOT_ALLOWED;
import static org.apache.coyote.http11.common.Status.NOT_FOUND;
import static org.apache.coyote.http11.common.Status.OK;

import org.apache.catalina.core.util.StaticResource;
import org.apache.coyote.http11.common.Status;
import org.apache.coyote.http11.common.header.EntityHeaders;
import org.apache.coyote.http11.common.header.GeneralHeaders;
import org.apache.coyote.http11.common.header.ResponseHeaders;
import org.apache.coyote.http11.response.Response;

public class HttpServletResponse {
    private final GeneralHeaders generalHeaders = new GeneralHeaders();
    private final ResponseHeaders responseHeaders = new ResponseHeaders();
    private final EntityHeaders entityHeaders = new EntityHeaders();
    private Status status;
    private String body;

    public HttpServletResponse() {
        this.status = OK;
    }

    public HttpServletResponse(final Status status) {
        this.status = status;
    }

    public static HttpServletResponse internalSeverError() {
        return new HttpServletResponse(INTERNAL_SERVER_ERROR);
    }

    public static HttpServletResponse ok() {
        return new HttpServletResponse(OK);
    }

    public static HttpServletResponse notFound() {
        return new HttpServletResponse(NOT_FOUND);
    }

    public static HttpServletResponse redirect(final String location) {
        return new HttpServletResponse(FOUND)
                .addContentType(HTML.toString())
                .addLocation(location);
    }

    public static HttpServletResponse methodNotAllowed() {
        return new HttpServletResponse(METHOD_NOT_ALLOWED);
    }

    public static HttpServletResponse staticResource(final StaticResource staticResource) {
        return ok()
                .body(staticResource.getContentBytes())
                .addContentType(staticResource.getContentType());
    }

    public void set(final HttpServletResponse response) {
        this.generalHeaders.addAll(response.generalHeaders);
        this.responseHeaders.addAll(response.responseHeaders);
        this.entityHeaders.addAll(response.entityHeaders);
        this.status = response.status;
        this.body = response.body;
    }

    public HttpServletResponse addSetCookie(final String cookie) {
        this.responseHeaders.addSetCookie(cookie);
        return this;
    }

    public HttpServletResponse addLocation(final String location) {
        this.responseHeaders.addLocation(location);
        return this;
    }

    public HttpServletResponse addContentType(final String contentType) {
        this.entityHeaders.addContentType(contentType);
        return this;
    }

    public HttpServletResponse body(final String body) {
        this.body = body;
        this.entityHeaders.addContentLength(body);
        return this;
    }

    public Response build() {
        if (body == null) {
            body = "";
        }
        return new Response(this);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public String getLocation() {
        return responseHeaders.getLocation();
    }

    public GeneralHeaders getGeneralHeaders() {
        return new GeneralHeaders(generalHeaders.values());
    }

    public ResponseHeaders getResponseHeaders() {
        return new ResponseHeaders(responseHeaders.values());
    }

    public EntityHeaders getEntityHeaders() {
        return new EntityHeaders(entityHeaders.values());
    }

}
