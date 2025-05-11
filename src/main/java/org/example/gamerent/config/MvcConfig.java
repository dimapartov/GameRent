package org.example.gamerent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.io.File;


@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${offer.image.path}")
    private String uploadPath;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File uploadDir = new File(uploadPath);
        String absolutePath = uploadDir.getAbsolutePath();
        registry
                .addResourceHandler("/images/**")
                .addResourceLocations("file:" + absolutePath, "classpath:/static/images/")
                .setCachePeriod(30 * 24 * 60 * 60)
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
    }

}