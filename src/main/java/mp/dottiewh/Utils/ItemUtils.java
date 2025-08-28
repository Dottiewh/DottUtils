package mp.dottiewh.Utils;

import mp.dottiewh.noaliasCommands.Gm;
import mp.dottiewh.noaliasCommands.Jump;
import mp.dottiewh.noaliasCommands.Status;
import org.bukkit.inventory.EquipmentSlotGroup;

public class ItemUtils {
    public static EquipmentSlotGroup getSlotFromString(String slot) {

        switch (slot.toUpperCase()){
            case "ARMOR" -> {
                return EquipmentSlotGroup.ARMOR;
            }
            case "BODY"->{
                return EquipmentSlotGroup.BODY;
            }
            case "CHEST"->{
                return EquipmentSlotGroup.CHEST;
            }
            case "FEET"->{
                return EquipmentSlotGroup.FEET;
            }
            case "HAND"->{
                return EquipmentSlotGroup.HAND;
            }
            case "HEAD"->{
                return EquipmentSlotGroup.HEAD;
            }
            case "LEGS"->{
                return EquipmentSlotGroup.LEGS;
            }
            case "MAINHAND"->{
                return EquipmentSlotGroup.MAINHAND;
            }
            case "OFFHAND"->{
                return EquipmentSlotGroup.OFFHAND;
            }
            case "SADDLE"->{
                return EquipmentSlotGroup.SADDLE;
            }
            case "ANY"->{
                return EquipmentSlotGroup.ANY;
            }
            //-------------------------
            default-> {
                return EquipmentSlotGroup.ANY;
            }
        }
    }
}
