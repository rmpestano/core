package br.ufrgs.rmpestano.intrabundle.plugin;

import br.ufrgs.rmpestano.intrabundle.facet.OSGiFacet;
import br.ufrgs.rmpestano.intrabundle.i18n.MessageProvider;
import br.ufrgs.rmpestano.intrabundle.jasper.JasperManager;
import br.ufrgs.rmpestano.intrabundle.model.ModuleDTO;
import br.ufrgs.rmpestano.intrabundle.model.OSGiModule;
import br.ufrgs.rmpestano.intrabundle.model.OSGiProject;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
@Alias("osgi")
@RequiresFacet(OSGiFacet.class)
public class OsgiPlugin implements Plugin {

    @Inject
    OSGiProject project;

    @Inject
    MessageProvider provider;

    @Inject
    ShellPrompt prompt;

    @Inject
    JasperManager jasperManager;


    @DefaultCommand
    public void defaultCommand(@PipeIn String in, PipeOut out) {
        out.println(ShellColor.YELLOW, provider.getMessage("osgi.defaultCommand"));
    }

    @Command(value = "countBundles")
    public void countBundles(@PipeIn String in, PipeOut out) {
        out.println("Total number of bundles:" + getModules().size());
    }

    @Command(value = "listBundles")
    public void listBundles(@PipeIn String in, PipeOut out) {
        for (OSGiModule osGiModule : getModules()) {
            out.println(osGiModule.toString());
        }
    }

    @Command(value = "loc", help = "count lines of code of all bundles")
    public void loc(@PipeIn String in, PipeOut out) {
        long total = 0;
        for (int i = 0; i < getModules().size(); i++) {
            long loci = getModules().get(i).getLinesOfCode();
            out.println(getModules().get(i).toString() + ":" + loci);
            total += loci;
        }
        out.println(provider.getMessage("osgi.total-loc") + total);
    }

    @Command(value = "lot", help = "count lines of test code of all bundles")
    public void lot(@PipeIn String in, PipeOut out) {
        long total = 0;
        for (int i = 0; i < getModules().size(); i++) {
            long loti = getModules().get(i).getLinesOfTestCode();
            out.println(getModules().get(i).toString() + ":" + loti);
            total += loti;
        }
        out.println(provider.getMessage("osgi.total-lot") + total);
    }

    @Command(value = "usesDeclarativeServices", help = "list modules that use declarative services")
    public void usesDeclarativeServices(@PipeIn String in, PipeOut out) {
        out.println(ShellColor.YELLOW, provider.getMessage("osgi.declarativeServices"));
        for (OSGiModule module : getModules()) {
            if (module.getUsesDeclarativeServices()) {
                out.println(module.toString());
            }
        }
    }

    @Command(value = "activators", help = "list modules activator classes")
    public void listActivators(@PipeIn String in, PipeOut out) {
        out.println(ShellColor.YELLOW, provider.getMessage("osgi.listActivators"));
        for (OSGiModule module : getModules()) {
            out.println(module.toString() + ":" + (module.getActivator() != null ? module.getActivator().getFullyQualifiedName() : provider.getMessage("osgi.no-activator")));
        }
    }

    @Command(value = "exportedPackages")
    public void listExportedPackages(@PipeIn String in, PipeOut out) {
        if (!allModules(provider.getMessage("packages"))) {
            OSGiModule choice = choiceModule();
            listModuleExportedPackages(choice, out);
        }//execute command for all modules
        else {
            for (OSGiModule osGiModule : getModules()) {
                listModuleExportedPackages(osGiModule, out);
            }
        }

    }

    @Command(value = "importedPackages")
    public void listImportedPackages(@PipeIn String in, PipeOut out) {
        if (!allModules(provider.getMessage("packages"))) {
            OSGiModule choice = choiceModule();
            listModuleImportedPackages(choice, out);
        }//execute command for all modules
        else {
            for (OSGiModule osGiModule : getModules()) {
                listModuleImportedPackages(osGiModule, out);
            }
        }
    }

