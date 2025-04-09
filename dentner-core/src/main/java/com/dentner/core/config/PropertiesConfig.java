package com.dentner.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(ignoreResourceNotFound = true,
    value = {
        "classpath:application-core.yaml",
        //"classpath:application-core-${spring.profiles.active}.yaml"
    }, factory = YamlPropertySourceFactory.class)
public class PropertiesConfig {

}
