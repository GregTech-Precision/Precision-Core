package precisioncore.api.capability;

public interface IReactorHatch {

    int getRodLevel();

    void downRod();

    void upRod();
    
    boolean isMOX();

    boolean hasRod();
    
    int depleteRod(int amount, boolean simulate);

    int depleteRod(boolean simulate);
}
