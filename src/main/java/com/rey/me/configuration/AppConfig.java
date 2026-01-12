package com.rey.me.configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {

    @Configuration
    public class ModelMapperConfig {

        @Bean
        public ModelMapper modelMapper() {

            ModelMapper modelMapper = new ModelMapper();

            modelMapper.getConfiguration()
                    .setMatchingStrategy(MatchingStrategies.STRICT) // strict mapping
                    .setSkipNullEnabled(true)                       // do NOT map null values
                    .setFieldMatchingEnabled(true);            // match fields directly
            return modelMapper;
        }
    }

}
