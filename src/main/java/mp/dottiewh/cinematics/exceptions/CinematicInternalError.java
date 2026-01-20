package mp.dottiewh.cinematics.exceptions;

public class CinematicInternalError extends CinematicRelatedException{
    public CinematicInternalError(String path) {
        super(path);
    }

    public CinematicInternalError(String message, String path) {
        super(message, path);
    }
}
