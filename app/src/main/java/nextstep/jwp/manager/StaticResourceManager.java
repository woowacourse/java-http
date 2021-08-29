package nextstep.jwp.manager;

import nextstep.jwp.request.ClientRequest;
import nextstep.jwp.request.HttpMethod;
import nextstep.jwp.response.ServerResponse;
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

import static nextstep.jwp.response.HttpStatusCode.*;

public class StaticResourceManager {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceManager.class);

    private static final String STATIC_FILE_PATH = "static";
    private static final String NOT_FOUND_FILE_PATH = "static/404.html";
    private static final String REDIRECT_INDICATOR = "redirect: ";

    private final Map<ClientRequest, File> staticResources = new HashMap<>();
    private final File notFoundFile;

    public StaticResourceManager() {
        log.info("-------loading static resources-------");
        final ClassLoader classLoader = getClass().getClassLoader();
        this.notFoundFile = new File(Objects.requireNonNull(classLoader.getResource(NOT_FOUND_FILE_PATH)).getFile());

        try {
            loadStaticResources(classLoader);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
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
            final String requestUrl = staticFileName.replaceAll("\\\\+","/");
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

    public void handleDynamicResult(String result, OutputStream outputStream) throws IOException {
        if (result.contains(REDIRECT_INDICATOR)) {
            handleRedirect(result, outputStream);
        }
        handlePath(result, outputStream);
    }

    private void handleRedirect(String result, OutputStream outputStream) throws IOException {
        result = result.replace(REDIRECT_INDICATOR, "");
        final ClientRequest resultRequest = ClientRequest.of(HttpMethod.GET, result);
        if (canHandle(resultRequest)) {
            ServerResponse.response(staticResources.get(resultRequest), FOUND, outputStream);
        }
    }

    private void handlePath(String result, OutputStream outputStream) throws IOException {
        final ClientRequest resultRequest = ClientRequest.of(HttpMethod.GET, result);
        if (canHandle(resultRequest)) {
            ServerResponse.response(staticResources.get(resultRequest), OK, outputStream);
        }
    }
}
