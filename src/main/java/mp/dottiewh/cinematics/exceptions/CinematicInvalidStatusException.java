package mp.dottiewh.cinematics.exceptions;

public class CinematicInvalidStatusException extends CinematicRelatedException{
    public CinematicInvalidStatusException(String path) {
        super(path);
    }

    public CinematicInvalidStatusException(String message, String path) {
        super(message, path);
    }
}
