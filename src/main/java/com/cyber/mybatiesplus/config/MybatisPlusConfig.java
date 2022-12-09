package com.cyber.mybatiesplus.config;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.sql.SQLUtils;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.cyber.mybatiesplus.filter.TraceDriudLogFilter;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MybatisPlusConfig {
    public static final Logger LOG = LoggerFactory.getLogger(MybatisPlusConfig.class);

    public static final String ENCODED_PREFIX = "encrypt:";

    @Bean(name = "encryptablePropertyDetector")
    public EncryptablePropertyDetector encryptablePropertyDetector() {
        return new DbEncryptablePropertyDetector();
    }

    @Bean(name = "encryptablePropertyResolver")
    public EncryptablePropertyResolver encryptablePropertyResolver() {
        return new DbEncryptablePropertyResolver();
    }

    @Bean
    @Primary
    public Slf4jLogFilter slf4jLogFilter() {
        Slf4jLogFilter slf4jLogFilter = new TraceDriudLogFilter();
        slf4jLogFilter.setConnectionLogEnabled(false);
        slf4jLogFilter.setResultSetLogEnabled(false);
        slf4jLogFilter.setStatementExecutableSqlLogEnable(false);
        slf4jLogFilter.setStatementSqlFormatOption(new SQLUtils.FormatOption(true, true));
        return slf4jLogFilter;
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
