package mp.dottiewh.commands.noaliasCommands.playtimecore;

public class NoPlaytimesException extends RuntimeException {
    public NoPlaytimesException(String msg) {
        super(msg);
    }
    public NoPlaytimesException(){
        super("Todo bien, solo no hay nada registrado en los playtimes.");
    }
}
