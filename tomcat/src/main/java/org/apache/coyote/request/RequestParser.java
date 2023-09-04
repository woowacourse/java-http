package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.coyote.response.Resource;
import org.apache.coyote.response.ResourceType;

public class RequestParser {

    private static final String HOME_URL = "/";
    private static final String DEFAULT_RESOURCE_PATH = "/static";
    private static final String INDEX_HTML_FILE_NAME = "/index.html";
    private static final String ACCEPT_HEADER = "Accept: ";
    private static final String CONTENT_TYPE = "Content-Type: ";
    private static final String SPLIT_HEADER_DELIMITER = " ";
    private static final String FINISH_SPLIT_DELIMITER = ";";
    private static final String SPLIT_VALUE_DELIMITER = ",";

    private static final int RESOURCE_INDEX = 1;
    private static final int FINISH_RESOURCE_INDEX = 0;

    private final BufferedReader bufferedReader;

    public RequestParser(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        this.bufferedReader = new BufferedReader(inputStreamReader);
    }

    public Resource getResource() throws IOException {
        String resourceUrl = getLines(bufferedReader.readLine());
        List<ResourceType> resourceType = getResourceType();

        if (Objects.equals(resourceUrl, HOME_URL)) {
            return Resource.of(getClass().getResource(DEFAULT_RESOURCE_PATH + INDEX_HTML_FILE_NAME), resourceType);
        }
        return Resource.of(getClass().getResource(DEFAULT_RESOURCE_PATH + resourceUrl), resourceType);
    }

    private String getLines(String message) {
        return message.split(SPLIT_HEADER_DELIMITER)[RESOURCE_INDEX]
                .split(FINISH_SPLIT_DELIMITER)[FINISH_RESOURCE_INDEX];
    }

    private List<ResourceType> getResourceType() throws IOException {
        String header;
        while ((header = bufferedReader.readLine()) != null) {
            if (header.startsWith(ACCEPT_HEADER) || header.startsWith(CONTENT_TYPE)) {
                String resourceType = getLines(header);
                return Arrays.stream(resourceType.split(SPLIT_VALUE_DELIMITER))
                        .map(ResourceType::findResourceType)
                        .collect(Collectors.toList());

            }
        }
        return List.of(ResourceType.HTML);
    }
}
