package mp.dottiewh.music.classes;

public class MusicBuilderClass implements MusicBuilder{
    private Music building;

    public MusicBuilderClass() {
        reset();
    }
    @Override
    public void reset() {
        this.building =new Music();
    }

    @Override
    public Music build() {
        return building;
    }

    @Override
    public void setNBSver(int ver) {
        building.setNBSVersion(ver);
    }

    @Override
    public void setVanillaInstrumentCount(int c) {
        building.setVanillaInstrumentCount(c);
    }

    @Override
    public void setSongLength(short l) {
        building.setSongLength(l);
    }

    @Override
    public void setName(String s) {
        building.setSongName(s);
    }

    @Override
    public void setAuthor(String s) {
        building.setSongAuthor(s);
    }

    @Override
    public void setOriginalAuthor(String s) {
        building.setSongOriginalAuthor(s);
    }

    @Override
    public void setDescription(String s) {
        building.setSongDescription(s);
    }

    @Override
    public void setTempo(float tempo) {
        building.setSongTempo(tempo);
    }

    @Override
    public void setOriginalName(String s) {
        building.setOriginalFileName(s);
    }

    @Override
    public void setLoop(byte b) {
        building.setLoop(b);
    }

    @Override
    public void setMaxLoopCount(int b) {
        building.setMaxLoopCount(b);
    }

    @Override
    public void setLoopStartTick(short tick) {
        building.setLoopStartTick(tick);
    }

    @Override
    public void setStereo(boolean b){
        building.setStereo(b);
    }
}
