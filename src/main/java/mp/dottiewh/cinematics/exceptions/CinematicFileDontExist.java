package mp.dottiewh.cinematics.exceptions;

public class CinematicFileDontExist extends CinematicRelatedException{
    public CinematicFileDontExist(String path) {
        super(path);
    }

    public CinematicFileDontExist(String message, String path) {
        super(message, path);
    }
}
