package nextstep.jwp.staticresource;

import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.response.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class StaticResourceFinder {

    private static final Logger LOG = LoggerFactory.getLogger(StaticResourceFinder.class);

    public StaticResource findStaticResource(String resourcePath) {
        final URL url = getClass().getClassLoader().getResource("static" + resourcePath);
        validateUrl(url);
        try {
            return getParsedStaticResource(url);
        } catch (URISyntaxException | IOException e) {
            throw new IllegalStateException("정적 리소스 파싱을 실패했습니다.");
        }
    }

    private void validateUrl(URL url) {
        if (url == null) {
            throw new NotFoundException("resourcePath로 찾은 url의 값이 null 입니다.");
        }
    }

    private StaticResource getParsedStaticResource(URL url) throws URISyntaxException, IOException {
        final String fileNameExtension = getFileNameExtension(url);
        LOG.debug("파일 확장자 : {}", fileNameExtension);
        return ContentType.getStaticResource(fileNameExtension, url);
    }

    private String getFileNameExtension(URL url) {
        final String[] splitFileUrl = url.toString().split("\\.");
        return splitFileUrl[splitFileUrl.length - 1];
    }
}
