package br.ufrgs.rmpestano.intrabundle.facet;

import br.ufrgs.rmpestano.intrabundle.model.OSGiModule;
import br.ufrgs.rmpestano.intrabundle.util.ProjectUtils;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFilter;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * Created by rmpestano on 1/22/14.
 */
@Singleton
public class BundleFacet extends BaseFacet {


    @Override
    public boolean install() {

        return isInstalled();
    }

    @Override
    public boolean isInstalled() {
         return project != null && isOsgiBundle(project.getProjectRoot());
    }

    public boolean isOsgiBundle(DirectoryResource projectRoot){
        Resource<?> manifestHome = ProjectUtils.getProjectManifestFolder(projectRoot);
        if(manifestHome == null || !manifestHome.exists()){
            return false;
        }
        List<Resource<?>> manifestCandidate =  manifestHome.listResources(new ResourceFilter() {
            @Override
            public boolean accept(Resource<?> resource) {
                return resource.getName().toLowerCase().endsWith(".mf");
            }
        });
        if(manifestCandidate == null || manifestCandidate.isEmpty()){
           return false;
        }
        Resource<?> manifest = manifestCandidate.get(0);
        if(!manifest.exists()){
            return false;
        }
        RandomAccessFile randomAccessFile;
        try {
            File f = new File(manifest.getFullyQualifiedName());
            randomAccessFile = new RandomAccessFile(f,"r");
            return hasOsgiConfig(randomAccessFile);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean hasOsgiConfig(RandomAccessFile aFile) throws IOException {
        String line;
        while((line = aFile.readLine())!=null){
            if(line.contains("Bundle-Version")){
                return true;
            }
        }
        return false;
    }

    @Produces
    public OSGiModule produceCurrentBundle(){
        return (OSGiModule) getProject();
    }

}
