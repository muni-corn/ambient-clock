
package app;

import musicaflight.avianutils.*;

public class SettingsPanel {

	float peekTarget;
	static float peek;

	public static float getPeek() {
		return peek;
	}

	boolean show;
	float iconPeekTarget;
	int selection = -1;
	float selectionAlpha;
	boolean itemSelected;
	Settings s;

	SettingsScreen[] screens = new SettingsScreen[] { new MusicSettings(),
			new PaperSettings(), new TextSettings(), new ClockSettings(),
			new OtherSettings()

	};

	float[] textPeeks = new float[screens.length];
	float[] textPeekTargets = new float[screens.length];
	float[] iconPeeks = new float[screens.length];
	float[] iconVelocities = new float[screens.length];

	public SettingsPanel(final Settings s) {
		this.s = s;
		AvianApp.addMouseListener(new AvianMouse() {

			@Override
			public void press(int button, float x, float y) {

				if (button == 1 && !show) {
					peekTarget = 10;
					iconPeekTarget = 40;
					selection = -1;
				} else if (button == 1 && show) {
					peekTarget = 150 - 10;
					iconPeekTarget = 95;
				}
				if (y >= AvianApp.getHeight() - peek && button == 0 && show) {
					itemSelected = true;
					selection = (int) (x * screens.length / AvianApp.getWidth());
					peekTarget = 190;
					iconPeekTarget = 95;
				}
			}

			@Override
			public void release(int button, float x, float y) {

				if (button == 1 && !show && Settings.screen == null) {
					show = true;
					int newSelection = (int) (x * screens.length / AvianApp.getWidth());
					if (y < AvianApp.getHeight() - peek) {
						newSelection = -1;
						for (int i = 0; i < textPeekTargets.length; i++) {
							textPeekTargets[i] = 0;
						}
					} else {
						for (int i = 0; i < textPeekTargets.length; i++) {
							if (i == newSelection) {
								textPeekTargets[i] = 10;
								continue;
							}
							textPeekTargets[i] = 0;
						}
					}
					for (int i = 0; i < iconVelocities.length; i++) {
						iconVelocities[i] = -6;
					}
					tick = 0;
					selection = newSelection;
				} else if (button == 1 && show) {
					show = false;
				}
				if (button == 0 && show && itemSelected) {
					s.open(screens[selection]);
					for (int i = 0; i < iconVelocities.length; i++) {
						iconVelocities[i] = 4;
					}
					itemSelected = false;
					show = false;
					peekTarget = 0;
					iconPeekTarget = 0;
				}
			}

			@Override
			public void move(float x, float y) {
				if (!show)
					return;
				int newSelection = (int) (x * screens.length / AvianApp.getWidth());
				if (y < AvianApp.getHeight() - peek) {
					newSelection = -1;
					for (int i = 0; i < textPeekTargets.length; i++) {
						textPeekTargets[i] = 0;
					}
				} else {
					for (int i = 0; i < textPeekTargets.length; i++) {
						if (i == newSelection) {
							textPeekTargets[i] = 10;
							continue;
						}
						textPeekTargets[i] = 0;
					}
				}
				if (AvianInput.isMouseButtonDown(0)) {
					if (newSelection != selection || y < AvianApp.getHeight() - 100) {
						itemSelected = false;
					}
				}
				selection = newSelection;
			}

			@Override
			public void scroll(int count) {

			}

		});
	}

	int tick;

	void logic() {
		if (show) {
			peekTarget = 100;
			iconPeekTarget = peekTarget / 2f;
		} else {
			peekTarget = iconPeekTarget = 0;
		}

		peek = AvianMath.glide(peek, peekTarget, 7f);

		tick++;

		for (int i = 0; i < iconPeeks.length; i++) {
			if (tick < (i) * 5 + 2) {
				iconPeeks[i] = -16;
				continue;
			}
			iconVelocities[i] += .2f;
			iconPeeks[i] -= iconVelocities[i];
			if (show) {
				if (iconPeeks[i] < iconPeekTarget && iconVelocities[i] > 0) {
					iconVelocities[i] *= -.5f;
					iconPeeks[i] = iconPeekTarget;
				}
			} else {
				if (iconPeeks[i] < -16)
					iconPeeks[i] = -16;
			}
		}
		for (int i = 0; i < textPeeks.length; i++) {
			textPeeks[i] = AvianMath.glide(textPeeks[i], show ? textPeekTargets[i] : 0, 10);
		}
		if (itemSelected) {
			selectionAlpha = AvianMath.glide(selectionAlpha, 75, 10f);
		} else {
			selectionAlpha = AvianMath.glide(selectionAlpha, 150, 10f);
		}
	}

