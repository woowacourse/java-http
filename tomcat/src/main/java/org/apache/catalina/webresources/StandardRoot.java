package org.apache.catalina.webresources;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import org.apache.tomcat.util.http.ResourceURI;

import static org.apache.catalina.webresources.FileResource.NOT_FOUND_RESOURCE_URI;

public class StandardRoot {

    private StandardRoot() {
    }

    public static WebResource getResource(ResourceURI resourceURI) {
        URL resourcePath = getResourcePath(resourceURI.uri());
        if (Objects.isNull(resourcePath)) {
            URL notFoundResourcePath = getResourcePath(NOT_FOUND_RESOURCE_URI.uri());
            File file = new File(notFoundResourcePath.getFile());
            return new FileResource(file);
        }
        File file = new File(resourcePath.getFile());
        return new FileResource(file);
    }

    private static URL getResourcePath(String uri) {
        return StandardRoot.class
                .getClassLoader()
                .getResource("static" + uri);
    }
}
