package precisioncore.api.capability;

public interface IReactorHatch {

    int getRodLevel();

    int getRodModifier();

    void downRod(int height, boolean callReactorUpdate);

    void upRod(int height, boolean callReactorUpdate);
    
    boolean isMOX();

    boolean hasRod();
    
    void depleteRod();
}
