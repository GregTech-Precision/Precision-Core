package gtwp.api.utils;

import gtwp.common.metatileentities.multi.parallel.SatelliteTransmitter;

import java.util.HashMap;
import java.util.UUID;

public class ParallelAPI {

    public static HashMap<UUID, SatelliteTransmitter> satelliteTransmitters = new HashMap<>();

    public static void addSatelliteTransmitter(UUID address, SatelliteTransmitter transmitter){
        if(address != null && transmitter != null)
            satelliteTransmitters.put(address, transmitter);
    }

    public static void removeSatelliteTransmitter(UUID address){
        satelliteTransmitters.remove(address);
    }
}
