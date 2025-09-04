package org.apache.coyote.http11.constant;

import java.util.Arrays;

public enum HttpVersion {
    HTTP_0_9(0, 0),
    HTTP_1(1, 0),
    HTTP_1_1(1, 1),
    HTTP_2(2, 0),
    HTTP_3(3, 0),
    ;

    final int major;
    final int minor;

    HttpVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public static HttpVersion from(String version) {
        final int delimiterIndex = version.indexOf("/");
        final String versionDetail = version.substring(delimiterIndex + 1);
        final String[] splitVersion = versionDetail.split("\\.");
        final int major = Integer.parseInt(splitVersion[0]);
        final int minor = Integer.parseInt(splitVersion[1]);
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.major == major && httpVersion.minor == minor)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 http 버전입니다."));
    }
}
