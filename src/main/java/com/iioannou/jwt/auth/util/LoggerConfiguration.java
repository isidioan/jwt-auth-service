package com.iioannou.jwt.auth.util;

import java.lang.reflect.Field;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;

/**
 * @author ioannou
 */
@Configuration
public class LoggerConfiguration {


    @Bean
    public Logger logger(final InjectionPoint ip) {
        return LoggerFactory.getLogger(Optional.of(ip.getMethodParameter())
                                               .map(MethodParameter::getContainingClass)
                                               .orElseGet(() -> Optional.ofNullable(ip.getField())
                                                       .<Class>map(Field::getDeclaringClass).orElseThrow(IllegalArgumentException::new)));
    }
}
