package nextstep.jwp.staticresource;

import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.response.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static nextstep.jwp.http.response.ContentType.ICO;

public class StaticResourceFinder {

    private static final Logger LOG = LoggerFactory.getLogger(StaticResourceFinder.class);
    private static final String NEW_LINE = System.getProperty("line.separator");

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
        return getStaticResource(fileNameExtension, url);
    }

    private String getFileNameExtension(URL url) {
        final String[] splitFileUrl = url.toString().split("\\.");
        return splitFileUrl[splitFileUrl.length - 1];
    }

    public static StaticResource getStaticResource(String fileNameExtension, URL url) throws URISyntaxException, IOException {
        final Path filePath = Paths.get(url.toURI());
        final ContentType contentType = ContentType.getContentTypeByFileNameExtension(fileNameExtension);
        if (ICO == contentType) {
            return getICOStaticResource(filePath, contentType);
        }
        return getStaticResourceExceptICO(filePath, contentType);
    }

    private static StaticResource getICOStaticResource(Path filePath, ContentType contentType) {
        final String content = new File(filePath.toUri()).toString();
        return new StaticResource(contentType, content);
    }

    private static StaticResource getStaticResourceExceptICO(Path filePath, ContentType contentType) throws IOException {
        final List<String> fileLines = Files.readAllLines(filePath);
        final String content = String.join(NEW_LINE, fileLines);
        return new StaticResource(contentType, content);
    }
}
