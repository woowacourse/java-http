package org.qupring.mvc;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationScanner {

    public List<Class<?>> scanForControllers(Class<?> applicationClass) {
        // TODO : 루트 애플리케이션 클래스패스를 받아오고 그 밑으로 스캔 진행
        return List.of();
    }

    /*
     * static 폴더 안의 HTML 파일을 스캔하여
     * 요청 경로와 클래스패스 리소스 경로를 반환한다.
     */
    public Map<String, String> scanForResources() {
        ClassLoader classLoader =
                Thread.currentThread().getContextClassLoader();

        URL resourceUrl = classLoader.getResource("static");

        if (resourceUrl == null) {
            throw new IllegalStateException("static 폴더를 찾을 수 없습니다.");
        }

        File rootFolder = new File(resourceUrl.getFile());

        return getFileMap(rootFolder);
    }

    private Map<String, String> getFileMap(
            File rootFolder
    ) {
        Map<String, String> resources = new HashMap<>();

        scanFiles(rootFolder, rootFolder, resources);

        return resources;
    }

    private void scanFiles(
            File rootFolder,
            File currentFolder,
            Map<String, String> resources
    ) {
        File[] files = currentFolder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanFiles(rootFolder, file, resources);
                continue;
            }

            String relativePath = rootFolder.toPath()
                    .relativize(file.toPath())
                    .toString()
                    .replace(File.separatorChar, '/');

            resources.put(
                    "/" + relativePath,
                    "static/" + relativePath
            );

            String nonHtmlRelativePath = relativePath.replace(".html", "");

            resources.put(
                    "/" + nonHtmlRelativePath,
                    "static/" + relativePath
            );
        }
    }

}
