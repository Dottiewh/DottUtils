package mp.dottiewh.cinematics.exceptions;

public class CinematicRecordingHasNotStarted extends CinematicRelatedException{
    public CinematicRecordingHasNotStarted(String path) {
        super(path);
    }

    public CinematicRecordingHasNotStarted(String message, String path) {
        super(message, path);
    }
}
