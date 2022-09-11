package precisioncore.api.capability;

public interface IReactorHatch {

    int getRodLevel();

    int getRodModifier();

    void downRod();

    void upRod();
    
    boolean isMOX();

    boolean hasRod();
    
    void depleteRod();
}
