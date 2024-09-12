package org.apache.tomcat.util.http;

public record HttpRequestLine(HttpMethod httpMethod, ResourceURI resourceURI, HttpVersion httpVersion) {
}
