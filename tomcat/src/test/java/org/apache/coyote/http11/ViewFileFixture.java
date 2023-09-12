package org.apache.coyote.http11;

import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.response.HttpResponse;

public class ViewFileFixture {

    public static final String PAGE_LOGIN = FileUtil.getResourceFromViewPath(HttpResponse.create(HttpVersion.HTTP11), "/login");

    public static final String PAGE_INDEX = FileUtil.getResourceFromViewPath(HttpResponse.create(HttpVersion.HTTP11), "/index");

    public static final String PAGE_REGISTER = FileUtil.getResourceFromViewPath(HttpResponse.create(HttpVersion.HTTP11), "/register");

    public static final String PAGE_404 = FileUtil.getResourceFromViewPath(HttpResponse.create(HttpVersion.HTTP11), "/404");

    public static final String PAGE_401 = FileUtil.getResourceFromViewPath(HttpResponse.create(HttpVersion.HTTP11), "/401");
}
