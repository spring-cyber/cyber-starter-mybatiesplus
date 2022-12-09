package com.cyber.mybatiesplus.type;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.nio.charset.Charset;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RSAEncryptType extends BaseTypeHandler<String> {

    static final Charset charset = CharsetUtil.CHARSET_UTF_8;
    static final RSA rsa =  SecureUtil.rsa(
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKPoknu+ap1LG5b84x3uVntZzzWB+q9FCo8l5S7HQp3qN2GnpsRfSicpXWSeuI9OxtnUC9wKAnIXbQJqlZx9QcPmJJByA+lIffzNbsTPinhTtFUMzpGKBmxe9ReoJiDrGMp1SNyNicgJV0ofmT6ZkB3E6ZNVFxhYehvXAAYn8av/AgMBAAECgYEAngN8FCeCCJl7w5eQLC602L2/8MmZrpZUk6Poyav3IN3G/jHtp6plhlNuYa5SJnW/ZgfyVvKfYPYMdSPjj7WBS5qVT4QP3TfjvbbqCGZqPRFBKm//zVmQfGr2gAR6oZITAWoMV4zKoM7QWV/Ecn+v+N/8MxOeKI8I2jPxhZW+HIECQQDWjfWc6Rg9se/3h+qT519quMt4LFxRZG3MNQw3BIRDgXjFpfVI15Kd0jeXe8P/xMuY2weHzHV2oIiEKWZMNH5RAkEAw5IXMDG+xvKqn2FWJ6iYAzrcrCydqGjauWCePSSBOnqRmtW1Ow9Y/W7FF01tni0uOUL/deUUDjRdo/3Cf0JhTwJAMOrA2vuSGU7eZVNJulsAODbVvRpwGeaJ2gsmM80F0tZMENQbrnXn6a+qGRGQyFm4Cau2ddG53kgPmAJQXoOBYQJAJ30rI3cX73H2U8JCtDVNpHFRN462stehOocwzGW/lkBDgEEgm/FIZbvlHgRrWuICkFtf271KpGecdM17ZVjQ7wJATDoFx/Z7aT3GHuu0y13VIcTAJ3uuVar4GzEewhD5CjrLEyfeXo3YccYUZTSInIA/E72ZiV+DFuz4son65pnsyw==",
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCj6JJ7vmqdSxuW/OMd7lZ7Wc81gfqvRQqPJeUux0Kd6jdhp6bEX0onKV1knriPTsbZ1AvcCgJyF20CapWcfUHD5iSQcgPpSH38zW7Ez4p4U7RVDM6RigZsXvUXqCYg6xjKdUjcjYnICVdKH5k+mZAdxOmTVRcYWHob1wAGJ/Gr/wIDAQAB");

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, String s, JdbcType jdbcType) throws SQLException {
        if(StringUtils.isNotEmpty(s)) {
            preparedStatement.setString(i,rsa.encryptHex(s,KeyType.PrivateKey));
            return;
        }
        preparedStatement.setString(i,s);
    }

    @Override
    public String getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return rsa.decryptStr(HexUtil.decodeHexStr(resultSet.getString(s)),KeyType.PrivateKey,charset);
    }

    @Override
    public String getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return rsa.decryptStr(HexUtil.decodeHexStr(resultSet.getString(i)),KeyType.PrivateKey,charset);
    }

    @Override
    public String getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return rsa.decryptStr(HexUtil.decodeHexStr(callableStatement.getString(i)),KeyType.PrivateKey,charset);
    }
}
