
package app;

import musicaflight.avianutils.AvianFont;
import musicaflight.avianutils.FontBank;

public class Fonts implements FontBank {

	public static AvianFont Sinkin;

	@Override
	public void initFonts() {
		Sinkin = new AvianFont("/res/fonts/SINKINSANS-100THIN_0.OTF", 110);
	}

}
