package com.cyber.mybatiesplus.config;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

class EncryptUtils {

    public static final String rsaPrvKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAItPcRRK2Tbz6piwnZCNSLEfWRX59q1eyB0Y9JgNtPs1f+Wkxm/2thT5iGqVLLNFEUbhELnxb89xk4heJzTqChGESYH/ZOs+HKJ8U3+Ttp0ZE6MY53pH8JzHqXnaePeDqX5tJtSxxWfD7wqkM1UglW6Fs8EnLP5k9zlriDIXMFvhAgMBAAECgYANcM21Kn+IiMICl0+saaUwyZh7wVEmavWdsRGwNepXLlM3oc0vcjshDO43cksMxMYk84P8nKmv9wJH7uWTel0cLOGkWCGtrSqPszJkx0Y9SHL6l9Z4yCk/TtGtBm/nP9Q3zM6ev1pfeTCtRxzK5Mb47e+WTNwVCJm0FrSoyIzw/QJBANKpaTy35+P6wZQMhnWNFQBN0PlnVPmTblMJDJW/cl+tfk9w15fFcT+rRwsWa/r2PKEiFzHg0kKAgaP6ePWtZhMCQQCpSty6LDDdqlNVbTwEFnrpG9qsWkPjhG9ajbpkTrpPnE3PT5MGw3t0GrDxK2elZ5PVutRpPLXs1DcYS4wkJ4S7AkEA0NETOxfFKixPJHUB95YAokuAgSiXh8lHi9Glgu7B7etpEE/3tT8HEiiyhGAWay8YTFUhjtSfN0Jwv12x9z2JtwJAbqEASx0TtddXa8zdWmKCYZEVPmoiUSy7Q/a4JlKYR+wBoQcEMnhOVZoXpRJTQfDE1/emVTsaO7CWbGb6Jqo4fwJAeBJFDV4qrNkreIrfpuIHUaire+0dPwer8X14iFxzf/P1oxLT/Nv2sWMNKQYTmGPmLt4IKeTb2WqOqNYvl1PGkg==";
    public static final PrivateKey RsaPrvKey = getRSAPrivateKey(rsaPrvKey);
    public static final String rsaPubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLT3EUStk28+qYsJ2QjUixH1kV+fatXsgdGPSYDbT7NX/lpMZv9rYU+YhqlSyzRRFG4RC58W/PcZOIXic06goRhEmB/2TrPhyifFN/k7adGROjGOd6R/Ccx6l52nj3g6l+bSbUscVnw+8KpDNVIJVuhbPBJyz+ZPc5a4gyFzBb4QIDAQAB";
    public static final PublicKey RsaPubKey = getRSAPublicKey(rsaPubKey);

    static KeyFactory rsaKeyFactory ;
    static {
        try{
            rsaKeyFactory = KeyFactory.getInstance("RSA");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static PublicKey getPublicKey(String keyName) {
        return RsaPubKey;
    }

    public static PrivateKey getPrivateKey(String keyName) {
        return RsaPrvKey;
    }

    public static PublicKey getRSAPublicKey(String publicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            return pubKey;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static PrivateKey getRSAPrivateKey(String privateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
            return priKey;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String sign(byte[] data, PrivateKey priKey) {
        try {
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initSign(priKey);
            signature.update(data);
            return Hex.encodeHexString(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decryptByPrivateKey(byte[] data, Key privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(rsaKeyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
