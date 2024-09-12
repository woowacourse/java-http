package org.apache.catalina.connector;

import org.apache.catalina.webresources.StandardRoot;
import org.apache.catalina.webresources.WebResource;
import org.apache.tomcat.util.http.HttpStatus;
import org.apache.tomcat.util.http.HttpStatusLine;
import org.apache.tomcat.util.http.HttpVersion;
import org.apache.tomcat.util.http.ResourceURI;
import org.apache.tomcat.util.http.body.HttpBody;
import org.apache.tomcat.util.http.header.HttpContentType;
import org.apache.tomcat.util.http.header.HttpHeaderType;
import org.apache.tomcat.util.http.header.HttpHeaders;

public class HttpResponse {

    public static final String NEW_LINE = "\r\n";
    private final HttpStatusLine httpStatusLine;
    private final HttpHeaders httpHeaders;
    private HttpBody httpBody;

    public HttpResponse(HttpVersion httpVersion) {
        this.httpStatusLine = new HttpStatusLine(httpVersion);
        this.httpHeaders = new HttpHeaders();
    }

    private void addHttpStatus(HttpStatus httpStatus) {
        httpStatusLine.setStatus(httpStatus);
    }

    private void setBody(String body) {
        this.httpBody = new HttpBody(body);
        addHeader(HttpHeaderType.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }

    public void addHeader(HttpHeaderType header, String headerValue) {
        if (header.equals(HttpHeaderType.CONTENT_TYPE)) {
            headerValue = HttpContentType.encoding(headerValue);
        }
        httpHeaders.put(header, headerValue);
    }

    public String buildResponse() {
        return httpStatusLine.buildResponse() +
                httpHeaders.buildResponse() +
                NEW_LINE + httpBody.buildResponse();
    }

    public void sendRedirect(ResourceURI redirectResourceURI) {
        WebResource webResource = StandardRoot.getResource(redirectResourceURI);
        this.addHttpStatus(HttpStatus.FOUND);
        this.addHeader(HttpHeaderType.CONTENT_TYPE, webResource.getContentType());
        this.addHeader(HttpHeaderType.LOCATION, redirectResourceURI.uri());
        this.setBody(webResource.getContent());
    }

    public void writeStaticResource(ResourceURI resourceURI) {
        WebResource webResource = StandardRoot.getResource(resourceURI);
        this.addHttpStatus(HttpStatus.OK);
        this.addHeader(HttpHeaderType.CONTENT_TYPE, webResource.getContentType());
        this.setBody(webResource.getContent());
    }
}
