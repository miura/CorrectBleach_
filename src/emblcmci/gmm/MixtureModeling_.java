package emblcmci.gmm;

/*
 * Mixture Modeling algorithm
 *
 * Copyright (c) 2003 by Christopher Mei (christopher.mei@sophia.inria.fr)
 *                    and Maxime Dauphin
 *
 * This plugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this plugin; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
/* This code was downloaded from
 * http://rsbweb.nih.gov/ij/plugins/mixture-modeling.html
 * 
 */

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.gui.*;
import ij.plugin.frame.PlugInFrame;

import java.awt.*;
import java.util.*;
import java.lang.*;

/**
 *  This algorithm thresholds the image using a gray-level 
 *  histogram Gaussian characterisation.
 *  
 **/

public class MixtureModeling_ implements PlugInFilter {
	private int threshold;
	final static int HMIN = 0;
	final static int HMAX = 256;
	private boolean runHistogram;

	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about"))
		{showAbout(); return DONE;}

		if(arg.equals("")) {
			runHistogram = true;
			return DOES_8G+DOES_STACKS+SUPPORTS_MASKING+NO_CHANGES;
		}
		else {
			int val1 = arg.indexOf("true");
			int val2 = arg.indexOf("false");
			//IJ.write("Ok");
			if ((val1 == -1)&&(val2 == -1)) {
				IJ.showMessage("Wrong parameters for TreeWatershed.");
				return DONE;
			}
			else {
				if ((val1 != -1)&&(val2 != -1)) {
					IJ.showMessage("Wrong parameters for TreeWatershed.");
					return DONE;
				}
				if (val1 != -1)
					runHistogram = true;
				else
					runHistogram = false;
			}
		}

		return DOES_8G+DOES_STACKS+SUPPORTS_MASKING+NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		boolean debug = false;

		GrayLevelClassMixtureModeling classes = new GrayLevelClassMixtureModeling((ByteProcessor) ip);

		double sigmaMax = 0;
		int threshold = 0;

		float error = 0;
		float errorMin = 9999999;
		float mu1 = 0, mu2 = 0, variance1 = 0, variance2 = 0;

		/** Start  **/
		while(classes.addToIndex()) {
			error = calculateError(classes);

			//IJ.write("Error "+i+" : "+error+", threshold : "+C1.getThreshold());

			if(error<errorMin) {
				errorMin = error;
				threshold = classes.getThreshold();
				mu1 = classes.getMu1();
				variance1 = classes.getVariance1();
				mu2 = classes.getMu2();
				variance2 = classes.getVariance2();
				//IJ.write(""+C1+C2+"\n");
			}
		}
		classes.setIndex(threshold);

		if(runHistogram)
			affHist(classes);

		IJ.log("Mu1 : "+mu1+", variance1 : "+variance1);
		IJ.log("Mu2 : "+mu2+", variance2 : "+variance2);
		IJ.log("ErrorMin : "+errorMin);
		IJ.log("Diff Mu : "+(mu2-mu1));
		IJ.log("Direct threshold : "+threshold);
		IJ.log("Real threshold : "+findThreshold((int)mu1, (int)mu2, classes));

		threshold(ip, (int)findThreshold((int)mu1, (int)mu2, classes) );
	}

	private float findThreshold(int mu1, int mu2, GrayLevelClassMixtureModeling classes) {
		float min = 9999999;
		int threshold = 0;

		for(int i=mu1; i<mu2; i++) {
			float val = (float)Math.pow(classes.differenceGamma(i),2); 
			if(min>val) {
				min = val;
				threshold = i;
			}
		}
		return threshold;
	}

	private float calculateError(GrayLevelClassMixtureModeling classes) {
		float error = 0;

		for(int i=0; i<=GrayLevelClassMixtureModeling.MAX; i++) {
			error += Math.pow(classes.gamma(i)-GrayLevelClassMixtureModeling.getHistogram(i),2);
		}

		return error/(GrayLevelClassMixtureModeling.MAX+1);
	}

	void showAbout() {
		IJ.showMessage("About MixtureModeling_...",
				"This plug-in filter calculates the mixtureModeling of a 8-bit images.\n"
				);
	}  


	//************************************************************
	//* Affiche un histogramme avec les classes
	//************************************************************
	public void affHist(GrayLevelClassMixtureModeling classes)
	{
		int max = maxi();
		ImagePlus imp = NewImage.createRGBImage ("Histogram", 256, 100, 1, NewImage.FILL_WHITE);
		ImageProcessor nip = imp.getProcessor();
		int pixel=0; //pixel courant

		int red   = 255;
		int green = 0;
		int blue  = 0;
		int gamma1 = ((red & 0xff) << 16) + ((green & 0xff) << 8) + (blue & 0xff);
		red   = 0;
		green = 0;
		blue  = 255;
		int gamma2 = ((red & 0xff) << 16) + ((green & 0xff) << 8) + (blue & 0xff);
		red = 0; green = 0; blue = 0;
		int hist = ((red & 0xff) << 16) + ((green & 0xff) << 8) + (blue & 0xff);

		for (int x = 0; x < 256 ; x++)
		{
			float approx1 = classes.gamma1(x);
			double app1 = 100.0 - ((double)(approx1) /(double)(max)) * 100.0;
			float approx2 = classes.gamma2(x);
			double app2 = 100.0 - ((double)(approx2) /(double)(max)) * 100.0;

			double t = 100.0 - ((double)(GrayLevelClassMixtureModeling.histogram[x]) /(double)(max)) * 100.0;
			for (int y = 100; y > (int)(t); y--)
				nip.putPixel(x,y,hist);

			nip.putPixel(x,(int)app1,gamma1);
			nip.putPixel(x,(int)app2,gamma2);
		}

		imp.show();
	}//fin affHist

	//************************************************************
	//* Recherche d'un maximun dans un tableau de int
	//************************************************************
	private int maxi()
	{
		int max = 0;
		for (int i=0;i<GrayLevelClassMixtureModeling.histogram.length;i++)
			if (GrayLevelClassMixtureModeling.histogram[i]>max) max = GrayLevelClassMixtureModeling.histogram[i];
		return max;
	}//fin maxi


	//************************************************************
	//* Threshold the image according to the mask
	//************************************************************

	public void threshold(ImageProcessor in, int threshold)
	{
		ImagePlus imp = null;
		imp = NewImage.createByteImage ("Threshold", in.getWidth(), in.getHeight(), 1, NewImage.FILL_WHITE);

		ImageProcessor nip = imp.getProcessor();

		for (int x = 0; x < in.getWidth() ; x++) {
			for (int y = 0; y < in.getHeight() ; y++) {
				if (in.getPixel(x,y)> threshold)
					nip.putPixel(x, y, 255);
				else
					nip.putPixel(x, y, 0);
			}
		}
		imp.show();
	}//fin affclassRGB
}

