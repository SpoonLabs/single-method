import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;

public class SpoonElement {
    public CtElement getElement() {
        Launcher launcher = new Launcher();
        Factory factory = launcher.getFactory();
        return factory.createTry();
    }
}
