import ij.ImagePlus;
import ij.plugin.Duplicator;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import emblcmci.BleachCorrection_MH;

/** implementing interface to ImageJ plugin
 * 
 *  main part is within embltools package. 
 * @author Miura
 *
 */

public class BleachCorrection_MatchHistogram implements PlugInFilter {
	ImagePlus imp;
	@Override
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G+DOES_16+STACK_REQUIRED;
	}

	@Override
	public void run(ImageProcessor ip) {
		ImagePlus impdup = new Duplicator().run(imp);//, "bleach_corrected") ;
		BleachCorrection_MH BCMH = new BleachCorrection_MH();
		BCMH.bleachCorrectionHM(impdup);
		impdup.show();
	}

}
