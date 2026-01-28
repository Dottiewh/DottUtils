package mp.dottiewh.music.classes;

public class Note {
    int tick;
    byte instrument;
    byte key;
    byte velocity; // new
    int panning; // new | 0-200
    int pitch; // new | -1200 A +1200

    public Note(int tick, byte instrument, byte key, byte velocity, int panning, int pitch) {
        this.tick = tick;
        this.instrument = instrument;
        this.key = key;
        this.velocity = velocity;
        this.panning = panning;
        this.pitch = pitch;
    }

    public int getTick() {
        return tick;
    }

    public byte getInstrument() {
        return instrument;
    }

    public byte getKey() {
        return key;
    }

    public byte getVelocity() {
        return velocity;
    }

    public int getPanning() {
        return panning;
    }

    public int getPitch() {
        return pitch;
    }

    @Override
    public String toString() {
        return "Note{" +
                "tick=" + tick +
                ", instrument=" + instrument +
                ", key=" + key +
                ", velocity=" + velocity +
                ", panning=" + panning +
                ", pitch=" + pitch +
                '}';
    }
}
