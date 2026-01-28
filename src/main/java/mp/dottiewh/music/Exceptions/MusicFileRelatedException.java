package mp.dottiewh.music.Exceptions;

public class MusicFileRelatedException extends InvalidMusicConfigException{
    public MusicFileRelatedException(String message, String path) {
        super(message, path);
    }

    public MusicFileRelatedException(String path) {
        super(path);
    }
}
