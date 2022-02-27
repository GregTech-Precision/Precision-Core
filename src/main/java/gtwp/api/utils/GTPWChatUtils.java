package gtwp.api.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class GTPWChatUtils {
    
    public void sendMessage(EntityPlayer player, String text){
        player.sendMessage(new TextComponentString(text));
    }
}
