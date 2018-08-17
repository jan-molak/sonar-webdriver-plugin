package it.ding.sonar.check.locator;

import static it.ding.sonar.data.CommonData.LOCATORS_RECOMMENDED;
import static it.ding.sonar.util.CommonUtil.getIdentifier;
import static it.ding.sonar.util.CommonUtil.getLocatorValueMapInAnnotation;
import static it.ding.sonar.util.CommonUtil.methodInvocationIsPartOfWebDriverPackage;

import java.util.Map;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.AnnotationTree;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;

@Rule(key = "locator-strategy-check",
    name = "locator-strategy-check",
    description = "Avoid xpath locator",
    priority = Priority.MAJOR,
    tags = {"bug"})
public class LocatorStrategyByXpathCheck extends BaseTreeVisitor implements JavaFileScanner {

    private JavaFileScannerContext context;

    public static final String XPATH_LOCATOR = "xpath";

    @Override
    public void scanFile(JavaFileScannerContext context) {
        this.context = context;

        scan(context.getTree());
    }

    @Override
    public void visitAnnotation(AnnotationTree tree) {
        Map<String, String> locatorsInAnnotation = getLocatorValueMapInAnnotation(tree);

        for (Map.Entry<String,String> locator : locatorsInAnnotation.entrySet()) {
            String locatorStrategy = locator.getKey();
            checkLocator(tree, locatorStrategy);
        }
    }

    @Override
    public void visitMethodInvocation(MethodInvocationTree tree) {
        if (methodInvocationIsPartOfWebDriverPackage(tree)) {
            checkLocator(tree, getIdentifier(tree).name());
        }
    }

    private void checkLocator(ExpressionTree expressionTree, String locatorStrategy) {
        if (XPATH_LOCATOR.equalsIgnoreCase(locatorStrategy)) {
            context.reportIssue(this, expressionTree,
                "Avoid using " + XPATH_LOCATOR + " locator, try using " + LOCATORS_RECOMMENDED.toString());
        }
    }


}