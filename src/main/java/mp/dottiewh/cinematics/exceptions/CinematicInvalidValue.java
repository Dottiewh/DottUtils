package mp.dottiewh.cinematics.exceptions;

public class CinematicInvalidValue extends CinematicRelatedException{
    public CinematicInvalidValue(String message, String path) {
        super(message, path);
    }

    public CinematicInvalidValue(String path) {
        super(path);
    }
}
