package com.voyagegames.bachatamusicality;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Utilities {
	
	public static double roundTo(final double value, final int places) {
	    if (places < 0) {
	    	throw new IllegalArgumentException("Decimal places specified was less than 0");
	    }

	    final long factor = (long) Math.pow(10, places);
	    final long tmp = Math.round(value * factor);
	    
	    return (double) tmp / factor;
	}
	
	public static double kilometersToMiles(final double km) {
		return km * 0.621371;
	}
	
	public static double milesToKilometers(final double mi) {
		return mi * 1.60934;
	}
	
	public static String exceptionToString(final Exception e) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	public static byte[] compress(final byte[] bytes) throws IOException {
		final Deflater compressor = new Deflater();
		compressor.setLevel(Deflater.BEST_SPEED);
	    compressor.setInput(bytes);
	    compressor.finish();

	    /*
	     * Create an expandable byte array to hold the compressed data.
	     * You cannot use an array that's the same size as the orginal because
	     * there is no guarantee that the compressed data will be smaller than
	     * the uncompressed data.
	     */
		final ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
	    final byte[] buf = new byte[1024];
	    
	    while (!compressor.finished()) {
	        final int count = compressor.deflate(buf);
	        bos.write(buf, 0, count);
	    }
	
	    return bos.toByteArray();
	}
	
	public static byte[] decompress(final byte[] bytes) throws IOException {
		final Inflater decompressor = new Inflater();
	    decompressor.setInput(bytes);

	    final ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
        final byte[] buf = new byte[1024];
        
        while (!decompressor.finished()) {
            try {
                final int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            } catch (final DataFormatException e) {
                throw new RuntimeException(e);
            }
        }

        return bos.toByteArray();
	}

}
