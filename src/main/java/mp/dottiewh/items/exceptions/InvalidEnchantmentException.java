package mp.dottiewh.items.exceptions;

public class InvalidEnchantmentException extends InvalidItemConfigException {
    public InvalidEnchantmentException(String enchString, String path) {
        super("Encantamiento inválido '" + enchString + "' en path '" + path +"'");
    }
}
