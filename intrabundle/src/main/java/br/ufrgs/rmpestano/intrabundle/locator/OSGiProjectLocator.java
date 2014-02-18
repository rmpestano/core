package br.ufrgs.rmpestano.intrabundle.locator;

import br.ufrgs.rmpestano.intrabundle.facet.BundleFacet;
import br.ufrgs.rmpestano.intrabundle.facet.OSGiFacet;
import br.ufrgs.rmpestano.intrabundle.i18n.MessageProvider;
import br.ufrgs.rmpestano.intrabundle.model.OSGiProjectImpl;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.locator.ProjectLocator;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by rmpestano on 1/22/14.
 */
@Singleton
public class OSGiProjectLocator implements ProjectLocator {

    private final ProjectFactory factory;

    private final Instance<OSGiFacet> osgiFacetInstance;

    @Inject
    MessageProvider provider;

    @Inject
    Shell shell;


    @Inject
    public OSGiProjectLocator(final ProjectFactory factory, @Any final Instance<OSGiFacet> osgiFacet) {
        this.factory = factory;
        this.osgiFacetInstance = osgiFacet;
    }

    @Override
    public Project createProject(DirectoryResource directoryResource) {
        OSGiFacet osgi = osgiFacetInstance.get();
        OSGiProjectImpl result = new OSGiProjectImpl(factory, directoryResource);
        osgi.setProject(result);
        /* we are not going to install OSGi projects, only inspect existing ones
        if (!osgi.isInstalled()) {
            result.installFacet(osgi);
        } else    */
        result.registerFacet(osgi);

        if (!result.hasFacet(OSGiFacet.class) || result.hasFacet(BundleFacet.class)) {
            return null;
        }
        if(!directoryResource.getChild("pom.xml").exists()){
            FileResource<?> pom = (FileResource<?>) directoryResource.getChild("pom.xml");
            pom.setContents(getClass().getResourceAsStream("/pom.xml"));
        }
        shell.println(ShellColor.YELLOW,provider.getMessage("osgi.welcome"));
        return result;
    }


    @Override
    public boolean containsProject(DirectoryResource directoryResource) {
        return osgiFacetInstance.get().isOSGiProject(directoryResource);
    }
}
