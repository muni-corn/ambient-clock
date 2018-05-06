
package app;

import musicaflight.avianutils.*;

public class OtherSettings extends SettingsScreen {

	public OtherSettings() {
		super("Updates", Images.gear);
		UpdateChecker.checkForUpdate();
		AvianApp.addMouseListener(new AvianMouse() {

			@Override
			public void scroll(int count) {
			}

			@Override
			public void release(int button, float x, float yy) {
				if (button != 0 || !isActive())
					return;
				if (hover) {
					if (UpdateChecker.readyToInstall()) {
						UpdateChecker.requestInstall();
					} else if (UpdateChecker.updateIsAvailable()) {
						UpdateChecker.requestDownload();
					}
				}

			}

			@Override
			public void press(int button, float x, float yy) {
			}

			@Override
			public void move(float x, float yy) {
				hover = hoversOverItem(yy, getButtonY(), 0);
			}
		});
	}

	boolean hover;
	float alpha;

	@Override
	void contentLogic() {
		alpha = AvianMath.glide(alpha, hover ? 50 : 0, 10f);
		if (UpdateChecker.updateIsAvailable()) {
			setContentHeight(getButtonY() + ITEM_HEIGHT * 2 - getScroll());
		} else {
			setContentHeight(250);
		}
	}

	float getButtonY() {
		String s = AvianApp.Vegur().wrap(UpdateChecker.getInfo(), AvianApp.getWidth() - 200);
		int lines = s.length() - s.replaceAll("\r\n|\r|\n", "").length();
		return 200 + AvianApp.Vegur().getHeight() * 6f + y + AvianApp.Vegur().getHeight() * lines;
	}

	float y;

	@Override
	public void contentRender(float yOffset, float a) {
		y = yOffset;
		if (UpdateChecker.getResults() == null)
			return;
		AvianApp.Vegur().drawString(100, 200f + yOffset, UpdateChecker.getResults(), a);
		AvianApp.Vegur().drawString(100, 300f + yOffset, UpdateChecker.getProblem(), a * .75f);

		if (UpdateChecker.updateIsAvailable())
			AvianApp.Vegur().drawString(100, 200f + AvianApp.Vegur().getHeight() * 3f + yOffset, AvianApp.Vegur().wrap(UpdateChecker.getInfo(), AvianApp.getWidth() - 200), a);

		double progress = UpdateChecker.getProgress();

		if (UpdateChecker.isDownloading()) {
			if (UpdateChecker.getProgress() > 0) {
				r.set(100, getButtonY() - 1, (float) (300 * progress), 2);
				r.render(200 * a / 255f);
				r.set(100 + r.getW(), getButtonY() - 1, (float) (300 * (1d - progress)), 2);
				r.render(50 * a / 255f);
			} else {
				MainScreen.renderDots(100, getButtonY(), a);
			}
		} else if (UpdateChecker.readyToInstall()) {
			renderItemRectangle(getButtonY(), 0, a * (hover ? 1 : 0));
			renderItem("Install", AvianApp.VegurBold(), null, 0, getButtonY(), 0, a);
		} else if (UpdateChecker.updateIsAvailable()) {
			renderItemRectangle(getButtonY(), 0, a * (hover ? 1 : 0));
			renderItem("Download", AvianApp.VegurBold(), null, 0, getButtonY(), 0, a);
		}

	}

	@Override
	public void onOpen() {
		UpdateChecker.checkForUpdate();
	}

	@Override
	public void onClose() {
		// TODO Auto-generated method stub

	}

}
