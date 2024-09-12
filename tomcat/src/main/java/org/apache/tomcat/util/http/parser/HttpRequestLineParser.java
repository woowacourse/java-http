package org.apache.tomcat.util.http.parser;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.tomcat.util.http.HttpMethod;
import org.apache.tomcat.util.http.HttpRequestLine;
import org.apache.tomcat.util.http.HttpVersion;
import org.apache.tomcat.util.http.ResourceURI;

public class HttpRequestLineParser {
    private static final String DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int RESOURCE_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private HttpRequestLineParser() {
    }

    protected static HttpRequestLine parse(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        String[] requestLineElements = line.split(DELIMITER);
        HttpMethod httpMethod = HttpMethod.of(requestLineElements[HTTP_METHOD_INDEX]);
        ResourceURI resourceURI = new ResourceURI(requestLineElements[RESOURCE_URI_INDEX]);
        HttpVersion httpVersion = HttpVersion.of(requestLineElements[HTTP_VERSION_INDEX]);
        return new HttpRequestLine(httpMethod, resourceURI, httpVersion);
    }
}
