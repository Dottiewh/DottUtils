package mp.dottiewh.features.items.Exceptions;

public class InvalidMaterialException extends InvalidItemConfigException {
    public InvalidMaterialException(String material, String path) {
        super("Material inválido '" + material + "' en path '" + path + "'");
    }
}