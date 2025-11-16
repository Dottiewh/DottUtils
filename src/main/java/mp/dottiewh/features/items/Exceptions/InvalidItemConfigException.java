package mp.dottiewh.features.items.Exceptions;

public class InvalidItemConfigException extends  RuntimeException{
    public InvalidItemConfigException(String path){
        super("Error en '" + path + "', checkea porfavor. ");
    }
    public InvalidItemConfigException(String path, String desc){
        super("Error en '" + path + "', Note: "+desc);
    }
}
