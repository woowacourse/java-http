package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.FileNotFoundException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResourceManagerTest {

    @Test
    void 존재하지_않는_자원의_location_으로_생성할_경우_예외를_던진다() {
        // given
        String wrongLocation = "/wrong.file";

        // expect
        assertThatThrownBy(() -> ResourceManager.from(wrongLocation))
                .isInstanceOf(FileNotFoundException.class)
                .hasMessage("존재하지 않는 파일의 경로입니다.");
    }

    @Test
    void 자원_타입을_반환한다() {
        // given
        String location = "css/styles.css";
        ResourceManager manager = ResourceManager.from(location);

        // when
        String resourceType = manager.extractResourceType();

        // then
        assertThat(resourceType).isEqualTo("css");
    }

    @Test
    void resource의_내용을_읽는다() {
        // given
        String location = "js/scripts.js";
        ResourceManager manager = ResourceManager.from(location);

        // when
        String resourceContent = manager.readResourceContent();

        // then
        assertThat(resourceContent).isEqualTo(
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
}
