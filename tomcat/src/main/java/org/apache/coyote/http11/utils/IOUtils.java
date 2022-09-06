package org.apache.coyote.http11.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.coyote.http11.url.Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtils {
    private static final Logger log = LoggerFactory.getLogger(IOUtils.class);
    private static final String STATIC_DIRECTORY = "static";

    public static String readResourceFile(String url) {
        try {
            String path = UrlParser.convertEmptyToHtml(url);
            log.info("정적 파일 읽기 url : {}", url);
            URL resource = IOUtils.class
                    .getClassLoader()
                    .getResource(STATIC_DIRECTORY + path);
            validatePath(resource);
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("파일을 읽어오는데 실패했습니다 : " + url);
        }

    }

    private static void validatePath(URL resource) {
        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException("경로가 잘못 되었습니다. : null");
        }
    }
}
