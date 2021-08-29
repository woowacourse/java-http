package nextstep.jwp.resolver;

import java.io.IOException;
import java.util.List;
import nextstep.jwp.http.response.HttpResponseBody;

public interface DataResolver {

    boolean isExist(String url);

    boolean isSuitable(List<String> acceptTypes);

    HttpResponseBody resolve(String url) throws IOException;
}