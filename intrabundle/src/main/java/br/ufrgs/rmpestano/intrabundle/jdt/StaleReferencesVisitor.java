package br.ufrgs.rmpestano.intrabundle.jdt;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;

/**
 * Created by rmpestano on 2/10/14.
 */
public class StaleReferencesVisitor extends ASTVisitor {

    private int numGetServices;
    private int numUngetServices;


    public void init() {
        numUngetServices = 0;
        numGetServices = 0;
    }


    @Override
    public boolean visit(MethodInvocation node) {
        SimpleName name = node.getName();
        if(name != null){
            if (name.toString().equals("getService")) {
                numGetServices++;
            } else if (name.toString().equals("ungetService")) {
                numUngetServices++;
            }
        }
        return super.visit(node);
    }

    public int getNumGetServices() {
        return numGetServices;
    }

    public int getNumUngetServices() {
        return numUngetServices;
    }

}


