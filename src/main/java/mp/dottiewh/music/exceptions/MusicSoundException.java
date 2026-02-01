package mp.dottiewh.music.exceptions;

public class MusicSoundException extends InvalidMusicConfigException{
    public MusicSoundException(String message, String path) {
        super(path, message);
    }

    public MusicSoundException(String path) {
        super(path);
    }
}
