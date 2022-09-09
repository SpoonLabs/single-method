package se.kth;

import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.Operation;
import spoon.reflect.declaration.CtMethod;

import java.io.File;
import java.util.HashSet;
import java.util.List;
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
            if (operation.getSrcNode() != null) {
                CtMethod<?> method = operation.getSrcNode().getParent(CtMethod.class);
                methods.add(method);
            }
        }

        return methods.size() == 1 && !methods.contains(null);
    }
}
