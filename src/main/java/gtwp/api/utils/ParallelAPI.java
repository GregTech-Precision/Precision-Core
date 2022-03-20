package gtwp.api.utils;

import gtwp.common.metatileentities.multi.parallel.CommunicationTower;
import gtwp.common.metatileentities.multi.parallel.SatelliteHatch;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ParallelAPI {

    public static final Map<UUID, SatelliteHatch> satelliteTransmitters = new ConcurrentHashMap<>();

    public static final Map<UUID, List<CommunicationTower>> communicationTowers = new ConcurrentHashMap<>();

    public static void addSatelliteTransmitter(UUID address, SatelliteHatch transmitter){
        if(address != null && transmitter != null && !satelliteTransmitters.containsKey(address))
            satelliteTransmitters.put(address, transmitter);
    }

    public static void removeSatelliteTransmitter(UUID address){
        if(address != null && satelliteTransmitters.containsKey(address))
            satelliteTransmitters.remove(address);
    }

    @Nullable
    public static SatelliteHatch getTransmitterByNetAddress(UUID netAddress){
        if(netAddress != null && !satelliteTransmitters.isEmpty() && satelliteTransmitters.containsKey(netAddress))
            return satelliteTransmitters.get(netAddress);
        else return null;
    }

    public static void addCommunicationTower(UUID netAddress, CommunicationTower tower){
        if(netAddress != null && tower != null){
            if(communicationTowers.containsKey(netAddress)) {
                communicationTowers.get(netAddress).add(tower);
            } else {
                List<CommunicationTower> towers = new ArrayList<>();
                towers.add(tower);
                communicationTowers.put(netAddress, towers);
            }
        }
    }

    public static void removeCommunicationTower(UUID netAddress, CommunicationTower tower){
        if(!communicationTowers.isEmpty() && netAddress != null && tower != null) {
            if (communicationTowers.containsKey(netAddress)) {
                communicationTowers.get(netAddress).remove(tower);
            }
        }
    }

    @Nullable
    public static CommunicationTower getActualTower(UUID netAddress, BlockPos location){
        if(netAddress != null && location != null) {
            if (!communicationTowers.isEmpty() && communicationTowers.containsKey(netAddress) && !communicationTowers.get(netAddress).isEmpty()){
                for(CommunicationTower tower : communicationTowers.get(netAddress)){
                    BlockPos towerPos = tower.getPos();
                    if(GTWPUtility.getDistance(towerPos, location) <= 256){
                        return tower;
                    }
                }
            }
        }
        return null;
    }

    public static boolean netAddressEqual(UUID first, UUID second){
        if(first != null && second != null)
            return first.equals(second);
        return false;
    }
}
