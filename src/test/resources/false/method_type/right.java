import spoon.reflect.code.CtTry;
import spoon.reflect.factory.Factory;

public class SpoonElement {
    public CtTry getElement() {
        Launcher launcher = new Launcher();
        Factory factory = launcher.getFactory();
        return factory.createTry();
    }
}
