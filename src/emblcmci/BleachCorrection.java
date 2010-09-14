package emblcmci;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.Duplicator;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import emblcmci.BleachCorrection_ExpoFit;
import emblcmci.BleachCorrection_MH;
import emblcmci.BleachCorrection_SimpleRatio;


public class BleachCorrection implements PlugInFilter {
		ImagePlus imp;
		String[] CorrectionMethods =  { "Simple Ratio", "Exponential Fit", "Histogram Matching" }; 
		private static int CorrectionMethod = 0; // 0: simple ratio 1: exponential fit 2: histogramMatch 

		@Override
		public int setup(String arg, ImagePlus imp) {
			this.imp = imp;
			showDialog();
			return DOES_8G+DOES_16+STACK_REQUIRED;
		}

		@Override
		public void run(ImageProcessor ip) {
			ImagePlus impdup = new Duplicator().run(imp);//, "bleach_corrected") ;
			if 		(CorrectionMethod == 0){			//simple ratio
				BleachCorrection_SimpleRatio BCSR = new BleachCorrection_SimpleRatio(impdup);
				BCSR.correctBleach();
				//ImagePlus correctedimp = BCMH.correctBleach();			
			}	
			else if (CorrectionMethod == 1){	//exponential fitting
				BleachCorrection_ExpoFit BCEF = new BleachCorrection_ExpoFit(impdup);
				//BCEF.dcayFitting();
				BCEF.core();
			}
			else if (CorrectionMethod == 2){	//HIstogram Matching
				BleachCorrection_MH BCMH = new BleachCorrection_MH();
				BCMH.bleachCorrectionHM(impdup);				
			}
			impdup.show();
		}
		
		public boolean showDialog()	{
			GenericDialog gd = new GenericDialog("Bleach Correction");
			gd.addChoice("Correction Method :", CorrectionMethods , CorrectionMethods[CorrectionMethod]);
			//gd.addNumericField("Min Spot Size for Segmentation (3Dobject) :", this.getMinspotvoxels(), 0);	
			gd.showDialog();
			if (gd.wasCanceled()) 
				return false;
			BleachCorrection.setCorrectionMethod(gd.getNextChoiceIndex());
			return true;
			
		}

		public static int getCorrectionMethod() {
			return CorrectionMethod;
		}

		public static void setCorrectionMethod(int correctionMethod) {
			CorrectionMethod = correctionMethod;
		}

}

