package mp.dottiewh.items.Exceptions;

public class InvalidItemConfigException extends  RuntimeException{
    public InvalidItemConfigException(String msg){
        super("Error: "+msg+", checkea porfavor. ");
    }
    public InvalidItemConfigException(String msg, String path){
        super("Error en '" + path + "', Note: "+msg);
    }
}
