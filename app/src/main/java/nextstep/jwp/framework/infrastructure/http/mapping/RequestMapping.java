package nextstep.jwp.framework.infrastructure.http.mapping;

import nextstep.jwp.framework.infrastructure.http.adapter.RequestAdapter;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;

public interface RequestMapping {

    RequestAdapter findAdapter(HttpRequest httpRequest);
}
