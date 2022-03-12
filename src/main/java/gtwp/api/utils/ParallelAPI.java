package gtwp.api.utils;

import gregtech.api.util.GTLog;
import gtwp.common.metatileentities.multi.parallel.SatelliteTransmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ParallelAPI {

    public static final Map<UUID, SatelliteTransmitter> satelliteTransmitters = new ConcurrentHashMap<>();

    public static void addSatelliteTransmitter(UUID address, SatelliteTransmitter transmitter){
        if(address != null && transmitter != null && !satelliteTransmitters.containsKey(address))
            satelliteTransmitters.put(address, transmitter);
    }

    public static void removeSatelliteTransmitter(UUID address){
        if(address != null && satelliteTransmitters.containsKey(address))
            satelliteTransmitters.remove(address);
    }

    public static SatelliteTransmitter getTransmitterByNetAddress(UUID netAddress){
        if(netAddress != null && !satelliteTransmitters.isEmpty() && satelliteTransmitters.containsKey(netAddress))
            return satelliteTransmitters.get(netAddress);
        else return null;
    }
}
