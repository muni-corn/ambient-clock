
package app;

import musicaflight.avianutils.*;
import musicaflight.avianutils.AvianEase.Direction;
import musicaflight.avianutils.AvianEase.Ease;

public abstract class SettingsScreen {

	public static final int ITEM_HEIGHT = 40;
	String header = "";
	AvianImage icon, iconBlur;
	private float scroll;
	float scrollFinal;
	private float height;
	boolean allowScrolling = true;
	static AvianRectangle r = new AvianRectangle();
	AvianEase e = new AvianEase(1f, Ease.EXPONENTIAL, Direction.IN);

	public SettingsScreen(String name, AvianImage icon) {
		this.header = name;
		this.icon = icon;
		iconBlur = new AvianImage(icon.getImageFilepath());
		iconBlur.gaussianBlur(2);
		AvianApp.addKeyListener(new AvianKeyboard() {

			@Override
			public void type(char text) {

			}

			@Override
			public void repeat(int key) {
				if (!allowScrolling)
					return;
				if (key == AvianInput.KEY_DOWN) {
					scrollFinal -= 100f;
					e.set(0);
				} else if (key == AvianInput.KEY_UP) {
					scrollFinal += 100f;
					e.set(0);
				}

			}

			@Override
			public void release(int key) {

			}

			@Override
			public void press(int key) {
				if (!allowScrolling)
					return;

				if (key == AvianInput.KEY_DOWN) {
					scrollFinal -= 100f;
					e.set(0);
				} else if (key == AvianInput.KEY_UP) {
					scrollFinal += 100f;
					e.set(0);
				}

			}
		});
		AvianApp.addMouseListener(new AvianMouse() {

			@Override
			public void press(int button, float x, float y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void release(int button, float x, float y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void move(float x, float y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void scroll(int count) {
				if (allowScrolling) {
					scrollFinal += count * 100f;
					e.set(0);
				}

			}

		});
	}

	public String getHeader() {
		return header;
	}

	public AvianImage getIcon() {
		return icon;
	}

	public AvianImage getBlurredIcon() {
		iconBlur.setSharpTexture(false);
		return iconBlur;
	}

	public boolean isActive() {
		return this.equals(Settings.getScreen()) && Settings.isOpen();
	}

	public void logic() {

		if (scrollFinal < AvianApp.getHeight() - height) {
			scrollFinal = AvianApp.getHeight() - height;
		}
		if (scrollFinal > 0) {
			scrollFinal = 0;
		}
		scroll = AvianMath.glide(scroll, scrollFinal, 5f);
		contentLogic();
		e.forward();
	}

	public void render(float yOffset, float alpha) {
		AvianFont.setAlignment(AvianFont.ALIGN_LEFT, AvianFont.ALIGN_TOP);
		contentRender(scroll + yOffset, alpha);
		if (height > AvianApp.getHeight()) {
			float availableHeight = AvianApp.getHeight() - 8;
			float scrubberHeight = (AvianApp.getHeight() / height) * availableHeight;
			if (scrubberHeight < 4)
				scrubberHeight = 4;
			float percentage = scroll / (AvianApp.getHeight() - height);
			r.set(AvianApp.getWidth() - 6, 4 + percentage * (availableHeight - scrubberHeight), 2, scrubberHeight);
			r.render((float) (150d * (1d - e.result())) / 255f);
		}
	}

	abstract void contentLogic();

	public abstract void contentRender(float yOffset, float alpha);

	public abstract void onOpen();

	public abstract void onClose();

	void setContentHeight(float height) {
		this.height = height;
		if (scrollFinal < AvianApp.getHeight() - height) {
			scrollFinal = AvianApp.getHeight() - height;
		}
		if (scrollFinal > 0) {
			scrollFinal = 0;
		}
	}

	float getScroll() {
		return scroll;
	}

	float getFinalScroll() {
		return scrollFinal;
	}

	void allowScrolling(boolean allow) {
		this.allowScrolling = allow;
	}

	protected static boolean hoversOverItem(float my, float y, int item) {
		return my > y + item * ITEM_HEIGHT && my < y + item * ITEM_HEIGHT + ITEM_HEIGHT;
	}

	protected static void renderItemRectangle(float y, int item, float alpha) {
		r.set(0, y + item * ITEM_HEIGHT, AvianApp.getWidth(), ITEM_HEIGHT);
		r.render(25f * alpha / 255f);
	}

	protected static void renderItem(String text, AvianImage icon, float x, float y, int item, float alpha) {
		renderItem(text, AvianApp.Vegur(), icon, x, y, item, alpha);
	}

	protected static void renderItem(String text, AvianFont font, AvianImage icon, float x, float y, int item, float alpha) {
		AvianFont.setAlignment(AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
		if (icon != null) {
			icon.render((int) (100 + x), (int) (y + item * ITEM_HEIGHT + (ITEM_HEIGHT / 2f) - 8), 16, 16, alpha);
			font.drawString(124 + x, y + item * ITEM_HEIGHT + (ITEM_HEIGHT / 2f), text, alpha);
		} else {
			font.drawString(100 + x, y + item * ITEM_HEIGHT + (ITEM_HEIGHT / 2f), text, alpha);
		}
	}

	public void setScroll(int s) {
		this.scrollFinal = s;
		this.scroll = s;
	}

}
