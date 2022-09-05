package org.apache.catalina;

import nextstep.jwp.presentation.filter.LoginFilter;
import nextstep.jwp.presentation.resolver.ViewResolver;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

public class ChicChocServlet {

    private final ViewResolver viewResolver;
    private final LoginFilter loginFilter;

    public ChicChocServlet() {
        this.loginFilter = new LoginFilter();
        this.viewResolver = new ViewResolver();
    }

    public void doService(final Request request, final Response response) {
        loginFilter.doFilter(request);
        viewResolver.resolve(request, response);
    }
}
