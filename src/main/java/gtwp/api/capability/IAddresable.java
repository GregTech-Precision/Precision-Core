package gtwp.api.capability;

import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public interface IAddresable {

    default UUID generateNetAddress(EntityPlayer player, int frequency){
        return UUID.nameUUIDFromBytes((player.getName()+frequency).getBytes());
    }

    void setNetAddress(UUID netAddress);

    default void setNetAddress(EntityPlayer player, int frequency){
        setNetAddress(generateNetAddress(player, frequency));
    };

    UUID getNetAddress();
}
