
package app;

import musicaflight.avianutils.AvianApp;

public class TextSettings extends SettingsScreen {

	public TextSettings() {
		super("Text", Images.text);
	}

	@Override
	public void contentLogic() {

	}

	@Override
	public void contentRender(float yOffset, float alpha) {
		DataHost.getMainHeader().restrictFocus(false);
		DataHost.getMainContent().restrictFocus(false);

		DataHost.getMainHeader().set(100, 200 + yOffset, AvianApp.getWidth() - 200);
		DataHost.getMainContent().set(100, 200 + DataHost.getMainHeader().getHeight() + DataHost.getMainHeader().getFont().getHeight() + yOffset, AvianApp.getWidth() - 200);
		DataHost.getMainHeader().render(alpha);
		DataHost.getMainContent().render(alpha);
		setContentHeight(200 + DataHost.getMainHeader().getHeight() + DataHost.getMainHeader().getFont().getHeight() + DataHost.getMainContent().getHeight()+10);
	}

	@Override
	public void onClose() {
		DataHost.getMainHeader().restrictFocus(true);
		DataHost.getMainContent().restrictFocus(true);

	}

	@Override
	public void onOpen() {
		DataHost.getMainHeader().restrictFocus(false);
		DataHost.getMainContent().restrictFocus(false);

	}

}
