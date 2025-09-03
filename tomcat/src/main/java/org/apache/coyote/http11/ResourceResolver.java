package org.apache.coyote.http11;

public class ResourceResolver {
    
    public static final String STATIC_RESOURCE_PREFIX = "static";
    public static final String HEADER_DELIMITER = " ";

    public static String resolveResourceName(String requestLine) {
        String[] splitRequestLine = requestLine.split(HEADER_DELIMITER);
        String requestUri = splitRequestLine[1];
        String resource = QueryStringParser.parseAndProcessQuery(requestUri);
        return STATIC_RESOURCE_PREFIX + resource;
    }
}
