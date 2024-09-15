package com.techcourse.controller;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.util.http.HttpMethod;
import org.apache.tomcat.util.http.HttpRequestLine;
import org.apache.tomcat.util.http.HttpVersion;
import org.apache.tomcat.util.http.ResourceURI;
import org.apache.tomcat.util.http.body.HttpBody;
import org.apache.tomcat.util.http.header.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RootControllerTest {

    @DisplayName("메인 페이지를 반환한다.")
    @Test
    void serviceGET() throws Exception {
        RootController controller = new RootController();
        HttpRequest httpRequest = new HttpRequest(new HttpRequestLine(HttpMethod.GET, new ResourceURI("/"), HttpVersion.HTTP11),
                new HttpHeaders(), HttpBody.none());
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP11);

        controller.service(httpRequest, httpResponse);

        String expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: 5564\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "\r\n" +
                StandardRoot.getResource(new ResourceURI("/index.html")).getContent();

        assertThat(httpResponse.buildResponse()).isEqualTo(expected);
    }
}
