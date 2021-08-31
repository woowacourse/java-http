package nextstep.jwp.infrastructure.http.view;

import java.util.Optional;
import nextstep.jwp.infrastructure.http.response.HttpResponse;

public interface View {

    Optional<HttpResponse> httpResponse(final String defaultPath);
}
