package br.ufrgs.rmpestano.intrabundle.plugin;

import org.jboss.forge.project.Project;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.test.SingletonAbstractShellTest;
import org.junit.Before;

/**
 * Created by rmpestano on 1/24/14.
 */
public abstract class BaseOSGiTest extends SingletonAbstractShellTest {



    public Project initializeOSGiProject() throws Exception
    {
        DirectoryResource root = createTempFolder();
        DirectoryResource main = root.getOrCreateChildDirectory("main");
        addBundle(main,"module1");
        addBundle(main,"module2");
        addBundle(main,"module3");
        getShell().setCurrentResource(main);
        return getProject();
    }

    public Project initializeOSGiMavenProject() throws Exception
    {
        DirectoryResource root = createTempFolder();
        DirectoryResource main = root.getOrCreateChildDirectory("main");
        addPom(main);
        addMavenBundle(main, "module1");
        addMavenBundle(main, "module2");
        addMavenBundle(main, "module3");
        getShell().setCurrentResource(main);
        return getProject();
    }

    private void addPom(DirectoryResource root) {
        FileResource<?> pom = (FileResource<?>) root.getChild("pom.xml");
        if(!pom.exists()){
            pom.setContents(getClass().getResourceAsStream("/pomWithDependencies.xml"));
        }
    }


    protected void addBundle(DirectoryResource dir, String module) {
        DirectoryResource bundle = dir.getOrCreateChildDirectory(module);
        addMetaInf(bundle,"/MANIFEST-"+module+".MF");
        addActivator(bundle);
    }

    protected void addMavenBundle(DirectoryResource dir, String module) {
        DirectoryResource bundle = dir.getOrCreateChildDirectory(module);
        DirectoryResource moduleResources = bundle.getOrCreateChildDirectory("src").getOrCreateChildDirectory("main").getOrCreateChildDirectory("resources");
        addMetaInf(moduleResources,"/MANIFEST-"+module+".MF");
        addMavenActivator(bundle);
        addPom(bundle);
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
        activator.setContents("activator content");
    }

    private void addMavenActivator(DirectoryResource root){
        DirectoryResource resource = root.getOrCreateChildDirectory("src").
                getOrCreateChildDirectory("main").getOrCreateChildDirectory("java").
                getOrCreateChildDirectory("br").
                getOrCreateChildDirectory("ufrgs").
                getOrCreateChildDirectory("rmpestano").
                getOrCreateChildDirectory("activator");
        FileResource<?> activator = (FileResource<?>) resource.getChild("Activator.java");
        activator.setContents("activator content");
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