	AvianRectangle r = new AvianRectangle();
	AvianRectangle selectRect = new AvianRectangle();

	void render() {
		if (peek == 0)
			return;

		AvianImage w = PaperSettings.getBackground(true);
		if (w != null) {
			float sW = AvianApp.getWidth();
			float sH = AvianApp.getHeight();

			float pX, pW, pH;

			float screenRatio = sW / sH;
			float picRatio = w.getWidth() / w.getHeight();

			if (screenRatio < picRatio) {
				pW = sH * picRatio;
				pH = sH;
				pX = sW / 2 - pW / 2;
				w.crop(0, 0, w.getWidth(), w.getHeight() * (1f - (peek / sH)));
			} else {
				pW = sW;
				pH = sW / picRatio;
				pX = 0;
				w.crop(0, 0, w.getWidth(), w.getHeight() * ((((pH - sH) / 2f) + (sH - peek)) / pH));
			}
			w.render(pX, AvianApp.getHeight() - peek + pH * (w.getCroppedHeight() / w.getHeight()), pW, -pH * (w.getCroppedHeight() / w.getHeight()), 255);
		}
		w = PaperSettings.getForeground(true);
		if (w != null) {

			float sW = AvianApp.getWidth();
			float sH = AvianApp.getHeight();

			float pX, pW, pH;

			float screenRatio = sW / sH;
			float picRatio = w.getWidth() / w.getHeight();

			if (screenRatio < picRatio) {
				pW = sH * picRatio;
				pH = sH;
				pX = sW / 2 - pW / 2;
				w.crop(0, 0, w.getWidth(), w.getHeight() * (1f - (peek / sH)));
			} else {
				pW = sW;
				pH = sW / picRatio;
				pX = 0;
				w.crop(0, 0, w.getWidth(), w.getHeight() * ((((pH - sH) / 2f) + (sH - peek)) / pH));
			}
			w.render(pX, AvianApp.getHeight() - peek + pH * (w.getCroppedHeight() / w.getHeight()), pW, -pH * (w.getCroppedHeight() / w.getHeight()), PaperSettings.getAlpha());
		}
		//		for (int i = 0; i < screens.length; i++) {
		//			screens[i].getBlurredIcon().render(AvianApp.getWidth() * (i * 2f + 1f) / (screens.length * 2f) - 16f, AvianApp.getHeight() - iconPeeks[i] + 32, 32, -32, peek * 255f * 5 / 150f);
		//		} 
		selectRect.set(selection * AvianApp.getWidth() / screens.length, AvianApp.getHeight() - peek, AvianApp.getWidth() / screens.length, peek);
		if (selection >= 0 && selection < screens.length)
			selectRect.render(0, (selectionAlpha * textPeeks[selection] / 10f) / 255f);

		r.set(0, AvianApp.getHeight() - peek, AvianApp.getWidth(), peek);
		r.render(0, (50 + (PaperSettings.doesDim() ? 150 : 0)) / 255f);

		for (int i = 0; i < screens.length; i++) {
			if (iconPeeks[i] == -16) {
				continue;
			}
			screens[i].getIcon().render(AvianApp.getWidth() * (i * 2f + 1f) / (screens.length * 2f) - 16f, AvianApp.getHeight() - iconPeeks[i] - 16, 1);
			AvianFont.setAlignment(AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
			AvianApp.Vegur().drawString(AvianApp.getWidth() * (i * 2f + 1f) / (screens.length * 2f), AvianApp.getHeight() - textPeeks[i] - 110f, screens[i].getHeader(), ((peek - 10f) / 90f) * textPeeks[i] / 10f);
		}
	}
}
