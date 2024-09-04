package org.apache.catalina.mapper;

import java.net.URL;

/**
 * 요청의 URL을 실제 정적 파일 절대 경로로 매핑해준다.
 */
public class Mapper {

    private static final String STATIC_PATH = "static";

    private Mapper() {}

    public static URL mapToFileURL(String path) { // login.html, login, login.css
        if (path.contains(".")) {
            return Mapper.class.getClassLoader().getResource(STATIC_PATH + path);
        }

        // FIXME: css 내부에 있는 값은 html 확장자를 가질 일이 없음.
        return Mapper.class.getClassLoader().getResource(STATIC_PATH + path + ".html");
    }
}
