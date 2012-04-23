package emblcmci;

import static org.junit.Assert.*;
import ij.IJ;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import org.junit.Before;
import org.junit.Test;

public class BleachCorrection_ExpoFitTest {

	private BleachCorrection_ExpoFit bcex;
	private ImagePlus imp;

	@Before
	public void testBleachCorrection_ExpoFitImagePlus() {
		ImagePlus imp = IJ.openImage("Z:\\bory\\100505\\dotsplit C=0.tif");
		BleachCorrection_ExpoFit bcex = new BleachCorrection_ExpoFit(imp);
		this.bcex = bcex;
		this.imp = imp;
	}

//	@Test
	public void testBleachCorrection_ExpoFitImagePlusRoi() {
		fail("Not yet implemented");
	}

//	@Test
	public void testDcayFitting() {
		fail("Not yet implemented");
	}
//	@Test
	public void testDecayFitting3D() {
		fail("Not yet implemented");
	}

	@Test
	public void testCalcExponentialOffset() {
		ImageProcessor ip = this.imp.getImageStack().getProcessor(1);
		FloatProcessor fp = bcex.calcExponentialOffset(ip, 0.12, 66, 100);
		String ts = fp.toString();
		IJ.log(ts);
		float[] fpa = (float[]) fp.getPixels();
//		for (int i = 0; i< fpa.length; i++){
//			IJ.log(Float.toString(fpa[i]));
//		}
	}

//	@Test
	public void testCore() {
		fail("Not yet implemented");
	}

//	@Test
	public void testCore2() {
		this.bcex.core2();
		this.imp.show();
	}

}
