package com.example.codingbattle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Arrays;
import org.springframework.core.convert.converter.Converter;

@Configuration
class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "codingbattle";
    }

    @Override
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new LocalDateTimeToDateConverter(),
                new DateToLocalDateTimeConverter()
        ));
    }

    static class LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {
        @Override
        public Date convert(LocalDateTime source) {
            return Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    static class DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {
        @Override
        public LocalDateTime convert(Date source) {
            return LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
        }
    }
}
