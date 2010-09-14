package emblcmci;

/** Bleach Correction Algorithm with Simple Ratio Calculation. 
 *  Based on 2D algorithm of ImageJ Macro written by Jens Rietdorf
 *  
 *  this plugin works on 2D and 3D time series (for 3D time series, it should be a hyperstack). 
 *  in case of 3D times series, mean intensity in the first time point stack becomes the reference.
 *  @author Kota Miura (miura@embl.de)
 */

import ij.IJ;
import ij.ImagePlus;
//import ij.gui.Roi;
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
/*		Roi curROI = imp.getRoi();
		System.out.println("in the method");
		if (curROI != null) {
			java.awt.Rectangle rect = curROI.getBounds();
			System.out.println("(x,y)=(" + rect.x + ","	+ rect.y); 
			System.out.println("Width="+ rect.width);
			System.out.println("Height="+ rect.height);
		} else {
			System.out.println("No ROI");
		}
*/
		boolean is3DT = false; 
		int zframes = 1;
		int timeframes = 1;
		int[] impdimA = imp.getDimensions();
		IJ.log("slices"+Integer.toString(impdimA[3])+"  -- frames"+Integer.toString(impdimA[4]));
		//IJ.log(Integer.toString(imp.getNChannels())+":"+Integer.toString(imp.getNSlices())+":"+ Integer.toString(imp.getNFrames()));
		if (impdimA[3]>1 && impdimA[4]>1){	// if slices and frames are both more than 1
			is3DT =true;
			zframes = impdimA[3];
			timeframes = impdimA[4];
			if ((zframes*timeframes) != imp.getStackSize()){
				IJ.showMessage("slice and time frames do not match with the length of the stack. Please correct!");
				return null;
			}
		}
		
		ImageStatistics imgstat = new ImageStatistics();
		ImageProcessor curip;
		double currentInt = 0.0;
		double ratio = 1.0;
		if (!is3DT) {
			for (int i = 0; i < imp.getStackSize(); i++){
				curip = imp.getImageStack().getProcessor(i+1);
	//			if (curROI != null) curip.setRoi(curROI);
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
		} else {
			for (int i = 0; i < timeframes; i++){
				currentInt = 0.0;
				for (int j = 0; j < zframes; j++) {
					curip = imp.getImageStack().getProcessor(i * zframes + j + 1);
					imgstat = curip.getStatistics();
					currentInt += imgstat.mean;					
				}
				currentInt /= zframes;
				if (i == 0) {
					referenceInt = currentInt;					
				} else {
					ratio = referenceInt / currentInt;
					for (int j = 0; j < zframes; j++) {
						curip = imp.getImageStack().getProcessor(i * zframes + j + 1);
						curip.multiply(ratio);
					}
					System.out.println("frame"+i+1+ ": mean int="+ currentInt +  " ratio=" + ratio);
				}					
			}		
		}
		
		return imp;
	}

	
}
