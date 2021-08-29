package nextstep.jwp.manager;

import nextstep.jwp.request.ClientRequest;
import nextstep.jwp.response.ServerResponse;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static nextstep.jwp.response.HttpStatusCode.*;

public class StaticResourceManager {

    private static final String STATIC = "static";

    private final Map<ClientRequest, File> staticResources = new HashMap<>();

    private File notFound;

    public StaticResourceManager() {
        final ClassLoader classLoader = getClass().getClassLoader();
        this.notFound = new File(Objects.requireNonNull(classLoader.getResource(STATIC + "/404.html")).getFile());

        try {
            setStaticResources(classLoader);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void setStaticResources(ClassLoader classLoader) throws IOException, URISyntaxException {
        final URL url = classLoader.getResource(STATIC);
        final List<File> staticFiles = Files.walk(Paths.get(new URI(url.toString())))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());

        for (File staticFile : staticFiles) {
            final String absolutePath = staticFile.getAbsolutePath();
            final int staticIndex = absolutePath.indexOf(STATIC);
            final String requestUrl = absolutePath.substring(staticIndex + STATIC.length()).replaceAll("\\\\", "/");
            staticResources.put(ClientRequest.of("GET", requestUrl), staticFile);
        }
    }

    public boolean canHandle(ClientRequest clientRequest) {
        return staticResources.containsKey(clientRequest);
    }

    public void handle(ClientRequest clientRequest, OutputStream outputStream) throws IOException {
        ServerResponse.response(staticResources.get(clientRequest), OK, outputStream);
    }

    public void handleNotFound(OutputStream outputStream) throws IOException {
        ServerResponse.response(this.notFound, NOT_FOUND, outputStream);
    }

    public void handleDynamicResult(String result, OutputStream outputStream) throws IOException {
        if (result.contains("redirect: ")) {
            handleRedirect(result, outputStream);
        }
        handlePath(result, outputStream);
    }

    private void handleRedirect(String result, OutputStream outputStream) throws IOException {
        result = result.replace("redirect :", "");
        final ClientRequest resultRequest = ClientRequest.of("GET", result);
        if (canHandle(resultRequest)) {
            ServerResponse.response(staticResources.get(resultRequest), FOUND, outputStream);
        }
    }

    private void handlePath(String result, OutputStream outputStream) throws IOException {
        final ClientRequest resultRequest = ClientRequest.of("GET", result);
        if (canHandle(resultRequest)) {
            ServerResponse.response(staticResources.get(resultRequest), OK, outputStream);
        }
    }
}
