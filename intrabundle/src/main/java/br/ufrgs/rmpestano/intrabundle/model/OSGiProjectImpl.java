package br.ufrgs.rmpestano.intrabundle.model;

import br.ufrgs.rmpestano.intrabundle.facet.OSGiFacet;
import org.jboss.forge.project.BaseProject;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.FacetNotFoundException;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFilter;

import javax.enterprise.inject.Typed;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rmpestano on 1/22/14.
 */
@Typed
public class OSGiProjectImpl extends BaseProject implements OSGiProject,Project {
    private DirectoryResource projectRoot = null;
    private final ProjectFactory factory;
    private List<OSGiModule> modules;
    private Map<OSGiModule, List<OSGiModule>> moduleDependenciesMap;


    public OSGiProjectImpl() {
        factory = null;
    }

    public OSGiProjectImpl(final ProjectFactory factory, final DirectoryResource dir) {
        this.factory = factory;
        this.projectRoot = dir;
    }

    @Override
    public <F extends Facet> F getFacet(final Class<F> type) {
        try {
            return super.getFacet(type);
        } catch (FacetNotFoundException e) {
            factory.registerSingleFacet(this, type);
            return super.getFacet(type);
        }
    }

    public List<OSGiModule> getModules() {
        if (modules == null) {
            modules = initalizeModules();
        }
        return modules;
    }

    private Map<OSGiModule,List<OSGiModule>> initalizeModulesDependencies() {
        Map<OSGiModule,List<OSGiModule>> dependencies = new HashMap<OSGiModule, List<OSGiModule>>();
        for (OSGiModule m1 : getModules()) {
            dependencies.put(m1,new ArrayList<OSGiModule>());
            if(m1.getImportedPackages().isEmpty()){
                //has no module dependencies, go to next module
                continue;
            }
            for (OSGiModule m2 : getModules()) {
                if(m2.equals(m1)){
                    continue;
                }
                if(!m2.getExportedPackages().isEmpty()){
                    for (String s : m2.getExportedPackages()) {
                        if(m1.getImportedPackages().contains(s)){
                            dependencies.get(m1).add(m2);
                            break;
                        }
                    }
                }


            }
        }
        return dependencies;
    }

    private List<OSGiModule> initalizeModules() {
        List<OSGiModule> modulesFound = new ArrayList<OSGiModule>();
        OSGiFacet osgi = getFacet(OSGiFacet.class);
        List<Resource<?>> children = this.getProjectRoot().listResources(new ResourceFilter() {
            @Override
            public boolean accept(Resource<?> resource) {
                return resource instanceof DirectoryResource;
            }
        });

        for (Resource<?> child : children) {
            DirectoryResource childRoot = (DirectoryResource) child;
            if(osgi.getBundleFacet().isOsgiBundle(childRoot)){
                OSGiModule osGiModule = new OSGiModuleImpl(factory, childRoot);
                modulesFound.add(osGiModule);
            }
        }

        return modulesFound;
    }

    @Override
    public DirectoryResource getProjectRoot() {
        return projectRoot;
    }

    @Override
    public boolean exists() {
        return (projectRoot != null) && projectRoot.exists();
    }

    @Override
    public String toString() {
        return getProjectRoot().toString();
    }

    public Map<OSGiModule, List<OSGiModule>> getModulesDependencies() {
        if(moduleDependenciesMap == null){
            moduleDependenciesMap = initalizeModulesDependencies();
        }
        return moduleDependenciesMap;
    }


}
