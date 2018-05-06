
package app;

import musicaflight.avianutils.*;
import musicaflight.avianutils.AvianEase.Direction;
import musicaflight.avianutils.AvianEase.Ease;

public class Settings {

	String header;
	float headerX = 100, headerY = 100;
	static SettingsScreen screen;
	float black;
	static AvianEase content = new AvianEase(.75f, Ease.EXPONENTIAL);
	static AvianEase fadeOut = new AvianEase(.35f, Ease.QUADRATIC, Direction.IN);
	static boolean open;
	SettingsPanel s = new SettingsPanel(this);

	public Settings() {
		AvianApp.addMouseListener(new AvianMouse() {

			@Override
			public void press(int button, float x, float y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void release(int button, float x, float y) {
				//				if (AvianApp.hasIsolatedTasksRunning())
				//					return;
				if (button == 1 && open) {
					open = false;
					s.show = false;
					screen.onClose();
				}
			}

			@Override
			public void move(float x, float y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void scroll(int count) {
				// TODO Auto-generated method stub

			}

		});

	}

	void open(SettingsScreen scr) {
		Settings.screen = scr;
		open = true;
		content.set(0);
		fadeOut.set(0);
		black = 0;
		scr.setScroll(0);
		scr.onOpen();
	}

	void logic() {
		s.logic();
		if (screen != null && content.result() != 0 && fadeOut.result() != 1)
			screen.logic();

		content.forward();
		if (!open) {
			fadeOut.forward();
			if (screen != null && fadeOut.result() * content.result() == 1) {
				screen = null;
			}
		}
		black = AvianMath.glide(black, 1, 10f);

	}

	AvianRectangle r = new AvianRectangle();

	void render() {
		s.render();

		if (screen == null || content.result() == 0 || fadeOut.result() == 1) {
			return;
		}

		float contentAlpha = content.result() * (1f - fadeOut.result());
		float contentOffset = (float) (100d - 100d * content.result());

		//		r.set(0, contentOffset, AvianApp.getWidth() - 200 - contentOffset + (200 - SettingsPanel.getPeek()), AvianApp.getHeight());
		//		if (open) {
		//			r.render(AvianColor.black((float) (50d * content.result())));
		//		} else {
		//			r.render(AvianColor.black((float) (50 * (1d - fadeOut.result()))));
		//		}
		AvianFont.setAlignment(AvianFont.ALIGN_LEFT, AvianFont.ALIGN_TOP);
		AvianApp.VegurBold().drawString(100, 100 + contentOffset + screen.getScroll(), screen.getHeader(), contentAlpha);
		AvianFont.setHorizontalAlignment(AvianFont.ALIGN_RIGHT);
		AvianApp.Vegur().drawString(AvianApp.getWidth() - 100, 100 + screen.getScroll() + contentOffset, "Right click to close", contentAlpha * .75f);

		if (contentAlpha > 0) {
			screen.render(contentOffset, contentAlpha);
		}

	}

	static float getAlpha() {
		return content.result() * (1f - fadeOut.result());
	}

	public static boolean isOpen() {
		return open;
	}

	public static SettingsScreen getScreen() {
		return screen;
	}

}
