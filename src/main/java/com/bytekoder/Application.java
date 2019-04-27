package com.bytekoder;

import com.bytekoder.query.ApplauseDataSetup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableSwagger2
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        ApplauseDataSetup.setupTables();
    }

    @Bean
    public Docket testerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("search")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/search.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Search Testers API Page")
                .description("Tester API with Swagger")
                .termsOfServiceUrl("www.bytekoder.com")
                .contact("Bhavani Shekhawat")
                .license("Apache License Version 2.0")
                .version("2.0")
                .build();
    }
}
