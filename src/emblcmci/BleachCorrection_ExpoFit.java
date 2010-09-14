package emblcmci;

/** Bleach Correction by Fitting Exponential Decay function.
 *  Kota Miura (miura@embl.de)
 * 
 * 	unlike 'simple ratio' and 'histogram matching' this method processes
 *  both 2D and 3D time series in same way, both treated as a single series
 *  of bleaching. This might not be appropriate when time interval is large. 
 *  3D processing part should be added. 
 *  
 *  TODO create another method "decayFitting3D"
 *  
 *  
 */

import ij.IJ;
import ij.ImagePlus;
import ij.measure.CurveFitter;
import ij.plugin.frame.Fitter;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;


public class BleachCorrection_ExpoFit {
	ImagePlus imp;

	/**
	 * @param imp
	 */
	public BleachCorrection_ExpoFit(ImagePlus imp) {
		super();
		this.imp = imp;
	}
	
	/** Fit the mean intensity time series of given ImagePlus in this class.
	 * fit equation is 11, parameter from 
	 * 		http://rsb.info.nih.gov/ij/developer/api/constant-values.html#ij.measure.CurveFitter.STRAIGHT_LINE
	 * @return an instance of CurveFitter
	 */
	public CurveFitter dcayFitting(){
		ImageProcessor curip;
		ImageStatistics imgstat;
		double[] xA = new double[imp.getStackSize()];
		double[] yA = new double[imp.getStackSize()];		
		for (int i = 0; i < imp.getStackSize(); i++){
			curip = imp.getImageStack().getProcessor(i+1);
//			if (curROI != null) curip.setRoi(curROI);
			imgstat = curip.getStatistics();
			xA[i] = i; 
			yA[i] =	imgstat.mean;			
		}
		CurveFitter cf = new CurveFitter(xA, yA);
		double firstframeint = yA[0];
		double lastframeint = yA[yA.length-1];
		double guess_a = firstframeint - lastframeint; 
		if (guess_a <= 0){
			IJ.error("This sequence seems to be not decaying");
			return null;
		}
		double guess_c = lastframeint;
		double maxiteration = 2000;
		double NumRestarts = 2;
		double errotTol = 10;
		double[] fitparam = {guess_a, -0.0001, guess_c, maxiteration, NumRestarts, errotTol};
		
		//cf.setInitialParameters(fitparam);
		cf.doFit(11); // 
		Fitter.plot(cf);
		IJ.log(cf.getResultString());
		return cf;
	}

	/** calculate estimated value from fitted "Exponential with Offset" equation
	 * 
	 * @param a  magnitude (difference between max and min of curve)
	 * @param b  exponent, defines degree of decay
	 * @param c  offset. 
	 * @param x  timepoints (or time frame number)
	 * @return estimate of intensity at x
	 */
	public double calcExponentialOffset(double a, double b, double c, double x){
		return (a * Math.exp(-b*x) + c);
	}
	
	/** does both decay fitting and bleach correction. 
	 * 
	 */
	public void core(){
		CurveFitter cf = dcayFitting();
		double[] respara = cf.getParams(); 
		double res_a = respara[0];
		double res_b = respara[1];
		double res_c = respara[2];
		double ratio = 0.0;
		ImageProcessor curip;
		System.out.println(res_a + "," + res_b + "," + res_c);
		for (int i = 0; i < imp.getStackSize(); i++){
			curip = imp.getImageStack().getProcessor(i+1);
			ratio = calcExponentialOffset(res_a, res_b, res_c, 0.0) / calcExponentialOffset(res_a, res_b, res_c, (double) (i + 1));
			curip.multiply(ratio);
		}
	}
	

	
}
