package org.apache.coyote.http11.dispatcher.handlerAdapter;

import java.net.URL;

public class ViewResolver {
    // viewName으로 resources 하위에 있는 파일들 다 탐색
    // ex: css/styles.css 가 들어오면, /static/css/styles.css를 찾으면 연결하기
    //    - root path 가 static/ 이렇게 되어 있을 것

    private static final String RESOURCE_ROOT_PATH = "static/";

    public static URL resolve(String resourcePath) {
        String resourceFullPath = RESOURCE_ROOT_PATH + resourcePath;
        return ViewResolver.class.getClassLoader().getResource(resourceFullPath);
    }
}
