package mp.dottiewh.music.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Music {
    int NBSVersion; // optional
    int VanillaInstrumentCount; //optional
    short songLength;
    short layerCount;
    String songName;
    String songAuthor;
    String songOriginalAuthor;
    String songDescription;
    float songTempo;
    //byte autoSave;
    //byte autoSaveDuration;
    //int minutesSpent;
    //int leftClicks;
    //int rightClicks;
    int totalNoteBlocksAdded;
    int totalNoteBlocksRemoved;
    String originalFileName;
    byte loop;
    int maxLoopCount;
    short loopStartTick;
    boolean stereo;

    HashMap<Integer, Layer> layers = new HashMap<>();

    Music() {
    }

    //getters setters

    public int getNBSVersion() {
        return NBSVersion;
    }

    public void setNBSVersion(int NBSVersion) {
        this.NBSVersion = NBSVersion;
    }

    public int getVanillaInstrumentCount() {
        return VanillaInstrumentCount;
    }

    public void setVanillaInstrumentCount(int vanillaInstrumentCount) {
        VanillaInstrumentCount = vanillaInstrumentCount;
    }

    public short getSongLength() {
        return songLength;
    }

    public void setSongLength(short songLength) {
        this.songLength = songLength;
    }

    public short getLayerCount() {
        return layerCount;
    }

    public void setLayerCount(short layerCount) {
        this.layerCount = layerCount;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongAuthor() {
        return songAuthor;
    }

    public void setSongAuthor(String songAuthor) {
        this.songAuthor = songAuthor;
    }

    public String getSongOriginalAuthor() {
        return songOriginalAuthor;
    }

    public void setSongOriginalAuthor(String songOriginalAuthor) {
        this.songOriginalAuthor = songOriginalAuthor;
    }

    public String getSongDescription() {
        return songDescription;
    }

    public void setSongDescription(String songDescription) {
        this.songDescription = songDescription;
    }

    public float getSongTempo() {
        return songTempo;
    }

    public void setSongTempo(float songTempo) {
        this.songTempo = songTempo;
    }

    public int getTotalNoteBlocksAdded() {
        return totalNoteBlocksAdded;
    }

    public void setTotalNoteBlocksAdded(int totalNoteBlocksAdded) {
        this.totalNoteBlocksAdded = totalNoteBlocksAdded;
    }

    public int getTotalNoteBlocksRemoved() {
        return totalNoteBlocksRemoved;
    }

    public void setTotalNoteBlocksRemoved(int totalNoteBlocksRemoved) {
        this.totalNoteBlocksRemoved = totalNoteBlocksRemoved;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public byte getLoop() {
        return loop;
    }

    public void setLoop(byte loop) {
        this.loop = loop;
    }

    public int getMaxLoopCount() {
        return maxLoopCount;
    }

    public void setMaxLoopCount(int maxLoopCount) {
        this.maxLoopCount = maxLoopCount;
    }

    public short getLoopStartTick() {
        return loopStartTick;
    }

    public void setLoopStartTick(short loopStartTick) {
        this.loopStartTick = loopStartTick;
    }

    public boolean isStereo() {
        return stereo;
    }

    public void setStereo(boolean stereo) {
        this.stereo = stereo;
    }

    public void addLayer(int id, Layer layer){
        layers.put(id, layer);
    }
    public void addLayer(Map.Entry<Integer, Layer> entry){
        layers.put(entry.getKey(), entry.getValue());
    }

    public List<Layer> getLayerList(){
        return new ArrayList<>(layers.values());
    }

    //
    @Override
    public String toString() {
        List<String> arrayList = new ArrayList<>();
        layers.values().forEach(layer->arrayList.add(layer.getName()));
        return "Music{" +
                "NBSVersion=" + NBSVersion +
                ", VanillaInstrumentCount=" + VanillaInstrumentCount +
                ", songLength=" + songLength +
                ", layerCount=" + layerCount +
                ", songName='" + songName + '\'' +
                ", songAuthor='" + songAuthor + '\'' +
                ", songOriginalAuthor='" + songOriginalAuthor + '\'' +
                ", songDescription='" + songDescription + '\'' +
                ", songTempo=" + songTempo +
                ", totalNoteBlocksAdded=" + totalNoteBlocksAdded +
                ", totalNoteBlocksRemoved=" + totalNoteBlocksRemoved +
                ", originalFileName='" + originalFileName + '\'' +
                ", loop=" + loop +
                ", maxLoopCount=" + maxLoopCount +
                ", loopStartTick=" + loopStartTick +
                ", stereo=" + stereo +
                '}'+
                "\n | Layers: "+arrayList.toString();
    }
}
