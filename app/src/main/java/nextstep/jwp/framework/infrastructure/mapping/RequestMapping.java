package nextstep.jwp.framework.infrastructure.mapping;

import nextstep.jwp.framework.infrastructure.adapter.RequestAdapter;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;

public interface RequestMapping {

    RequestAdapter findAdapter(HttpRequest httpRequest);
}
