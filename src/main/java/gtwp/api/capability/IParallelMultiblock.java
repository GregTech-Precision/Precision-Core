package gtwp.api.capability;

public interface IParallelMultiblock {

    default boolean isParallel(){
        return false;
    }

    default int getMaxParallel(){
        return 1;
    }
}
