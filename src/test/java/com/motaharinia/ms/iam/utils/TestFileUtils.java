package com.motaharinia.ms.iam.utils;

import com.motaharinia.ms.iam.modules.fso.presentation.frontuploader.FineUploaderResponseDto;
import com.motaharinia.ms.iam.modules.fso.presentation.validation.FsoSettingDto;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class TestFileUtils {

    private HttpHeaders headers;
    private final TestRestTemplate testRestTemplate;
    private Integer PORT;
    private String MODULE_API;

    public TestFileUtils(TestRestTemplate testRestTemplate, Integer port) {
        this.testRestTemplate = testRestTemplate;
        PORT = port;
        //تنظیم زبان لوکیل پروژه روی پارسی
        Locale.setDefault(new Locale("fa", "IR"));
        //ساخت هدر درخواست
        this.headers = new HttpHeaders();
        this.headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        this.MODULE_API = "http://localhost:" + PORT + "/api/v1.0/fso/";
    }


    public void upload(FsoSettingDto fsoSettingDto, String filePath, String fileUploadKey) throws IOException {

        //ایجاد درخواست
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("qqfile", new ByteArrayResource(getFileData(filePath)) {
            @Override
            public String getFilename() {
                return FilenameUtils.getName(filePath);
            }
        });
        body.add("qqtotalparts", 1);
        body.add("qqpartindex", 0);
        body.add("qqfilename", FilenameUtils.getName(filePath));
        body.add("qquuid", fileUploadKey);
        body.add("qqtotalfilesize", FileUtils.sizeOf(new File(filePath)));
        body.add("qqchunksize", FileUtils.sizeOf(new File(filePath)));


        ResponseEntity<FineUploaderResponseDto> response = this.testRestTemplate.exchange(MODULE_API + "upload/" + fsoSettingDto.getSubSystem() + "/" + fsoSettingDto.getEntity() + "/" + fsoSettingDto.getKind() + "/fine", HttpMethod.POST, new HttpEntity<>(body, this.headers), new ParameterizedTypeReference<>() {
        });
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

    }

    byte[] getFileData(String filePath) throws IOException {
        File initialFile = new File(filePath);
        Optional<InputStream> targetStream = Optional.of(new FileInputStream(initialFile));
        if (targetStream.isPresent()) {
            return targetStream.get().readAllBytes();
        } else {
            return new byte[0];
        }
    }

}
