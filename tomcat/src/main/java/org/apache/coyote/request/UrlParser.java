package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

public class UrlParser {

    private static final String DEFAULT_RESOURCE_PATH = "/static";
    private static final String INDEX_HTML_FILE_NAME = "/index.html";
    private static final String SPLIT_DELIMITER = " ";
    private static final int RESOURCE_INDEX = 1;

    private final BufferedReader bufferedReader;

    public UrlParser(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        this.bufferedReader = new BufferedReader(inputStreamReader);
    }

    public URL getResourceUrl() throws IOException {
        String resourceUrl = bufferedReader.readLine().split(SPLIT_DELIMITER)[RESOURCE_INDEX];
        if (Objects.equals(resourceUrl, "/")) {
            return getClass().getResource(DEFAULT_RESOURCE_PATH + INDEX_HTML_FILE_NAME);
        }
        return getClass().getResource(DEFAULT_RESOURCE_PATH + resourceUrl);
    }
}
