package mp.dottiewh.music.Exceptions;

public class InvalidMusicConfigException extends RuntimeException {
    public InvalidMusicConfigException(String path) {
        super("Error en "+path+" en la config de musica.");
    }
    public InvalidMusicConfigException(String message, String path) {
        super("Error en "+path+" en la config de musica. Mensaje: "+message);
    }
}
