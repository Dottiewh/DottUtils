package mp.dottiewh.Items.Exceptions;

public class InvalidEnchantmentException extends InvalidItemConfigException {
    public InvalidEnchantmentException(String enchString, String path) {
        super("Encantamiento inválido '" + enchString + "' en path '" + path +"'");
    }
}
