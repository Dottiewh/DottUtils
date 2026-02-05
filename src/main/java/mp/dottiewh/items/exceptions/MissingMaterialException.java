package mp.dottiewh.items.exceptions;

public class MissingMaterialException extends InvalidItemConfigException {
    public MissingMaterialException(String path) {
        super("No se definió 'material' en path '" + path + "'");
    }
}