package mp.dottiewh.music.exceptions;

public class MusicSectionEmpty extends InvalidMusicConfigException{
    public MusicSectionEmpty(String path) {
        super(path);
    }

    public MusicSectionEmpty(String path, String message) {
        super(path, message);
    }
}
