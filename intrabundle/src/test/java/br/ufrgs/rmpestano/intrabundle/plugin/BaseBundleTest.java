package br.ufrgs.rmpestano.intrabundle.plugin;

import org.jboss.forge.project.Project;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.test.SingletonAbstractShellTest;
import org.junit.Before;

/**
 * Created by rmpestano on 1/24/14.
 */
public abstract class BaseBundleTest extends SingletonAbstractShellTest {



    public Project initializeOSGiProject() throws Exception{
        DirectoryResource root = createTempFolder();
        DirectoryResource main = root.getOrCreateChildDirectory("module");
        addMetaInf(main, "/MANIFEST.MF");
        addActivator(main);
        addTestClass(main);
        addPermissions(main);
        addExportedPackage(main);
        addHelloManager(main);
        getShell().setCurrentResource(main);
        return getProject();
    }

    private void addHelloManager(DirectoryResource root) {
        DirectoryResource resource = root.getOrCreateChildDirectory("src").
                getOrCreateChildDirectory("br").
                getOrCreateChildDirectory("ufrgs").
                getOrCreateChildDirectory("rmpestano").
                getOrCreateChildDirectory("manager");
        FileResource<?> manager = (FileResource<?>) resource.getChild("HelloManager.java");
        manager.setContents(getClass().getResourceAsStream("/HelloManager.java"));
        FileResource<?> staleManager = (FileResource<?>) resource.getChild("HelloStaleManager.java");
        staleManager.setContents(getClass().getResourceAsStream("/HelloStaleManager.java"));
    }

    private void addPermissions(DirectoryResource root) {
        DirectoryResource resource = root.getOrCreateChildDirectory("OSGI-INF");
        FileResource<?> permissions = (FileResource<?>) resource.getChild("permissions.perm");
        permissions.setContents("permissions content");
    }

    private void addMetaInf(DirectoryResource root,String manifestName) {
        DirectoryResource metaInf = root.getOrCreateChildDirectory("META-INF");
        FileResource<?> fileResource = (FileResource<?>) metaInf.getChild("MANIFEST.MF");
        if(!fileResource.exists()){
            fileResource.setContents(getClass().getResourceAsStream(manifestName));

        }
    }

    private void addActivator(DirectoryResource root){
        DirectoryResource resource = root.getOrCreateChildDirectory("src").
                getOrCreateChildDirectory("br").
                getOrCreateChildDirectory("ufrgs").
                getOrCreateChildDirectory("rmpestano").
                getOrCreateChildDirectory("activator");
        FileResource<?> activator = (FileResource<?>) resource.getChild("Activator.java");
        activator.setContents(getClass().getResourceAsStream("/Activator.java"));
    }

    private void addTestClass(DirectoryResource root) {
        DirectoryResource resource = root.getOrCreateChildDirectory("test").
                getOrCreateChildDirectory("br").
                getOrCreateChildDirectory("ufrgs").
                getOrCreateChildDirectory("rmpestano");
        FileResource<?> testClass = (FileResource<?>) resource.getChild("TestClass.java");
        testClass.setContents("test class content");
    }

    private void addExportedPackage(DirectoryResource root){
     DirectoryResource resource =  root.getOrCreateChildDirectory("src").
               getOrCreateChildDirectory("br").
               getOrCreateChildDirectory("ufrgs").
               getOrCreateChildDirectory("rmpestano").
               getOrCreateChildDirectory("api");
        FileResource<?> exportedInterface = (FileResource<?>)resource.getChild("ExportedClass.java");
        exportedInterface.setContents("public interface Interface{}");
    }


    @Before
    public void initProject(){
        try {
            initializeOSGiProject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
