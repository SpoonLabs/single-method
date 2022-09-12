package se.kth;

import gumtree.spoon.AstComparator;
import gumtree.spoon.builder.CtWrapper;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.Operation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.path.CtRole;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        File original = new File(args[0]);
        File patched = new File(args[1]);

        System.out.println(api(original, patched));
    }

    /**
     * Returns whether the patch is contained within exactly one method or not.
     *
     * @param original original version of file
     * @param patched patched version of file
     */
    public static boolean api(File original, File patched) throws Exception {
        Diff diff = new AstComparator().compare(original, patched);
        return isInsideOnlyOneMethod(diff.getRootOperations());
    }
    
    private static boolean isInsideOnlyOneMethod(List<Operation> operations) {
        Set<CtMethod> methods = new HashSet<>();
        for (Operation operation : operations) {
            if (operation.getSrcNode() != null && !shouldBeIgnored(operation.getSrcNode())) {
                CtMethod<?> method = new CtMethodWrapper(operation.getSrcNode().getParent(CtMethod.class));
                methods.add(method);
            }
        }

        return methods.size() == 1 && !methods.contains(new CtMethodWrapper(null));
    }

    private static boolean shouldBeIgnored(CtElement element) {
        return element instanceof CtWrapper
                || (element.getParent() instanceof CtMethod && (
                        element.getRoleInParent() == CtRole.TYPE
                        || element.getRoleInParent() == CtRole.ANNOTATION));
    }
}

/**
 * Comparator that compares two instances of {@link CtMethod} by their {@link CtMethod#getSignature()} and return type.
 */
class CtMethodWrapper extends CtMethodImpl {

    private final CtMethod<?> wrappedMethod;
    public CtMethodWrapper(CtMethod<?> method) {
        super();
        this.wrappedMethod = method;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CtMethodWrapper)) {
            return false;
        }
        CtMethodWrapper other = (CtMethodWrapper) obj;
        if (wrappedMethod == null && other.wrappedMethod == null) {
            return true;
        }
        return wrappedMethod.getSignature().equals(other.wrappedMethod.getSignature())
                && wrappedMethod.getType().equals(other.wrappedMethod.getType());
    }

    @Override
    public int hashCode() {
        if (wrappedMethod == null) {
            return 0;
        }
        return Objects.hash(wrappedMethod.getSignature(), wrappedMethod.getType());
    }
}
