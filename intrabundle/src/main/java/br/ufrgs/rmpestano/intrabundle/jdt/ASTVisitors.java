package br.ufrgs.rmpestano.intrabundle.jdt;

/**
 * Created by rmpestano on 2/10/14.
 */
public class ASTVisitors {

    private static StaleReferencesVisitor staleReferencesVisitor;


    public synchronized static StaleReferencesVisitor getStaleReferencesVisitor()  {

        if(staleReferencesVisitor == null){
            staleReferencesVisitor =  new StaleReferencesVisitor();
        }
        staleReferencesVisitor.init();
        return staleReferencesVisitor;

    }

}
