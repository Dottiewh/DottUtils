package mp.dottiewh.items.exceptions;

public class InvalidItemFile extends InvalidItemConfigException{
    public InvalidItemFile(String msg) {
        super(msg);
    }

    public InvalidItemFile(String msg, String path) {
        super(msg, path);
    }
}
