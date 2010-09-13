import emblcmci.BleachCorrection_MH;
import emblcmci.BleachCorrection_SimpleRatio;
import ij.ImagePlus;
import ij.plugin.Duplicator;
import ij.plugin.PlugIn;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


public class BleachCorrection_by_simpleratio implements PlugInFilter {
	ImagePlus imp;
	@Override
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G+DOES_16+STACK_REQUIRED;
	}

	@Override
	public void run(ImageProcessor ip) {
		//ImagePlus impdup = new Duplicator().run(imp);//, "bleach_corrected") ;
		BleachCorrection_SimpleRatio BCMH = new BleachCorrection_SimpleRatio(imp);
		ImagePlus correctedimp = BCMH.correctBleach();
		//ImagePlus correctedimp = BCMH.getImp();
		if (correctedimp != null)
			correctedimp.show();
	}

}
