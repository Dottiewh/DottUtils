package mp.dottiewh.utils;

import mp.dottiewh.music.classes.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NBSutils {

    @Nullable
    public static Music decodeNBS(@NotNull File songFile) {
        try {
            return decodeNBS(new FileInputStream(songFile), songFile);
        } catch (FileNotFoundException e) {
            U.printException(e);
            U.mensajeConsolaNP("&cNo se ha encontrado el archivo "+songFile.getName());
            //System.out.println("not found the file");
        }
        return null;
    }
    @Nullable
    public static Music decodeNBS(@NotNull FileInputStream fileInput, @NotNull File file){
        try{
            DataInputStream dataInStream = new DataInputStream(fileInput);
            HashMap<Integer, Layer> layerList = new HashMap<>();

            // PARTE 1-
            short songLength = readShort(dataInStream);
            boolean legacy = songLength!=0, stereo=false;
            int vanillaInstrumentCount = 10;

            int nbsVer = 0;
            boolean loop=false;
            int loopCount=1;

            if(!legacy){
                nbsVer = dataInStream.readByte();
                vanillaInstrumentCount = dataInStream.readByte();
                if (nbsVer >= 3) {
                    songLength = readShort(dataInStream);
                }
            }
            short layerCount = readShort(dataInStream);
            String title = readString(dataInStream, legacy);
            String author = readString(dataInStream, legacy);
            String originalAuthor = readString(dataInStream, legacy);
            String description = readString(dataInStream, legacy);
            float speed = readShort(dataInStream)/100f;
            dataInStream.readBoolean(); //autosave
            dataInStream.readByte(); // auto save duration
            dataInStream.readByte(); // x/4 | time signature
            readInt(dataInStream); // minutes spent
            readInt(dataInStream); // left clicks
            readInt(dataInStream); // right clicks
            readInt(dataInStream); // blocks added
            readInt(dataInStream); // blocks removed
            String originalName = readString(dataInStream, legacy); // was from mid or schematic
            if(nbsVer>=4){
                 loop = dataInStream.readBoolean();
                 loopCount = dataInStream.readByte();
                 readShort(dataInStream); // loop start tick
            }
            //PARTE 2

            short tick = 1;
            while(true){
                short jump_tick = readShort(dataInStream);
                if(jump_tick==0) break;

                tick+=jump_tick;
                short layer = -1;
                while(true){
                    short jumpLayer = readShort(dataInStream);
                    if(jumpLayer==0) break;
                    //System.out.println(jumpLayer);
                    layer+=jumpLayer; // layer = layer + jumpLayer

                    byte instrument = dataInStream.readByte(); // 0-15 o mas si es custom
                    byte key = dataInStream.readByte(); // 0-87

                    byte velocity = 100; // 0-100
                    int panning = 100; // 0-200 | 100 es centro
                    short pitch = 0; // -1200 - +1200

                    if(nbsVer>=4){
                        velocity=dataInStream.readByte();
                        panning= dataInStream.readUnsignedByte();
                        pitch=readShort(dataInStream);
                    }
                    if(panning!=100) stereo=true;

                    Note note = new Note(tick, instrument, key, velocity, panning, pitch);
                    addNote(layerList, layer, note);

                    //System.out.println("tick="+tick+" | jump_tick="+jump_tick+" | layer="+layer);
                }
            }
            if (nbsVer > 0 && nbsVer < 3) {
                songLength = tick;
            }
            // PARTE 3
            for(int i=0;i<layerCount;i++){
                Layer layer = layerList.get(i);

                String name = readString(dataInStream, legacy);
                if(nbsVer>=4){
                    dataInStream.readByte(); //layer lock
                }
                byte volume = dataInStream.readByte();

                int panning = 100;
                if(nbsVer>=2){
                    panning=dataInStream.readUnsignedByte();
                    if(panning!=100) stereo=true;
                }

                if(layer!=null){
                    layer.setName(name);
                    layer.setVolume(volume);
                    layer.setStereoData(panning);
                }
            }
            // PARTE 4

            //
            /*for(Map.Entry<Integer, Layer> entry : layerList.entrySet()){
                System.out.println(entry.getKey());
                for(Note note : entry.getValue().getNotes()){
                    System.out.println(note.toString());
                }
            }
            System.out.println(layerList.size());*/

            //=============
            MusicBuilderClass musicBuilder = new MusicBuilderClass();
            musicBuilder.setNBSver(nbsVer);
            musicBuilder.setVanillaInstrumentCount(vanillaInstrumentCount);
            musicBuilder.setSongLength(songLength);
            musicBuilder.setName(title);
            musicBuilder.setAuthor(author);
            musicBuilder.setOriginalAuthor(originalAuthor);
            musicBuilder.setDescription(description);
            musicBuilder.setTempo(speed);

            musicBuilder.setOriginalName(originalName);
            musicBuilder.setStereo(stereo);

            Music music = musicBuilder.build();
            for(Map.Entry<Integer, Layer> entry : layerList.entrySet()){
                music.addLayer(entry);
            }

            //System.out.println(music.toString());
            U.mensajeDebugConsole(music.toString());

            fileInput.close();
            dataInStream.close();
            return music;
        } catch (FileNotFoundException e){
            U.mensajeConsolaNP("&cError al intentar cargar NBS "+file.getName()+" | File not found.");
            U.mensajeConsolaNP(String.valueOf(e));
        }catch (EOFException e){
            U.mensajeConsolaNP("&cCanción corrupta: "+file.getName());
        }
        catch (IOException e){
            U.mensajeConsolaNP(e.toString());
            U.mensajeConsolaNP("---");
            U.mensajeConsolaNP(Arrays.toString(e.getStackTrace()));
        }

        return null;
    }
    //
    private static void addNote(HashMap<Integer, Layer> layerMap, int layerID, Note note){
        Layer layer = layerMap.get(layerID);
        if(layer==null){
            layer=new Layer();
        }
        layer.addNote(note);
        layerMap.put(layerID, layer);
    }

    //
    private static short readShort(DataInputStream dataInputStream) throws IOException {
        int byte1 = dataInputStream.readUnsignedByte();
        int byte2 = dataInputStream.readUnsignedByte();
        return (short) (byte1 + (byte2 << 8));
    }

    private static int readInt(DataInputStream dataInputStream) throws IOException {
        int byte1 = dataInputStream.readUnsignedByte();
        int byte2 = dataInputStream.readUnsignedByte();
        int byte3 = dataInputStream.readUnsignedByte();
        int byte4 = dataInputStream.readUnsignedByte();
        return (byte1 + (byte2 << 8) + (byte3 << 16) + (byte4 << 24));
    }

    private static String readString(DataInputStream dataInputStream, boolean legacy) throws IOException {
        if(legacy) return readStringOld(dataInputStream);
        else return readStringNew(dataInputStream);
    }
    private static String readStringNew(DataInputStream dataInputStream) throws IOException {
        int length = readInt(dataInputStream);

        if (length == 0) return "";

        byte[] data = new byte[length];
        dataInputStream.readFully(data);

        return new String(data, StandardCharsets.UTF_8);
    }
    private static String readStringOld(DataInputStream dataInputStream) throws IOException {
        int length = readInt(dataInputStream);
        StringBuilder builder = new StringBuilder(length);
        for (; length > 0; --length) {
            char c = (char) dataInputStream.readUnsignedByte();
            if (c == (char) 0x0D) {
                c = ' ';
            }
            builder.append(c);
        }
        return builder.toString();
    }
}
