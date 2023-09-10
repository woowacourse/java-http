package nextstep.jwp.handle.mapping;

import java.util.regex.Pattern;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.request.HttpRequest;

public class GetFileMappingInfo extends MappingInfo {

    private static final String FILE_PATTER = "/[^.]*\\.[^.]*$";
    private static final Pattern PATTERN = Pattern.compile(FILE_PATTER);

    public GetFileMappingInfo() {
        super(HttpMethod.GET, FILE_PATTER);
    }

    @Override
    public boolean support(final HttpRequest request) {
        return httpMethod.equals(request.getHttpMethod()) && PATTERN.matcher(request.getUriPath()).matches();
    }
}