    @Command(value = "requiredBundles")
    public void listRequiredBundles(@PipeIn String in, PipeOut out) {
        if (!allModules(provider.getMessage("requireBundles"))) {
            OSGiModule choice = choiceModule();
            listModuleRequiredBundles(choice, out);
        }//execute command for all bundles
        else {
            for (OSGiModule osGiModule : getModules()) {
                listModuleRequiredBundles(osGiModule, out);
            }
        }
    }

    @Command("dependencies")
    public void moduleDependencies(@PipeIn String in, PipeOut out) {
        if (!this.allModules(provider.getMessage("dependencies"))) {
            OSGiModule choice = choiceModule();
            this.listModuleDependencies(choice, out);
        }//execute command for all modules
        else {
            for (OSGiModule osGiModule : getModules()) {
                listModuleDependencies(osGiModule, out);
            }
        }
    }

    @Command("publishInterfaces")
    public void publishInterfaces(@PipeIn String in, PipeOut out) {
        out.println(ShellColor.YELLOW, provider.getMessage("osgi.publishInterfaces"));
        for (OSGiModule osGiModule : getModules()) {
            if (osGiModule.getPublishesInterfaces()) {
                out.println(osGiModule.toString());
            }
        }
    }

    @Command("declaresPermissions")
    public void declaresPermissions(@PipeIn String in, PipeOut out) {
        out.println(ShellColor.YELLOW, provider.getMessage("osgi.declaresPermissions"));
        for (OSGiModule osGiModule : getModules()) {
            if (osGiModule.getDeclaresPermissions()) {
                out.println(osGiModule.toString());
            }
        }
    }

    private void listModuleImportedPackages(OSGiModule module, PipeOut out) {
        out.println(ShellColor.YELLOW, "===== " + provider.getMessage("module.listImported", module) + " =====");
        if (module.getImportedPackages().isEmpty()) {
            out.println(provider.getMessage("module.noImportedPackages"));
        } else {
            for (String s : module.getImportedPackages()) {
                out.println(s);
            }
        }

    }

    private void listModuleExportedPackages(OSGiModule module, PipeOut out) {
        out.println(ShellColor.YELLOW, "===== " + provider.getMessage("module.listExported", module) + " =====");
        if (module.getExportedPackages().isEmpty()) {
            out.println(provider.getMessage("module.noExportedPackages"));
        } else {
            for (String s : module.getExportedPackages()) {
                out.println(s);
            }
        }

    }

    private void listModuleRequiredBundles(OSGiModule module, PipeOut out) {
        out.println(ShellColor.YELLOW, "===== " + provider.getMessage("module.listRequiredBundles", module) + " =====");
        if (module.getExportedPackages().isEmpty()) {
            out.println(provider.getMessage("module.noRequiredBundle"));
        } else {
            for (String s : module.getRequiredBundles()) {
                out.println(s);
            }
        }

    }

    private void listModuleDependencies(OSGiModule choice, PipeOut out) {
        out.println(ShellColor.YELLOW, "===== " + provider.getMessage("module.dependencies", choice) + " =====");
        if (project.getModulesDependencies().get(choice).isEmpty()) {
            out.println(provider.getMessage("module.noDependency"));
        } else {
            for (OSGiModule m : project.getModulesDependencies().get(choice)) {
                out.println(m.toString());
            }
        }
    }

    private OSGiModule choiceModule() {
        return prompt.promptChoiceTyped(provider.getMessage("module.choice"), getModules());
    }

    private boolean allModules(String allWhat) {
        return prompt.promptBoolean(provider.getMessage("module.all", allWhat));
    }

    public List<OSGiModule> getModules() {
        return project.getModules();
    }

    public List<ModuleDTO> getModulesToReport() {
        List<ModuleDTO> modulesDTO = new ArrayList<ModuleDTO>();

        for (OSGiModule module : getModules()) {
            modulesDTO.add(new ModuleDTO(module));
        }
        return modulesDTO;
    }

    @Command(help = "Generate a .pdf file containing information about all bundles of the project")
    public void report() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("project", project);
        params.put("provider", provider);
        jasperManager.reportName("osgi").filename(project.toString()).data(getModulesToReport()).params(params).build();
    }

}
