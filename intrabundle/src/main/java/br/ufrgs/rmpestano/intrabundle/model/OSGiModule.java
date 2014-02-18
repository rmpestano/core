package br.ufrgs.rmpestano.intrabundle.model;

import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rmpestano on 1/28/14.
 */
public interface OSGiModule extends Serializable{

    /**
     *
     * @return total .java files(under src or src/main/java) lines of code
     */
    Long getLinesOfCode();

    /**
     *
     * @return total .java files(under test or src/test/ava) test lines of code
     */
    Long getLinesOfTestCode();

    /**
     * @return <code>true</code> if bundle uses declarative services specification
     * <code>false</code> if it doesnt
     */
    Boolean getUsesDeclarativeServices();

    /**
     * @return <code>true</code> if bundle uses Blueprint specification
     * <code>false</code> if it doesnt
     */
    Boolean getUsesBlueprint();

    /**
     *
     * @return bundle MANIFEST.MF file
     */
    FileResource<?> getManifest();

    /**
     *
     * @return bundle activator java file
     */
    FileResource<?> getActivator();

    /**
     *
     * @return bundle imported packages
     */
    List<String> getImportedPackages();

    /**
     *
     * @return bundle exported packages
     */
    List<String> getExportedPackages();

    /**
     *
     * @return bundle required bundles
     */
    List<String> getRequiredBundles();

    /**
     * @return <code>true</code> if bundle exported packages contains only interfaces
     * <code>false</code> if it has one or more classes
     */
    Boolean getPublishesInterfaces();

    /**
     *
     * @return <code>true</code> if bundle declares permissions
     * <code>false</code> otherwise
     */
    Boolean getDeclaresPermissions();

    /**
     *
     * @return .java files possibly containing OSGi service stale references
     */
    List<Resource<?>> getStaleReferences();

}
