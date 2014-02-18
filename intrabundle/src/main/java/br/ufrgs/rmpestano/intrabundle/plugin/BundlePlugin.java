package br.ufrgs.rmpestano.intrabundle.plugin;

import br.ufrgs.rmpestano.intrabundle.facet.BundleFacet;
import br.ufrgs.rmpestano.intrabundle.i18n.MessageProvider;
import br.ufrgs.rmpestano.intrabundle.model.OSGiModule;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.*;

import javax.inject.Inject;
import java.util.List;

/**
 *
 */
@Alias("bundle")
@RequiresFacet(BundleFacet.class)
public class BundlePlugin implements Plugin {
    @Inject
    private ShellPrompt prompt;

    @Inject
    OSGiModule bundle;

    @Inject
    MessageProvider messageProvider;


    @DefaultCommand
    public void defaultCommand(@PipeIn String in, PipeOut out) {
        out.println("default command");
    }

    @Command(value = "usesDeclarativeServices")
    public void usesDeclarativeServices(@PipeIn String in, PipeOut out) {
           out.println(bundle.getUsesDeclarativeServices() ? messageProvider.getMessage("yes"):messageProvider.getMessage("no"));
    }

    @Command(value = "usesBlueprint")
    public void usesBlueprint(@PipeIn String in, PipeOut out) {
        out.println(bundle.getUsesBlueprint() ? messageProvider.getMessage("yes"):messageProvider.getMessage("no"));
    }

    @Command(value = "declaresPermissions")
    public void declaresPermissions(@PipeIn String in, PipeOut out) {
        out.println(bundle.getDeclaresPermissions() ? messageProvider.getMessage("yes"):messageProvider.getMessage("no"));
    }

    @Command(value = "activator", help = "prints module activator path")
    public void activator(@PipeIn String in, PipeOut out) {
            out.println((bundle.getActivator() != null ? bundle.getActivator().getFullyQualifiedName() : messageProvider.getMessage("osgi.no-activator")));
    }

    @Command(value = "exportedPackages",help = "list bundle exported packages")
    public void exportedPackages(PipeOut out){
        if(bundle.getExportedPackages().isEmpty()){
            out.println(messageProvider.getMessage("module.noExportedPackages"));
        }
        else{
            for (String s : bundle.getExportedPackages()) {
                out.println(s);
            }
        }
    }

    @Command(value = "importedPackages",help = "list bundle imported packages")
    public void importedPackages(PipeOut out){
        if(bundle.getImportedPackages().isEmpty()){
            out.println(messageProvider.getMessage("module.noImportedPackages"));
        }
        else{
            for (String s : bundle.getImportedPackages()) {
                out.println(s);
            }
        }
    }

    @Command(value = "requiredBundles",help = "list bundle required bundles")
    public void requiredBundles(PipeOut out){
        if(bundle.getRequiredBundles().isEmpty()){
            out.println(messageProvider.getMessage("module.noRequiredBundles"));
        }
        else{
            for (String s : bundle.getRequiredBundles()) {
                out.println(s);
            }
        }
    }

    @Command(value = "publishesInterfaces", help = "true if bundle exported packages contains only interfaces, false if it contains one or more classes")
    public void publishesInterfaces(PipeOut out) {
        out.println(bundle.getPublishesInterfaces() ? messageProvider.getMessage("yes"):messageProvider.getMessage("no"));
    }

    @Command(value = "loc",help = "Count bundle lines of code")
    public void loc(PipeOut out){
         out.println(bundle.getLinesOfCode() != null ? bundle.getLinesOfCode().toString():"0");
    }

    @Command(value = "lot",help = "Count bundle lines of test code")
    public void lot(PipeOut out){
        out.println(bundle.getLinesOfTestCode() != null ? bundle.getLinesOfTestCode().toString():"0");
    }

    @Command(value = "staleReferences",help = "List files possibly containing OSGi service stale references")
    public void staleReferences(PipeOut out){
        List<Resource<?>> staleReferences = bundle.getStaleReferences();
        if(!staleReferences.isEmpty()){
            out.println(messageProvider.getMessage("bundle.listing-stale-references"));
            for (Resource<?> staleReference : staleReferences) {
                out.println(staleReference.getFullyQualifiedName());
            }

        }
        else{
            out.println(messageProvider.getMessage("bundle.noStaleReferences"));
        }
    }


}
