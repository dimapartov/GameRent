package org.example.gamerent.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;


@Configuration
public class BeanConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        return modelMapper;
    }
}