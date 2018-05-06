
package app;

import java.io.File;
import java.io.IOException;

import it.sauronsoftware.jave.*;

public class AudioConverter {

	private static String sourceFileName;
	private static String destFileName;

	public static void setUpConversion(String src, String dst) {
		sourceFileName = src;
		destFileName = dst;
	}

	public static String convert() {
		File source = new File(sourceFileName);
		File target = new File(destFileName);
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec("pcm_s16le");
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("wav");
		attrs.setAudioAttributes(audio);
		Encoder encoder = new Encoder();
		if (!target.exists()) {
			target.getParentFile().mkdirs();
			try {
				target.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			encoder.encode(source, target, attrs);
		} catch (IllegalArgumentException | EncoderException e) {
			e.printStackTrace();
		}
		String dst = null;
		try {
			dst = target.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		destFileName = null;
		sourceFileName = null;
		return dst;
	}

}
