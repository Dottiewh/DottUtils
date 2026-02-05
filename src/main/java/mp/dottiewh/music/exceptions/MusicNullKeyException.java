package mp.dottiewh.music.exceptions;

public class MusicNullKeyException extends InvalidMusicConfigException{
    public MusicNullKeyException(String path) {
        super(path);
    }

    public MusicNullKeyException(String path, String message) {
        super(path, message);
    }
}
