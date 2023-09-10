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

public class ServletResponse {
    private final GeneralHeaders generalHeaders = new GeneralHeaders();
    private final ResponseHeaders responseHeaders = new ResponseHeaders();
    private final EntityHeaders entityHeaders = new EntityHeaders();
    private Status status;
    private String body;

    public ServletResponse() {
        this.status = OK;
    }

    public ServletResponse(final Status status) {
        this.status = status;
    }

    public static ServletResponse internalSeverError() {
        return new ServletResponse(INTERNAL_SERVER_ERROR);
    }

    public static ServletResponse ok() {
        return new ServletResponse(OK);
    }

    public static ServletResponse notFound() {
        return new ServletResponse(NOT_FOUND);
    }

    public static ServletResponse redirect(final String location) {
        return new ServletResponse(FOUND)
                .addContentType(HTML.toString())
                .addLocation(location);
    }

    public static ServletResponse methodNotAllowed() {
        return new ServletResponse(METHOD_NOT_ALLOWED);
    }

    public static ServletResponse staticResource(final StaticResource staticResource) {
        return ok()
                .body(staticResource.getContentBytes())
                .addContentType(staticResource.getContentType());
    }

    public void set(final ServletResponse response) {
        this.generalHeaders.addAll(response.generalHeaders);
        this.responseHeaders.addAll(response.responseHeaders);
        this.entityHeaders.addAll(response.entityHeaders);
        this.status = response.status;
        this.body = response.body;
    }

    public ServletResponse addSetCookie(final String cookie) {
        this.responseHeaders.addSetCookie(cookie);
        return this;
    }

    public ServletResponse addLocation(final String location) {
        this.responseHeaders.addLocation(location);
        return this;
    }

    public ServletResponse addContentType(final String contentType) {
        this.entityHeaders.addContentType(contentType);
        return this;
    }

    public ServletResponse body(final String body) {
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
