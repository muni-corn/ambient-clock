
package app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import musicaflight.avianutils.*;
import musicaflight.avianutils.AvianEase.Direction;
import musicaflight.avianutils.AvianEase.Ease;

public class MainScreen {

	AvianFont periodFont = new AvianFont(AvianApp.Vegur().getFont(), 55);
	static AvianRectangle r = new AvianRectangle();
	static AvianLine l = new AvianLine();
	static AvianArc a = new AvianArc();
	static AvianCircle c = new AvianCircle();

	ArrayList<String> text = new ArrayList<String>();
	float releaseTime;
	float spacebarTime;
	boolean spacebar;
	float e;
	float h;
	Settings s = new Settings();
	DataHost d = new DataHost(DataHost.getDataDirectory() + "prefs.amb");
	float timerCosIn, resetCosIn;
	AvianEase upNextEase = new AvianEase(.5f, Ease.EXPONENTIAL, Direction.IN);
	boolean showNext = false;

	public MainScreen() {
		AvianApp.addKeyListener(new AvianKeyboard() {

			@Override
			public void press(int key) {
				if (key == AvianInput.KEY_SPACE && !(Settings.screen instanceof TextSettings && Settings.open)) {
					spacebar = true;
					spacebarTime = 0;
				} else if (key == AvianInput.KEY_Q) {
					showNext = true;
				}
			}

			@Override
			public void type(char t) {

			}

			@Override
			public void repeat(int key) {
			}

			@Override
			public void release(int key) {
				if (MusicSettings.getCurrentSong() != null && spacebar && spacebarTime < .25f && key == AvianInput.KEY_SPACE && !(Settings.screen instanceof TextSettings && Settings.open)) {
					MusicSettings.toggleMusic();
				} else if (spacebar && spacebarTime >= .75f) {
					DataHost.resetTimer();
					if (MusicSettings.getCurrentSong() != null && !MusicSettings.getCurrentSong().isPlaying()) {
						MusicSettings.toggleMusic();
					}
					releaseTime = spacebarTime;
				}
				if (key == AvianInput.KEY_SPACE)
					spacebar = false;
				else if (key == AvianInput.KEY_Q) {
					showNext = false;
				}
			}

		});
	}

	long tick;

	public void logic() {
		s.logic();
		d.logic();
		DataHost.getMainHeader().logic();
		DataHost.getMainContent().logic();

		if (showNext) {
			upNextEase.forward();
		} else {
			upNextEase.rewind();
		}
		if (DataHost.getMillisRemaining() != 0) {
			timerCosIn = 0;
		} else {
			if (timerCosIn == 0 && MusicSettings.isPlayingMusic() && ClockSettings.stopMusic) {
				MusicSettings.pauseMusic();
			}
			timerCosIn += 2;
			if (timerCosIn > 180 * (ClockSettings.analog ? 6 : 5))
				timerCosIn = 180 * (ClockSettings.analog ? 6 : 5);
		}

		if (spacebar) {
			e = 0;
			if (spacebarTime > .25f)
				h = AvianMath.glide(h, 75f, 3f);
			else
				h = 0;
		} else {
			if (releaseTime >= .75) {
				e = AvianMath.glide(e, 1f, 10f);
			} else {
				h = AvianMath.glide(h, 0f, 10f);
				spacebarTime = AvianMath.glide(spacebarTime, 0, 15f);
			}
		}
		if (ClockSettings.useTimer) {
			spacebarTime += .01f;
		}
		releaseTime = spacebarTime;
		if (spacebarTime > .75f) {
			spacebarTime = .75f;
			releaseTime = .75f;
			resetCosIn += 3;
		} else {
			resetCosIn = 0;
		}

		for (int i = 0; i < yv.length; i++) {
			//			y[i] = AvianMath.sin(tick*1.5f - i * 50)*-10;
			yv[i] += .05f;
			if (tick % 200 == (i * 20) % 200) {
				yv[i] = -1.5f/* - AvianMath.randomFloat() / 8f*/;
			}
			y[i] += yv[i];
			if (y[i] > 0) {
				y[i] = 0;
				yv[i] *= 0;
			}
		}
		tick++;

	}

	SimpleDateFormat sdf = new SimpleDateFormat("h:mm");
	SimpleDateFormat period = new SimpleDateFormat("aa");

	static int spheres = 5;
	static float[] y = new float[spheres];
	float[] yv = new float[spheres];

	boolean newMessage = true;

