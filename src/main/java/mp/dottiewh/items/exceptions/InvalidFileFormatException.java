package mp.dottiewh.items.exceptions;

public class InvalidFileFormatException extends InvalidItemConfigException{
    public InvalidFileFormatException(String msg) {
        super(msg);
    }

    public InvalidFileFormatException(String msg, String path) {
        super(msg, path);
    }
}
