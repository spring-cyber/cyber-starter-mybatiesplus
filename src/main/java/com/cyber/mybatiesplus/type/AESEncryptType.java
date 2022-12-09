package com.cyber.mybatiesplus.type;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.nio.charset.Charset;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AESEncryptType extends BaseTypeHandler<String> {

    static final Charset charset = CharsetUtil.CHARSET_UTF_8;
    static final AES aes =  SecureUtil.aes(HexUtil.decodeHex("696cd329e6b3b680adf5779dce1adb52"));

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, String s, JdbcType jdbcType) throws SQLException {
        if(StringUtils.isNotEmpty(s)) {
            preparedStatement.setString(i,aes.encryptHex(s));
            return;
        }
        preparedStatement.setString(i,s);
    }

    @Override
    public String getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return aes.decryptStr(HexUtil.decodeHexStr(resultSet.getString(s)),charset);
    }

    @Override
    public String getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return aes.decryptStr(HexUtil.decodeHexStr(resultSet.getString(i)),charset);
    }

    @Override
    public String getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return aes.decryptStr(HexUtil.decodeHexStr(callableStatement.getString(i)),charset);
    }
}
