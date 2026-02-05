package mp.dottiewh.items.exceptions;

public class InvalidMaterialException extends InvalidItemConfigException {
    public InvalidMaterialException(String material, String path) {
        super("Material inválido '" + material + "' en path '" + path + "'");
    }
}