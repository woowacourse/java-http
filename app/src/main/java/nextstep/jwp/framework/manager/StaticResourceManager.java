package nextstep.jwp.framework.manager;

import nextstep.jwp.framework.request.ClientRequest;
import nextstep.jwp.framework.request.HttpMethod;
import nextstep.jwp.framework.response.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.jwp.framework.response.HttpStatusCode.*;

public class StaticResourceManager {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceManager.class);

    private static final String STATIC_FILE_PATH = "static";
    private static final String NOT_FOUND_FILE_PATH = "static/404.html";
    private static final String INTERNAL_SERVER_ERROR_FILE_PATH = "static/500.html";
    private static final String REDIRECT_INDICATOR = "redirect: ";

    private final Map<ClientRequest, File> staticResources = new HashMap<>();
    private final File notFoundFile;
    private final File internalServerErrorFile;

    public StaticResourceManager() {
        log.info("-------loading static resources-------");
        final ClassLoader classLoader = getClass().getClassLoader();
        this.notFoundFile = new File(Objects.requireNonNull(classLoader.getResource(NOT_FOUND_FILE_PATH)).getFile());
        this.internalServerErrorFile = new File(Objects.requireNonNull(classLoader.getResource(INTERNAL_SERVER_ERROR_FILE_PATH)).getFile());

        try {
            loadStaticResources(classLoader);
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("StaticResourceManager 생성 실패!");
        }
        log.info("-------all static resources loaded-------");
    }

    private void loadStaticResources(ClassLoader classLoader) throws IOException, URISyntaxException {
        final Path staticFilePath = Paths.get(new URI(Objects.requireNonNull(classLoader.getResource(STATIC_FILE_PATH)).toString()));

        final Stream<Path> staticFilePathStream = Files.walk(staticFilePath);
        final List<File> staticFiles = staticFilePathStream
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());

        cacheStaticFiles(staticFiles);
        staticFilePathStream.close();
    }

    private void cacheStaticFiles(List<File> staticFiles) {
        for (File staticFile : staticFiles) {
            final String absolutePath = staticFile.getAbsolutePath();
            final int staticFileNameIndex = absolutePath.indexOf(STATIC_FILE_PATH);
            final String staticFileName = absolutePath.substring(staticFileNameIndex + STATIC_FILE_PATH.length());
            final String requestUrl = staticFileName.replaceAll("\\\\+", "/");
            staticResources.put(ClientRequest.of(HttpMethod.GET, requestUrl), staticFile);
        }
    }

    public boolean canHandle(ClientRequest clientRequest) {
        return staticResources.containsKey(clientRequest);
    }

    public void handle(ClientRequest clientRequest, OutputStream outputStream) throws IOException {
        ServerResponse.response(staticResources.get(clientRequest), OK, outputStream);
    }

    public void handleNotFound(OutputStream outputStream) throws IOException {
        ServerResponse.response(this.notFoundFile, NOT_FOUND, outputStream);
    }

    public void handleInternalServerError(OutputStream outputStream) throws IOException {
        ServerResponse.response(this.internalServerErrorFile, INTERNAL_SERVER_ERROR, outputStream);
    }

    public void handleDynamicResult(String result, OutputStream outputStream) throws IOException {
        if (result.contains(REDIRECT_INDICATOR)) {
            handleRedirect(result, outputStream);
            return;
        }
        handlePath(result, outputStream);
    }

    private void handleRedirect(String result, OutputStream outputStream) throws IOException {
        result = result.replace(REDIRECT_INDICATOR, "");
        final ClientRequest resultRequest = ClientRequest.of(HttpMethod.GET, result);
        if (canHandle(resultRequest)) {
            ServerResponse.response(staticResources.get(resultRequest), FOUND, outputStream);
            return;
        }
        throw new IllegalArgumentException("컨트롤러에서 반환한 값에 해당하는 리다이렉트 정적 자원이 없습니다.");
    }

    private void handlePath(String result, OutputStream outputStream) throws IOException {
        final ClientRequest resultRequest = ClientRequest.of(HttpMethod.GET, result);
        if (canHandle(resultRequest)) {
            ServerResponse.response(staticResources.get(resultRequest), OK, outputStream);
            return;
        }
        throw new IllegalArgumentException("컨트롤러에서 반환한 값에 해당하는 정적 자원이 없습니다.");
    }
}
