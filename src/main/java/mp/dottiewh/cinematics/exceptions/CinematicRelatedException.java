package mp.dottiewh.cinematics.exceptions;

public class CinematicRelatedException extends RuntimeException{
    public CinematicRelatedException(String path) {
        super("Error en "+path+" sobre cinematicas.");
    }
    public CinematicRelatedException(String message, String path) {
        super("Error en "+path+" sobre cinematicas. Mensaje: "+message);
    }
}
