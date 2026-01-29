package mp.dottiewh.music.classes;

public interface MusicBuilder {
    void reset();
    Music build();

    void setNBSver(int ver);
    void setVanillaInstrumentCount(int c);
    void setSongLength(short l);
    void setName(String s);
    void setAuthor(String s);
    void setOriginalAuthor(String s);
    void setDescription(String s);
    void setTempo(float tempo);

    void setOriginalName(String s);
    void setLoop(byte b);
    void setMaxLoopCount(int b);
    void setLoopStartTick(short tick);

    void setStereo(boolean b);
}
