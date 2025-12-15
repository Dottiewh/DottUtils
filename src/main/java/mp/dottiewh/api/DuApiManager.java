package mp.dottiewh.api;

import mp.dottiewh.DottUtils;
import mp.dottiewh.config.Config;
import mp.dottiewh.utils.U;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class DuApiManager {
    Plugin plugin;
    String prefix;

    public DuApiManager(Plugin plugin, String prefix) {
        this.plugin = plugin;
        this.prefix = prefix;

        U.mensajeConsolaNP(prefix+"se ha suscrito correctamente a la api de "+ DottUtils.prefix);
    }
    //---

    public List<String> getAdminList(){return Config.getAdminList();}
    public boolean containsAdmin(String adm){return Config.containsAdmin(adm);}

    public boolean enablePvP(){return Config.onPvP();}
    public boolean disablePvP(){return Config.offPvP();}
    //
    public void targetMsg(Player player, String msg){
        targetMsgNP(player, prefix+msg);
    }
    public void targetMsgNP(Player p, String msg){
        U.targetMessageNP(p, msg);
    }
    public Component mensajeConPrefix(String msg){
        return mensajeConColor(prefix+msg);
    }
    public Component mensajeConColor(String msg){
        return U.mensajeConColor(msg);
    }
    public void mensajeConsola(String msg){
        mensajeConsolaNP(prefix+msg);
    }
    public void mensajeConsolaNP(String msg){
        U.mensajeConsolaNP(msg);
    }
    public String componentToString(Component component){
        return U.componentToStringMsg(component);
    }
    public Component componentFromString(String msg){
        return U.componentColor(msg);
    }

    public double truncar(double value, int decimales){
        return U.truncar(value, decimales);
    }
    public String getLastVersionGithub(String urlGithub){
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlGithub))
                    .header("User-Agent", "DottUtils-Version-Checker")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String body = response.body();

                int start = body.indexOf("\"name\":\"") + 8;
                int end = body.indexOf("\"", start);
                return body.substring(start, end);
            } else {
                mensajeConsola("&cError al obtener versión: " + response.statusCode());
                return null;
            }

        }catch(Exception e){
            mensajeConsola("&cOcurrió un problema intentando conseguir la última versión de github. Details:");
            mensajeConsolaNP("c"+ Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    public void startCountdownForAll(int segundos, String format){
        U.countdownForAll(plugin, segundos, format);
    }
    public void stopCountdowns(){
        U.stopAllCountdowns();
    }
}
