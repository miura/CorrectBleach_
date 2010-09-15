package emblcmci;

/** Bleach Correction with three different methods, for 2D and 3D time series.
 * 	contact: Kota Miura (miura@embl.de)
 * 
 * 	Simple Ratio Method: 
 * 		Migrated version Jens Rietdorf's macro, 
 * 			see http://www.embl.de/eamnet/html/bleach_correction.html
 * 		Works differently with 2D and 3D time series.
 *
 *  Exponential Fitting Method:
 *  	Similar to MBF-ImageJ method. 
 *  		See http://www.macbiophotonics.ca/imagej/t.htm#t_bleach
 *  	MBF-ImageJ suggests to use "Exponential" equation for fitting, 
 *  	whereas this plugin uses "Exponential with Offset"
 *
 *  HIstogram Matching Method:
 *  	This method does much better restoration of bleaching sequence 
 *  	for segmentation but might not good for intensity quantification.  
 *  	See documentation at http://cmci.embl.de
 *  	works differently with 2D and 3D time series.
 *  
 *  
 * Copyright © 2010 Kota Miura
 * License: GPL 2
 *    
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *   
 * You should have received a copy of the GNU General Public License 
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
		
		/**Correction Method  0: simple ratio 1: exponential fit 2: histogramMatch
		*/
		private static int CorrectionMethod = 0;  

		@Override
		public int setup(String arg, ImagePlus imp) {
			this.imp = imp;
			if (!showDialog()){
					return 0;
			}
			return DOES_8G+DOES_16+STACK_REQUIRED;
		}

		@Override
		public void run(ImageProcessor ip) {
			ImagePlus impdup = new Duplicator().run(imp);
			if 		(CorrectionMethod == 0){			//Simple Ratio Method
				BleachCorrection_SimpleRatio BCSR = new BleachCorrection_SimpleRatio(impdup);
				BCSR.correctBleach();
			}	
			else if (CorrectionMethod == 1){	//Exponential Fitting Method
				BleachCorrection_ExpoFit BCEF = new BleachCorrection_ExpoFit(impdup);
				BCEF.core();
			}
			else if (CorrectionMethod == 2){	//HIstogram Matching Method
				BleachCorrection_MH BCMH = new BleachCorrection_MH();
				BCMH.bleachCorrectionHM(impdup);				
			}
			impdup.show();
		}
		
		/**Dialog to ask which method to be used for Bleach Correction
		 * 
		 * @return
		 */
		public boolean showDialog()	{
			GenericDialog gd = new GenericDialog("Bleach Correction");
			gd.addChoice("Correction Method :", CorrectionMethods , CorrectionMethods[CorrectionMethod]);
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

