package gtwp.common;

import net.minecraftforge.common.config.Config;

@Config(modid = "gtwp")
public class GTWPConfigHolder {

    @Config.Name("Machine options")
    @Config.Comment({"Options for singleblock and multiblock machines"})
    @Config.RequiresMcRestart
    public static MachineOptions machineOptions = new MachineOptions();

    public static class MachineOptions {

        @Config.Comment({"Satellite requires moon instead of space station"})
        public boolean satelliteRequireMoon = false;

        @Config.Comment({"Debug mode!!! DEV USE ONLY"})
        public boolean devMode = false;
    }

}