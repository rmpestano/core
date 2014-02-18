package br.ufrgs.rmpestano.intrabundle.plugin;

import br.ufrgs.rmpestano.intrabundle.i18n.MessageProvider;
import br.ufrgs.rmpestano.intrabundle.locator.BundleProjectLocator;
import br.ufrgs.rmpestano.intrabundle.util.ProjectUtils;
import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.shell.util.OSUtils;
import org.jboss.forge.test.SingletonAbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

import javax.inject.Inject;
import java.io.File;

public class BundlePluginTest extends BaseBundleTest {


    @Inject
    MessageProvider provider;

    @Deployment
    public static JavaArchive getDeployment() {
        JavaArchive jar = SingletonAbstractShellTest.getDeployment()
                .addPackages(true, "br.ufrgs.rmpestano.intrabundle.facet")
                .addPackages(true, "br.ufrgs.rmpestano.intrabundle.model")
                .addPackages(true, "br.ufrgs.rmpestano.intrabundle.i18n")
                .addPackages(true, "br.ufrgs.rmpestano.intrabundle.event")
                .addPackages(true, "br.ufrgs.rmpestano.intrabundle.annotation")
                .addPackages(true, "br.ufrgs.rmpestano.intrabundle.jdt")
                .addClass(ProjectUtils.class)
                .addClass(BundlePlugin.class).
                addClass(LocalePlugin.class).
                addClass(BundleProjectLocator.class);
        System.out.println(jar.toString(true));
        return jar;
    }

    @Test
    public void shouldUseDeclarativeServices() throws Exception {
        resetOutput();
        getShell().execute("bundle usesDeclarativeServices");
        Assert.assertTrue(getOutput().contains(provider.getMessage("yes")));
    }

    @Test
    public void shouldPublishInterfaces() throws Exception {
        resetOutput();
        getShell().execute("bundle publishesInterfaces");
        Assert.assertTrue(getOutput().contains(provider.getMessage("yes")));
    }

    @Test
    public void shouldDeclarePermissions() throws Exception{
        resetOutput();
        getShell().execute("bundle declaresPermissions");
        Assert.assertTrue(getOutput().contains(provider.getMessage("yes")));
    }

    @Test
    public void shouldNotUseBlueprint() throws Exception{
        resetOutput();
        getShell().execute("bundle usesBlueprint");
        Assert.assertTrue(getOutput().contains(provider.getMessage("no")));
    }

    @Test
    public void shouldFindActivator() throws Exception {
        resetOutput();
        getShell().execute("bundle activator");
        String activatorPath = "/module/src/br/ufrgs/rmpestano/activator/Activator.java";
        if(OSUtils.isWindows()){
            activatorPath = activatorPath.replaceAll("/", File.separator + File.separator);
        }
        Assert.assertTrue(getOutput().contains(activatorPath));
    }

    @Test
    public void shouldGetExportedPackages() throws Exception {
        resetOutput();
        getShell().execute("bundle exportedPackages");
        Assert.assertTrue(getOutput().contains("br.ufrgs.rmpestano.api"));
    }

    @Test
    public void shouldGetImportedPackages() throws Exception {
        resetOutput();
        getShell().execute("bundle importedPackages");
        Assert.assertTrue(getOutput().contains("org.osgi.framework"));
    }

    @Test
    public void shouldCountLinesOfCode() throws Exception {
        resetOutput();
        getShell().execute("bundle loc");
        Assert.assertTrue(getOutput().startsWith("2"));
    }

    @Test
    public void shouldFindRequiredBundle() throws Exception {
        resetOutput();
        getShell().execute("bundle requiredBundles");
        Assert.assertTrue(getOutput().contains("org.apache.tuscany.sdo.spec;visibility:=reexport"));
    }

    /**
     * lines of test code test
     */
    @Test
    public void shouldCountLinesOfTest() throws Exception {
        resetOutput();
        getShell().execute("bundle lot");
        Assert.assertTrue(getOutput().startsWith("1"));
    }

    @Test
    public void shouldContainStaleReferences() throws Exception {
        resetOutput();
        getShell().clear();
        getShell().execute("bundle staleReferences");
        Assert.assertTrue(getOutput().contains("HelloStaleManager.java"));
    }

}
