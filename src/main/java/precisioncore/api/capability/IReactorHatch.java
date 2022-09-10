package precisioncore.api.capability;

public interface IReactorHatch {

    int getRodLevel();

    void downRod();

    void upRod();
    
    boolean isMOX();

    boolean hasRod();
    
    void depleteRod();
}
