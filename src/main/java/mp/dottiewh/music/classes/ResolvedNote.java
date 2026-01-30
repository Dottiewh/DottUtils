package mp.dottiewh.music.classes;

import mp.dottiewh.utils.U;
import org.bukkit.Registry;
import org.bukkit.Sound;

public class ResolvedNote{
    int tick;
    Sound sound;
    float volume;
    float pitch;
    float panning;

    public enum PitchConstants {

        FSHARP_1(0,  0.5f),
        G_1      (1,  0.529732f),
        GSHARP_1 (2,  0.561231f),
        A_1      (3,  0.594604f),
        ASHARP_1 (4,  0.629961f),
        B_1      (5,  0.667420f),
        C_1      (6,  0.707107f),
        CSHARP_1 (7,  0.749154f),
        D_1      (8,  0.793701f),
        DSHARP_1 (9,  0.840896f),
        E_1      (10, 0.890899f),
        F_1      (11, 0.943874f),

        FSHARP_2     (12, 1.0f),
        G_2          (13, 1.059463f),
        GSHARP_2     (14, 1.122462f),
        A_2          (15, 1.189207f),
        ASHARP_2     (16, 1.259921f),
        B_2          (17, 1.334840f),
        C_2          (18, 1.414214f),
        CSHARP_2     (19, 1.498307f),
        D_2          (20, 1.587401f),
        DSHARP_2     (21, 1.681793f),
        E_2          (22, 1.781797f),
        F_2          (23, 1.887749f),
        FSHARP_2_TOP (24, 2.0f);

        private final int index;
        private final float value;

        PitchConstants(int index, float value) {
            this.index = index;
            this.value = value;
        }
        public int getIndex() {
            return index;
        }
        public float getValue() {
            return value;
        }
        private static final PitchConstants[] byID = PitchConstants.values();
        public static float fromIndex(int index) {
            return (index >= 0 && index < byID.length) ? byID[index].value : 1f;
        }
    }

    public ResolvedNote(Note note, byte layerVolume, int layerPanning){
        this.tick=note.getTick();
        this.sound=switchKey(note.getInstrument());
        float rawVol = (layerVolume*note.getVelocity()) / 100f;
        this.volume=rawVol/10f;

        int pitch = Math.ceilDivExact(note.getPitch(), 100);
        int rawPitch = note.getKey()+pitch;

        this.pitch=switchPitch(rawPitch);
        //System.out.println(this.volume+":"+this.tick+" |||"+rawPitch+" | "+this.pitch);
        this.panning=(note.getPanning()+layerPanning)/2f;
    }

    private Sound switchKey(byte instrument){
        Sound toReturn;
        switch(instrument){
            case 0-> toReturn=Sound.BLOCK_NOTE_BLOCK_HARP;
            case 1-> toReturn=Sound.BLOCK_NOTE_BLOCK_BASS;
            case 2-> toReturn=Sound.BLOCK_NOTE_BLOCK_BASEDRUM;
            case 3-> toReturn=Sound.BLOCK_NOTE_BLOCK_SNARE;
            case 4-> toReturn=Sound.BLOCK_NOTE_BLOCK_HAT;
            case 5-> toReturn=Sound.BLOCK_NOTE_BLOCK_GUITAR;
            case 6-> toReturn=Sound.BLOCK_NOTE_BLOCK_FLUTE;
            case 7-> toReturn=Sound.BLOCK_NOTE_BLOCK_BELL;
            case 8-> toReturn=Sound.BLOCK_NOTE_BLOCK_CHIME;
            case 9-> toReturn=Sound.BLOCK_NOTE_BLOCK_XYLOPHONE;
            // not legacy
            case 10-> toReturn=Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE;
            case 11-> toReturn=Sound.BLOCK_NOTE_BLOCK_COW_BELL;
            case 12-> toReturn=Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO;
            case 13-> toReturn=Sound.BLOCK_NOTE_BLOCK_BIT;
            case 14-> toReturn=Sound.BLOCK_NOTE_BLOCK_BANJO;
            case 15-> toReturn=Sound.BLOCK_NOTE_BLOCK_PLING;
            default->{
                toReturn = Sound.BLOCK_NOTE_BLOCK_HARP;
                U.mensajeConsolaNP("Se ha intentado resolver el instrument ID:"+instrument+" ("+tick+") y no está registrada! Usando default...");
            }
        }
        return toReturn;
    }
    private float switchPitch(int key){
        float toGive= PitchConstants.fromIndex(key-33);

        if(key>57){
            toGive=switchFromHighPitch(key);
            U.mensajeDebugConsole("&cSe ha intentado registrar una nota más aguda de lo definido (>57) usando alternativa... | "+key+"->"+toGive+" / "+tick);
            //System.out.println("&cSe ha intentado registrar una nota más aguda de lo definido (>57) usando defauk... | "+key+" / "+tick);
        }
        if(key<33){
            toGive=switchFromlowPitch(key);
            U.mensajeDebugConsole("&cSe ha intentado registrar una nota más grave de lo definido (<33) usando alternativa... | "+key+"->"+toGive+" / "+tick);
            //System.out.println("&cSe ha intentado registrar una nota más grave de lo definido (<33) usando default... | "+key+" / "+tick);
        }

        //System.out.println(key);
        return toGive;
    }

    private float switchFromlowPitch(int key){
        key -= 9;
        int factor = (key%12);
        //if(factor==0) factor=12;

        int alteredKey = factor+33;
        return switchPitch(alteredKey);

    }
    private float switchFromHighPitch(int key){
        key -= 9;
        int factor = (key%12);
        if(factor==0) factor=12;

        int alteredKey = factor+12+33;
        return switchPitch(alteredKey);
    }

    //

    public int getTick() {
        return tick;
    }

    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public float getPanning() {
        return panning;
    }
}
