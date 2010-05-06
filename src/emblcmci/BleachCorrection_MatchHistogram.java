package emblcmci;
/**
 * based on original:"Match_To_Image_Histogram.java"(http://www.imagingbook.com)
 * original package "histogram2" is untouched, and required for the plugin to run.  
 * 
 *  
 */

import histogram2.HistogramMatcher;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class BleachCorrection_MatchHistogram implements PlugIn { 
	
	public ImagePlus imp = DuplicateStack(WindowManager.getCurrentImage()) ;
	public void run(String arg) {
		if (imp.getBitDepth()!= 8 && imp.getBitDepth()!=16){
			IJ.showMessage("should be 8 or 16 bit image");
			return;
		}
		if (imp.getStackSize()<2) {
			IJ.showMessage("need a stack!");
			return;
		}
		boolean is3DT = false; 
		int[] impdimA = imp.getDimensions();
		if (impdimA[3]>1 && impdimA[4]>1){	// if slices and frames are both more than 1
			is3DT =true;
			
		}
		ImageStack stack = imp.getStack();
		ImageProcessor ipA = null;
		ImageProcessor ipB = null;
		HistogramMatcher m = new HistogramMatcher();
		int[] hA = null;
		int[] hB = null;
		int[] F = null;
		//IJ.log(Integer.toString(stack.getSize()));
		
		if (is3DT){
			//should implement here, 
		} else {

			for (int i=0; i<stack.getSize(); i++){
				if (i==0) {
					ipB = stack.getProcessor(i+1);
					hB = ipB.getHistogram();
				}
				else {
					ipA = stack.getProcessor(i+1);
					hA = ipA.getHistogram();
					F = m.matchHistograms(hA, hB);
					ipA.applyTable(F);
					IJ.log("corrected frame: "+Integer.toString(i+1));
				}
			}
		}
		imp.show();
	}
	//duplicate and make another instance of imp
	public ImagePlus DuplicateStack(ImagePlus ims){
		ImageStack stack = ims.getImageStack();
		
		ImageStack dupstack = ims.createEmptyStack();
		//ImageStack dupstack = new ImageStack(stack.getWidth(), stack.getHeight());
		for (int i=0; i<stack.getSize(); i++){
				dupstack.addSlice(Integer.toString(i), stack.getProcessor(i+1).duplicate(), i); 
		}
		ImagePlus dupimp = new ImagePlus("bleach_corrected", dupstack);
		//dupimp.show();
		return dupimp;
	}	
}

