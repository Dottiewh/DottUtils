package mp.dottiewh.utils;

import mp.dottiewh.DottUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public class Crypto {
    public static String encodeForBack(double value, UUID uuid) {
        return mainEncodeForBack(value, uuid, DottUtils.ymlConfig.getConfig().getInt("back_encrypt_mode"));
    }
    public static String encodeForBack(double value, UUID uuid, int mode) {
        return mainEncodeForBack(value, uuid, mode);
    }
    private static String mainEncodeForBack(double value, UUID uuid, int mode){
        switch (mode){
            case 0 -> {return String.valueOf(value);}
            case 1 -> {
                return Base64.getEncoder().encodeToString(String.valueOf(value).getBytes());
            }
            case 2->{
                String uuidRaw = uuid.toString();
                String key = uuidRaw.replace("-","").substring(0, 16);
                return encryptWithKey(key, String.valueOf(value*3.2));
            }

            //---------
            default -> {return null;}
        }
    }
    public static double decodeForBack(String encoded, UUID uuid) {
        return mainDecodeForBack(encoded, uuid, DottUtils.ymlConfig.getConfig().getInt("back_encrypt_mode"));
    }
    public static double decodeForBack(String encoded, UUID uuid, int mode) {
        return mainDecodeForBack(encoded, uuid, mode);
    }
    private static double mainDecodeForBack(String encoded, UUID uuid, int mode){
        switch (mode){
            case 0 -> {return Double.parseDouble(encoded);}
            case 1 -> {
                return Double.parseDouble(new String(Base64.getDecoder().decode(encoded)));
            }
            case 2->{
                String uuidRaw = uuid.toString();
                String key = uuidRaw.replace("-","").substring(0, 16);
                return Double.parseDouble(decryptWithKey(key, encoded)) / 3.2;
            }

            //---------
            default -> {return 0;}
        }
    }
    //====================
    private static String encryptWithKey(String KEY, String value) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes()));
        } catch (Exception e) {
            return null;
        }
    }
    private static String decryptWithKey(String KEY, String value) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(value)));
        } catch (Exception e) {
            U.mensajeConsola("&cProblema con decrypt. Details:");
            U.mensajeConsolaNP("&c"+ Arrays.toString(e.getStackTrace()));
            return "0";
        }
    }
}
