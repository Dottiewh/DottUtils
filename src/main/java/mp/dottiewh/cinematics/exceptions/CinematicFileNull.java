package mp.dottiewh.cinematics.exceptions;

public class CinematicFileNull extends CinematicRelatedException{
    public CinematicFileNull(String path) {
        super(path);
    }

    public CinematicFileNull(String message, String path) {
        super(message, path);
    }
}
