package com.motaharinia.ms.iam.modules.home.presentation;

import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس کنترلر خانه
 */
@RestController
@GraphQLApi
@Slf4j
public class HomeController {

    @Value("${spring.application.name}")
    private String springApplicationName;

    @GetMapping("/")
    public String getUrl() {
        return springApplicationName;
    }

}
