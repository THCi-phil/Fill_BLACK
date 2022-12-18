/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
  *
 * Invert_image image code 2022 Prof Phil Threlfall-Holmes, TH Collaborative Innovation
 * modification from tutorial template, licence terms unmodified.
 */

package com.pthci.imagej;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.process.ColorProcessor;


public class Fill_BLACK implements PlugInFilter {
	protected ImagePlus image;
	boolean   process_all_Slices = true;

	// image property members
	private int width   ;
	private int height  ;
	private int type    ;
	private int nSlices ;
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about")) {
			showAbout();
			return DONE;
		}
		if (arg.equals("CURRENT_SLICE")) {
			process_all_Slices = false;
		}
		image = imp;		
		return DOES_8G | DOES_16 | DOES_32 | DOES_RGB;
	} //end public int setup(String arg, ImagePlus imp)
	//-----------------------------------------------------


	@Override
	public void run(ImageProcessor ip) {
		width   = ip.getWidth();    //in pixel units
		height  = ip.getHeight();
		type    = image.getType();
		nSlices = image.getStackSize();
		if( process_all_Slices ) {
			process(image);
		}else{
			process(ip);
		}
		image.updateAndDraw();
	} //end public void run(ImageProcessor ip)
	//-----------------------------------------------------


	/**
	 * Process an image.
	 * <p>
	 * Please provide this method even if {@link ij.plugin.filter.PlugInFilter} does require it;
	 * the method {@link ij.plugin.filter.PlugInFilter#run(ij.process.ImageProcessor)} can only
	 * handle 2-dimensional data.
	 * </p>
	 * <p>
	 * If your plugin does not change the pixels in-place, make this method return the results and
	 * change the {@link #setup(java.lang.String, ij.ImagePlus)} method to return also the
	 * <i>DOES_NOTHING</i> flag.
	 * </p>
	 *
	 * @param image the image (possible multi-dimensional)
	 */
	public void process(ImagePlus image) {
		// slice numbers start with 1 for historical reasons
		//skip straight to the action, to avoid nSlice tests of image type
    if      (type == ImagePlus.GRAY8    ) for (int i = 1; i <= nSlices; i++) process( (byte[])  image.getStack().getProcessor(i).getPixels() );
		else if (type == ImagePlus.GRAY16   ) for (int i = 1; i <= nSlices; i++) process( (short[]) image.getStack().getProcessor(i).getPixels() );
		else if (type == ImagePlus.GRAY32   ) for (int i = 1; i <= nSlices; i++) process( (float[]) image.getStack().getProcessor(i).getPixels() );
		else if (type == ImagePlus.COLOR_RGB) for (int i = 1; i <= nSlices; i++) process( (int[])   image.getStack().getProcessor(i).getPixels() );                             
		else {
			throw new RuntimeException("image type not supported");
		}
	} //end public void process(ImagePlus image) 
	//-----------------------------------------------------


	// Select processing method depending on image type
	public void process(ImageProcessor ip) {
		if      (type == ImagePlus.GRAY8    ) process( (byte[])  ip.getPixels() );
		else if (type == ImagePlus.GRAY16   ) process( (short[]) ip.getPixels() );
		else if (type == ImagePlus.GRAY32   ) process( (float[]) ip.getPixels() );
		else if (type == ImagePlus.COLOR_RGB) process( (int[])   ip.getPixels() );                             
		else {
			throw new RuntimeException("not supported");
		}
	} //end public void process(ImageProcessor ip)
	//-----------------------------------------------------


	// processing of GRAY8 images
	public void process(byte[] pixels) {
		for( int pixelPos=0; pixelPos<(width*height); pixelPos++ ) {
			pixels[pixelPos] = (byte)0 ;
		}
	} //end public void process(byte[] pixels)
  //-----------------------------------------------------


	// processing of GRAY16 images
	public void process(short[] pixels) {
		for( int pixelPos=0; pixelPos<(width*height); pixelPos++ ) {
			pixels[pixelPos] = (short)0 ;
		}
	} //end public void process(short[] pixels)
  //-----------------------------------------------------


	// processing of GRAY32 images	
	public void process( float[] pixels ) {
		for( int pixelPos=0; pixelPos<(width*height); pixelPos++ ) {
			pixels[pixelPos] = (float)0.0 ;
		}
	} //end public void process(float[] pixels)
  //-----------------------------------------------------


	// processing of COLOR_RGB images
	public void process(int[] pixels ) {
		ColorProcessor cp = new ColorProcessor(width, height, pixels);
		
		byte[] R = new byte[ width*height ];
		byte[] G = new byte[ width*height ];
		byte[] B = new byte[ width*height ];
		process(R);
		process(G);
		process(B);
		cp.setRGB( R, G, B );
	} //end public void process(int[] pixels)
  //-----------------------------------------------------


/*=================================================================================*/


	public void showAbout() {
		IJ.showMessage("Fill_BLACK",
			             "Set each pixel in image to 0"
		+ "\n"        +"works with 8, 16 or 32 bit greysacle, and colorRGB"
		+ "\n"        +"setup( String args, ImagePlus img )"
		+ "\n"        +"args can be ALL_SLICES or CURRENT_SLICE"
		+ "\n"        +"default no option set is ALL_SLICES"
		);
	} //end public void showAbout()
  //-----------------------------------------------------


/*=================================================================================*/

	/**
	 * Main method for debugging.
	 *
	 * For debugging, it is convenient to have a method that starts ImageJ, loads
	 * an image and calls the plugin, e.g. after setting breakpoints.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) throws Exception {
		// set the plugins.dir property to make the plugin appear in the Plugins menu
		// see: https://stackoverflow.com/a/7060464/1207769
		Class<?> clazz = Fill_BLACK.class;
		java.net.URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
		java.io.File file = new java.io.File(url.toURI());
		System.setProperty("plugins.dir", file.getAbsolutePath());

		// start ImageJ
		new ImageJ();

		//ImagePlus image = IJ.openImage("d:/vertical spray test_bkgndRmvd.tif");
		//ImagePlus image = IJ.openImage("d:/test16bitBandWinvert.tif");
		//ImagePlus image = IJ.openImage("d:/test32bitBandWinvert.tif");
		//ImagePlus image = IJ.openImage("d:/testRGB.tif");
		
		// open the Clown sample
		ImagePlus image = IJ.openImage("http://imagej.net/images/clown.jpg");
		image.show();

		// run the plugin
		IJ.runPlugIn(clazz.getName(), "");
	}  //end public static void main(String[] args)
  
/*=================================================================================*/
  
}  //end public class Fill_BLACK
//========================================================================================
//                         end public class Fill_BLACK
//========================================================================================