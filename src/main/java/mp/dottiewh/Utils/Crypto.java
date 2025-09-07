package mp.dottiewh.Utils;

import com.google.common.graph.AbstractNetwork;
import mp.dottiewh.DottUtils;
import org.bukkit.entity.Player;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public class Crypto {
    public static String encodeForBack(double value, UUID uuid) {

        switch (DottUtils.ymlConfig.getConfig().getInt("back_encrypt_mode")){
            case 0 -> {return String.valueOf(value);}
            case 1 -> {
                return Base64.getEncoder().encodeToString(String.valueOf(value).getBytes());
            }
            case 2->{
                String uuidRaw = uuid.toString();
                String key = uuidRaw.replace("-","").substring(0, 16);
                return encryptWithKey(key, String.valueOf(value));
            }

            //---------
            default -> {return null;}
        }

    }
    public static double decodeForBack(String encoded, UUID uuid) {
        switch (DottUtils.ymlConfig.getConfig().getInt("back_encrypt_mode")){
            case 0 -> {return Double.parseDouble(encoded);}
            case 1 -> {
                return Double.parseDouble(new String(Base64.getDecoder().decode(encoded)));
            }
            case 2->{
                String uuidRaw = uuid.toString();
                String key = uuidRaw.replace("-","").substring(0, 16);
                return Double.parseDouble(decryptWithKey(key, encoded));
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
            U.STmensajeConsola("&cProblema con decrypt. Details:");
            U.STmensajeConsolaNP("&c"+ Arrays.toString(e.getStackTrace()));
            return "0";
        }
    }
}