	public void render() {
		Calendar cal = Calendar.getInstance();
		float alpha = (1f - Settings.getAlpha());
		PaperSettings.renderWallpaper();
		r.set(0, 0, AvianApp.getWidth(), AvianApp.getHeight());
		if (PaperSettings.doesDim()) {
			r.render(0, .5f);
		} else {
			r.render(0, (1f - alpha) * .5f);
		}
		AvianFont.setAlignment(AvianFont.ALIGN_LEFT, AvianFont.ALIGN_TOP);

		if (ClockSettings.analog) {

			if (ClockSettings.useTimer) {
				c.setQuality(64);
				c.set(350, 315, 300);
				float timeUpAlpha = (1f - Settings.getAlpha()) * (1f - ((AvianMath.cos(timerCosIn) + 1f) / 2f));
				c.render(false, timeUpAlpha);
				l.setLineWidth(1);
				float timeLeft = (float) DataHost.getMillisRemaining() / DataHost.getSetTime();
				if (timeLeft > 1f)
					timeLeft = 1f;
				float arcStart = timeLeft * 360f;
				float arcEnd = timeLeft * 720f;
				a.setQuality(64);
				a.set(350, 315, 150, arcStart, arcEnd);
				a.render((1f - Settings.getAlpha()) * .5f);

				l.set(350, 315, 350 + 148 * AvianMath.sin(timeLeft * 360f), 315 - 148 * AvianMath.cos(timeLeft * 360f));
				l.render((1f - Settings.getAlpha()) * .25f + .75f * timeUpAlpha);

				c.set(350 + 150 * AvianMath.sin(timeLeft * 360f), 315 - 150 * AvianMath.cos(timeLeft * 360f), 5);
				c.render(true, (1f - Settings.getAlpha()) * .5f);
			} else {}
			int ms = cal.get(Calendar.MILLISECOND);
			float sec = cal.get(Calendar.SECOND) + (ms / 1000f);
			float m = cal.get(Calendar.MINUTE) + (sec / 60f);
			float hr = cal.get(Calendar.HOUR) + (m / 60f);
			l.setLineWidth(2);

			l.set(350, 315, 350 + 140 * AvianMath.sin(m * 6f), 315 - 140 * AvianMath.cos(m * 6f));
			l.render(1f - Settings.getAlpha());

			l.set(350, 315, 350 + 70 * AvianMath.sin(hr * 360f / 12), 315 - 70 * AvianMath.cos(hr * 360f / 12));
			l.render(1f - Settings.getAlpha());

		} else if (!ClockSettings.useTimer) {
			String clock = getClockAsString(cal);
			Fonts.Sinkin.drawString(200, 200, clock, alpha);
			AvianFont.setAlignment(AvianFont.ALIGN_LEFT, AvianFont.ALIGN_BOTTOM);
			periodFont.drawString(200 + Fonts.Sinkin.getWidth(clock + ","), 200 + Fonts.Sinkin.getAscent(), period.format(cal.getTime()), alpha);
		} else {
			Fonts.Sinkin.drawString(200, 200, DataHost.getTimeRemainingAsString(), ((AvianMath.cos(timerCosIn) + 1f) / 2f * .8f + .2f) * alpha);
		}

		if (MusicSettings.getNowPlaying() != null) {
			SettingsScreen.renderItem(MusicSettings.getNowPlaying(), Images.beamNote, 100, 100, 0, .75f * alpha * MusicSettings.getVolume());
			SettingsScreen.renderItem(MusicSettings.getNowPlaying(), Images.pause, 100, 100, 0, ((1f - MusicSettings.getVolume()) * .25f) * alpha);
		}
		if (upNextEase.result() > 0) {
			SettingsScreen.renderItem("Up next: " + MusicSettings.getNext(), null, 100, 100, 1, upNextEase.result() * .25f * alpha);
		}

		DataHost.getMainHeader().set(200, (ClockSettings.analog ? 500 : 400), AvianApp.getWidth() - 400);
		DataHost.getMainHeader().renderContentOnly(alpha);

		DataHost.getMainContent().set(200, (ClockSettings.analog ? 500 : 400) + DataHost.getMainHeader().getHeight() + AvianApp.VegurBold().getHeight(), AvianApp.getWidth() - 400);
		DataHost.getMainContent().renderContentOnly(alpha);

		if (AvianApp.hasIsolatedTasksRunning()) {
			renderDots(200, AvianApp.getHeight() - 50, alpha);
		}

		if (releaseTime > .25f) {
			float perc = (releaseTime - .25f) / .5f;
			float lx0 = AvianApp.getWidth() / 2f - AvianApp.getWidth() * perc / 8f - AvianApp.getWidth() * e * 3 / 8f;
			float lx1 = AvianApp.getWidth() / 2f - AvianApp.getWidth() * e / 2f;
			float rx0 = AvianApp.getWidth() / 2f + AvianApp.getWidth() * perc / 8f + AvianApp.getWidth() * e * 3 / 8f;
			float rx1 = AvianApp.getWidth() / 2f + AvianApp.getWidth() * e / 2f;

			float alpha2 = 25 + 25 * (AvianMath.cos(resetCosIn) + 1f) / 2f;

			float peek = SettingsPanel.getPeek();

			r.set(lx0, AvianApp.getHeight() - h - peek, lx1 - lx0, 40);
			r.render(alpha2 / 255f);
			r.set(rx0, AvianApp.getHeight() - h - peek, rx1 - rx0, 40);
			r.render(alpha2 / 255f);

			r.set(AvianApp.getWidth() / 2 - AvianApp.getWidth() / 8 - e * AvianApp.getWidth() * 3f / 8f, AvianApp.getHeight() - h - peek, AvianApp.getWidth() * (1f - e) / 8f, 40);
			r.render(alpha2 / 255f);
			r.set(AvianApp.getWidth() / 2 + e * AvianApp.getWidth() / 2f, AvianApp.getHeight() - h - peek, AvianApp.getWidth() * (1f - e) / 8f, 40);
			r.render(alpha2 / 255f);

			AvianFont.setVerticalAlignment(AvianFont.ALIGN_CENTER);
			AvianApp.Vegur().drawString(AvianApp.getHeight() - h + 20 - peek, "Reset timer" + (!MusicSettings.isPlayingMusic() && MusicSettings.hasMusic() ? " and play music" : ""), (1f - e));

		}
		s.render();

	}

	public static void renderDots(float xx, float yy, float alpha) {
		for (int i = 0; i < y.length; i++) {
			c.set(xx + i * 20 + 2.5f, yy + y[i] / 10, 5);
			c.render(true, (.05f + y[i] / -50) * alpha);
		}
	}

	public String getClockAsString(Calendar cal) {
		return sdf.format(cal.getTime());
	}

}
