package com.cyber.mybatiesplus.config;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;

public class DbEncryptablePropertyResolver implements EncryptablePropertyResolver {

    private static final Logger LOG = LoggerFactory.getLogger(DbEncryptablePropertyResolver.class);

    private static String noPrefix = Hex.encodeHexString("NOPFX:".getBytes());

    @Override
    public String resolvePropertyValue(String s) {
        if(StringUtils.isNotEmpty(s) && s.startsWith(MybatisPlusConfig.ENCODED_PREFIX)) {
             String subPrefix = s.substring(MybatisPlusConfig.ENCODED_PREFIX.length());
             if(StringUtils.isNotEmpty(subPrefix) && subPrefix.startsWith(noPrefix)) {
                 String subStr = subPrefix.substring(noPrefix.length());
                 try {
                     PrivateKey privateKey = EncryptUtils.getPrivateKey("UMPassword");
                     byte[] subStrByte = Hex.decodeHex(subStr.toCharArray());
                     byte[] resultByte = EncryptUtils.decryptByPrivateKey(subStrByte, privateKey);
                     String result = new String(resultByte);
                     LOG.info("Found {} String {} Decoder {} ...", MybatisPlusConfig.ENCODED_PREFIX, s, result);
                     return result;
                 } catch (Exception e) {
                    LOG.error("Found " + MybatisPlusConfig.ENCODED_PREFIX + " String " + s + " But Decode Error ... ", e);
                    System.exit(2);
                 }
             }
        }
        return s;
    }
}
