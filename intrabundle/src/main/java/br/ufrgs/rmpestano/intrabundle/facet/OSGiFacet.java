package br.ufrgs.rmpestano.intrabundle.facet;

import br.ufrgs.rmpestano.intrabundle.model.OSGiProject;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by rmpestano on 1/22/14.
 */

@Singleton
public class OSGiFacet extends BaseFacet {

    @Inject
    protected Instance<BundleFacet> bundleFacet;

    @Override
    public boolean install() {

        /**
         * for now we are not going to install OSGi projects,
         * just analyse existing ones
         *
         */
        return isInstalled();
    }

    @Override
    public boolean isInstalled() {
        return isOSGiProject(project.getProjectRoot());

    }

    /**
     * verify if directory contains OSGi bundles by looking at each child folder
     *
     * @param projectRoot
     * @return true if it constains at least one OSGi bundle, false otherwise
     */
    public boolean isOSGiProject(DirectoryResource projectRoot) {

        for (Resource<?> child : projectRoot.listResources()) {
            DirectoryResource directoryResource = child.reify(DirectoryResource.class);
            if(directoryResource != null){
                if(bundleFacet.get().isOsgiBundle(directoryResource)){
                    return true;
                }
            }
        }
        return false;
    }

    @Produces
    public OSGiProject getCurrentOSGiProject() {
        return (OSGiProject) getProject();
    }

    public BundleFacet getBundleFacet() {
        return bundleFacet.get();
    }
}