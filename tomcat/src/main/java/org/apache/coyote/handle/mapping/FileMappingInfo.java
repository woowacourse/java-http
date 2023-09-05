package org.apache.coyote.handle.mapping;

import java.util.regex.Pattern;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.handle.handler.FileHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class FileMappingInfo extends MappingInfo {

    private static final String FILE_PATTER = "/[^.]*\\.[^.]*$";
    private static final Pattern PATTERN = Pattern.compile(FILE_PATTER);

    public FileMappingInfo() {
        super(FILE_PATTER);
        try {
            methodMapping.put(
                    HttpMethod.GET,
                    FileHandler.class.getDeclaredMethod("doGet", HttpRequest.class, HttpResponse.class)
            );
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return methodMapping.keySet().contains(httpRequest.getHttpMethod())
                && PATTERN.matcher(httpRequest.getUriPath()).matches();
    }
}
