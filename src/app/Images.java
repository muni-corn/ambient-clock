
package app;

import musicaflight.avianutils.AvianImage;
import musicaflight.avianutils.ImageBank;

public class Images implements ImageBank {

	public static AvianImage gear;
	public static AvianImage note, img, clock, text, beamNote, process;
	public static AvianImage pause;
	public static AvianImage trueCirc, falseCirc;
	public static AvianImage up, down;
	public static AvianImage plus, x;

	@Override
	public void initImages() {
		note = new AvianImage("/res/photos/icons/note.png");
		img = new AvianImage("/res/photos/icons/pic.png");
		text = new AvianImage("/res/photos/icons/text.png");
		clock = new AvianImage("/res/photos/icons/clock.png");
		beamNote = new AvianImage("/res/photos/icons/beam.png");
		pause = new AvianImage("/res/photos/icons/pause.png");
		process = new AvianImage("/res/photos/icons/process.png");
		trueCirc = new AvianImage("/res/photos/icons/true.png");
		falseCirc = new AvianImage("/res/photos/icons/false.png");
		gear = new AvianImage("/res/photos/icons/gear.png");
		up = new AvianImage("/res/photos/icons/up.png");
		down = new AvianImage("/res/photos/icons/down.png");
		plus = new AvianImage("/res/photos/icons/plus.png");
		x = new AvianImage("/res/photos/icons/x.png");
	}
}
