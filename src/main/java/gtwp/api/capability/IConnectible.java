package gtwp.api.capability;

import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public interface IConnectible {

    UUID generateNetAddress(EntityPlayer player, int frequency);

    UUID getNetAddress();
}
