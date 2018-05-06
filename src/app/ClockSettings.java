
package app;

import java.text.DecimalFormat;

import musicaflight.avianutils.*;

public class ClockSettings extends SettingsScreen {

	boolean click;

	public ClockSettings() {
		super("Clock", Images.clock);
		AvianApp.addMouseListener(new AvianMouse() {

			@Override
			public void press(int button, float x, float y) {
				if (button == 0)
					click = true;

			}

			@Override
			public void release(int button, float x, float y) {
				click = false;
				if (button != 0 || !isActive()) {
					return;
				}
				if (hoverAna) {
					analog = !analog;
				} else if (hoverUse) {
					useTimer = !useTimer;
					DataHost.resetTimer();
				} else if (useTimer && hoverStop) {
					stopMusic = !stopMusic;
				}

			}

			@Override
			public void move(float x, float y) {
				mx = x;
				my = y;
				hoverAna = hoversOverItem(y, 200 + getScroll(), 0);
				hoverUse = hoversOverItem(y, 200 + getScroll(), 1);
				hoverStop = hoversOverItem(y, 200 + getScroll(), 2);
			}

			@Override
			public void scroll(int count) {
				// TODO Auto-generated method stub

			}

		});
	}

	boolean hoverAna, hoverUse, hoverStop;

	float mx, my;
	float interval, time;
	public static boolean analog, useTimer, stopMusic = true;

	@Override
	public void contentLogic() {

		if (AvianInput.isMouseButtonDown(0) && mx > 85 && mx < 235 && my > 350 + getScroll() && my < 500 + getScroll()) {
			time -= .01f;
			if (time < 0) {
				if (mx < 160 && my < 425 + getScroll()) {
					DataHost.addToSetTime(1000 * 60);
				} else if (mx > 160 && my < 425 + getScroll()) {
					DataHost.addToSetTime(1000 * 5);
				} else if (mx < 160 && my > 425 + getScroll()) {
					DataHost.addToSetTime(-1000 * 60);
				} else if (mx > 160 && my > 425 + getScroll()) {
					DataHost.addToSetTime(-1000 * 5);
				}
				time = interval;
				if (interval > .01f) {
					interval -= .05f;
				} else {
					interval = .01f;
				}
			}
		} else {
			interval = .5f;
			time = 0;
		}
		setContentHeight(500);
	}

	static DecimalFormat df = new DecimalFormat("00");
	static AvianCircle c = new AvianCircle();

	@Override
	public void contentRender(float yOffset, float alpha) {
//		AvianFont v = AvianApp.Vegur();
		AvianFont b = AvianApp.VegurBold();

		AvianImage switchImg = analog ? Images.trueCirc : Images.falseCirc;
		renderItemRectangle(200 + yOffset, 0, alpha * (hoverAna ? 1 : 0));
		renderItem("Analog clock", switchImg, 0, 200 + yOffset, 0, alpha);

		switchImg = useTimer ? Images.trueCirc : Images.falseCirc;
		renderItemRectangle(200 + yOffset, 1, alpha * (hoverUse ? 1 : 0));
		renderItem("Use timer", switchImg, 0, 200 + yOffset, 1, alpha);

		if (useTimer) {
			switchImg = stopMusic ? Images.trueCirc : Images.falseCirc;
			renderItemRectangle(200 + yOffset, 2, alpha * (hoverStop ? 1 : 0));
			renderItem("Stop music when timer ends", switchImg, 0, 200 + yOffset, 2, alpha);

			long millis = DataHost.getSetTime();
			long s = (millis / 1000) % 60;
			long m = (millis / (1000 * 60));

			AvianFont.setHorizontalAlignment(AvianFont.ALIGN_CENTER);
			AvianFont.setVerticalAlignment(AvianFont.ALIGN_CENTER);
			b.drawString(110, 425 + yOffset, m + "", alpha);
			b.drawString(160, 425 + yOffset, ":", alpha);
			b.drawString(210, 425 + yOffset, df.format(s), alpha);
			renderItem("Timer duration", null, 0, 200 + yOffset, 3, alpha * .5f);

			if (mx > 85 && mx < 235 && my > 350 + yOffset && my < 500 + yOffset) {
				if (mx < 160 && my < 425 + yOffset) {
					c.set(110, 375 + yOffset, 50);
					c.render(true, 25 * alpha / 255f);
				} else if (mx > 160 && my < 425 + yOffset) {
					c.set(210, 375 + yOffset, 50);
					c.render(true, 25 * alpha / 255f);
				} else if (mx < 160 && my > 425 + yOffset) {
					c.set(110, 475 + yOffset, 50);
					c.render(true, 25 * alpha / 255f);
				} else if (mx > 160 && my > 425 + yOffset) {
					c.set(210, 475 + yOffset, 50);
					c.render(true, 25 * alpha / 255f);
				}
			}

			Images.up.render(102, 375 + yOffset - 4, alpha);
			Images.down.render(102, 475 + yOffset - 4, alpha);
			Images.up.render(202, 375 + yOffset - 4, alpha);
			Images.down.render(202, 475 + yOffset - 4, alpha);
		}
	}

	@Override
	public void onClose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOpen() {
		// TODO Auto-generated method stub

	}

}
