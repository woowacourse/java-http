package org.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.common.request.parser.RequestLineParser;
import org.apache.coyote.http11.StaticResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceGenerator {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceHandler.class);
    private static final String CANNOT_FIND_STATIC_RESOURCE = "Could not find static resource";

    public static String getStaticResource(final String requestUrl) {
        try {
            final String resourcePath = RequestLineParser.getStaticResourcePath(requestUrl);

            final URL url = Thread.currentThread()
                    .getContextClassLoader()
                    .getResource(resourcePath);
            if (url == null) {
                throw new IllegalArgumentException(CANNOT_FIND_STATIC_RESOURCE);
            }
            return new String(Files.readAllBytes(new File(url.getFile()).toPath()));
        } catch (IOException | NullPointerException e ) {
            log.error(e.getMessage(), e);
        }
        return "";
    }
}
