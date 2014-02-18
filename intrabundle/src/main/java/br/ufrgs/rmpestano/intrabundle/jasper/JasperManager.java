package br.ufrgs.rmpestano.intrabundle.jasper;

import br.ufrgs.rmpestano.intrabundle.i18n.MessageProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.jboss.forge.shell.Shell;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Singleton
public class JasperManager implements Serializable {

	private static final long serialVersionUID = 1L;

    private String fileName;
    private String reportName;
    private List<?> data;
    private Map params;

    @Inject
    Shell shell;

    @Inject
    MessageProvider provider;


    /**
     *
     * @param filename name of the generated PDF file, if not provided reportName.pdf will be used
     * @return
     */
    public JasperManager filename(String filename){
        this.fileName = filename;
        return this;
    }

    /**
     *
     * @param reportName name of the .jasper file
     * @return
     */
    public JasperManager reportName(String reportName){
        this.reportName = reportName;
        return this;
    }


    public JasperManager data(List<?> data){
        this.data = data;
        return this;
    }

    public JasperManager params(Map params){
            this.params = params;
            return this;
    }

    /**
     * build PDF file from .jasper(located on /resources/report/filename) on current directory
     */
	public void build() {
        if(reportName == null || data == null){
            throw new RuntimeException("reportName and data is required to generate the report");
        }
        if(fileName == null){
            fileName = reportName;
        }
        String path = shell.getCurrentDirectory().getFullyQualifiedName()+"/"+fileName+".pdf";
        JasperPrint jp = getJasperPrint();
		try {
			JasperExportManager.exportReportToPdfStream(jp, new FileOutputStream(path));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
        shell.println(provider.getMessage("report.generated",path));
	}

	/**
	 * get JasperPrint from .jasper file
	 *
	 */
	public JasperPrint getJasperPrint() {
		try {
            //reports are placed under /resources/reports
            String path = "/reports/"+reportName;
            if(!path.endsWith(".jasper")){
                path = path.concat(".jasper");
            }
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                throw new RuntimeException("File " + path + " not found");
            }
            return JasperFillManager.fillReport(is, params, new JRBeanCollectionDataSource(data));
		} catch (JRException e) {
			throw new RuntimeException(e);
		}
	}



}
