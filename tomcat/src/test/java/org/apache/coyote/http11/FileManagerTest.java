package org.apache.coyote.http11;

import static org.apache.coyote.http11.FileManager.from;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FileManagerTest {

    @Test
    void location으로_생성한다() {
        // given
        String location = "css/styles.css";

        // when
        FileManager fileManager = from(location);

        // then
        File file = fileManager.file();
        assertThat(file.getName()).isEqualTo("styles.css");
    }

    @Test
    void 존재하지_않는_파일의_location_으로_생성할_경우_예외를_던진다() {
        // given
        String location = "wrong.file";

        // expect
        assertThatThrownBy(() -> from(location))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 파일의 경로입니다.");
    }

    @Test
    void 파일_확장자를_반환한다() {
        // given
        String location = "css/styles.css";
        FileManager fileManager = from(location);

        // when
        String fileExtension = fileManager.extractFileExtension();

        // then
        assertThat(fileExtension).isEqualTo("css");
    }

    @Test
    void file의_내용을_반환한다() {
        // given
        String location = "js/scripts.js";
        FileManager fileManager = from(location);

        // when
        String fileContent = fileManager.fileContent();

        // then
        assertThat(fileContent).isEqualTo(
                "/*!\n"
                        + "    * Start Bootstrap - SB Admin v7.0.2 (https://startbootstrap.com/template/sb-admin)\n"
                        + "    * Copyright 2013-2021 Start Bootstrap\n"
                        + "    * Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-sb-admin/blob/master/LICENSE)\n"
                        + "    */\n"
                        + "    // \n"
                        + "// Scripts\n"
                        + "// \n"
                        + "\n"
                        + "window.addEventListener('DOMContentLoaded', event => {\n"
                        + "\n"
                        + "    // Toggle the side navigation\n"
                        + "    const sidebarToggle = document.body.querySelector('#sidebarToggle');\n"
                        + "    if (sidebarToggle) {\n"
                        + "        // Uncomment Below to persist sidebar toggle between refreshes\n"
                        + "        // if (localStorage.getItem('sb|sidebar-toggle') === 'true') {\n"
                        + "        //     document.body.classList.toggle('sb-sidenav-toggled');\n"
                        + "        // }\n"
                        + "        sidebarToggle.addEventListener('click', event => {\n"
                        + "            event.preventDefault();\n"
                        + "            document.body.classList.toggle('sb-sidenav-toggled');\n"
                        + "            localStorage.setItem('sb|sidebar-toggle', document.body.classList.contains('sb-sidenav-toggled'));\n"
                        + "        });\n"
                        + "    }\n"
                        + "\n"
                        + "});\n");
    }

    @Test
    void file의_내용을_반환할_수_없는_경우_예외를_던진다() {
        // given
        FileManager fileManager = new FileManager(new File("wrong.file"));

        // when
        assertThatThrownBy(fileManager::fileContent)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파일을 불러올 수 없습니다.");
    }
}
