package emblcmci;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

public class BleachCorrection_SimpleRatio {
	ImagePlus imp;
	double referenceInt = 0;
	/**
	 * @param imp ImagePlus instance
	 */
	public BleachCorrection_SimpleRatio(ImagePlus imp) {
		super();
		this.imp = imp;
	}
	
	public ImagePlus correctBleach(){
		Roi curROI = imp.getRoi();
		System.out.println("in the method");
		if (curROI != null) {
			java.awt.Rectangle rect = curROI.getBounds();
			System.out.println(rect.x + ","
					+ rect.y + "," 
					+ rect.width + ","
					+ rect.height);
		} else {
			System.out.println("ROI is null");
		}
		ImageStatistics imgstat = new ImageStatistics();
		ImageProcessor curip;
		double currentInt = 0.0;
		double ratio = 1.0;
		for (int i = 0; i < imp.getStackSize(); i++){
			curip = imp.getImageStack().getProcessor(i+1);
			if (curROI != null) curip.setRoi(curROI);
			imgstat = curip.getStatistics();
			if (i == 0) {
				referenceInt = imgstat.mean;
				System.out.println("ref intensity=" + imgstat.mean);
			} else {
				currentInt = imgstat.mean;
				
				ratio = referenceInt / currentInt;
				curip.multiply(ratio);
				System.out.println("frame"+i+1+ "mean int="+ currentInt +  " ratio=" + ratio);
			}
			
		}
//		if (curROI.isArea())
//			System.out.println("ROI is an area");
		return imp;
}

	public ImagePlus getImp() {
		return imp;
	}

	public void setImp(ImagePlus imp) {
		this.imp = imp;
	}
	
	
	
	
}
