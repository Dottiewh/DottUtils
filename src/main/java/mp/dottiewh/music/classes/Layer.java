package mp.dottiewh.music.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Layer {
    List<Note> notes = new ArrayList<>();
    String name;
    byte volume; // 0-100
    int stereoData; // NEW | 0-200

    public Layer(){};
    public Layer(String name, byte isLock, byte volume, int stereoData) {
        this.name = name;
        this.volume = volume;
        this.stereoData = stereoData;
    }

    public Layer(String name, byte volume) {
        this.name = name;
        this.volume = volume;
    }

    //
    public int getStereoData() {
        return stereoData;
    }

    public void setStereoData(int stereoData) {
        this.stereoData = stereoData;
    }

    public byte getVolume() {
        return volume;
    }

    public void setVolume(byte volume) {
        this.volume = volume;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void addNote(Note note){
        this.notes.add(note);
    }
}
