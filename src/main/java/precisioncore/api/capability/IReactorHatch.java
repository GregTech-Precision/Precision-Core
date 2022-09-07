package precisioncore.api.capability;

public interface IReactorHatch {

    int getRodLevel();
    
    boolean isMOX();
    
    int depleteRod(int amount, boolean simulate);

}
