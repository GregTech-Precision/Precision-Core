package gtwp.api.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

public class GTWPChatUtils {

    public static void sendMessage(EntityPlayer player, String text){
        player.sendMessage(new TextComponentString(text));
    }
}
