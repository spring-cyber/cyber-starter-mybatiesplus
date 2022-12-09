package com.cyber.mybatiesplus.config;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;
import org.apache.commons.lang3.StringUtils;

public class DbEncryptablePropertyDetector implements EncryptablePropertyDetector {

    @Override
    public boolean isEncrypted(String s) {
        if (StringUtils.isNotEmpty(s)) {
            return s.startsWith(MybatisPlusConfig.ENCODED_PREFIX);
        }
        return false;
    }

    @Override
    public String unwrapEncryptedValue(String s) {
        return s.substring(MybatisPlusConfig.ENCODED_PREFIX.length());
    }
}
