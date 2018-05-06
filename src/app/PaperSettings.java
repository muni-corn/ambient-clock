
package app;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

import app.FileExplorer.FileTypes;
import musicaflight.avianutils.*;
import musicaflight.avianutils.AvianApp.IsolatedTask;

public class PaperSettings extends SettingsScreen {

	int selection;
	float deleteTime;
	boolean delete;
	static FileExplorer f = new FileExplorer(FileTypes.IMAGES) {
		@Override
		public void handleFiles(final String[] paths) {
			AvianApp.startIsolatedTask(new IsolatedTask() {
				@Override
				public void task() {
					Collections.shuffle(Arrays.asList(paths));
					close();
					addBackgrounds(paths);
				}
			});

		}
	};

	float highlight;
	float hAlpha;
	float mx, my;
	public static AvianComboBox b = new AvianComboBox("30 seconds", "1 minute", "2 minutes", "5 minutes", "10 minutes", "15 minutes", "30 minutes", "1 hour");

	private static Paper foreground, trash;
	static Paper next;
	static ArrayList<String> papers = new ArrayList<String>();
	static ArrayList<String> queue = new ArrayList<String>();
	static float interval = 10f;
	static float changeTime;

	public PaperSettings() {
		super("Images", Images.img);
		AvianApp.addMouseListener(new AvianMouse() {

			@Override
			public void scroll(int count) {
				if (!isActive() || f.isOpen())
					return;
			}

			@Override
			public void release(int button, float mx1, float my1) {
				delete = false;
				if (!isActive() || f.isOpen())
					return;
				if (hoverAdd) {
					f.open();
				} else if (button == 0 && hoversOverItem(my1, 400 + getScroll(), 0)) {
					doBlur = !doBlur;
				} else if (button == 0 && hoversOverItem(my1, 400 + getScroll(), 1)) {
					dim = !dim;
				}

			}

			@Override
			public void press(int button, float x, float y) {
				if (!isActive() || f.isOpen() || papers.isEmpty() || button != 0)
					return;
				selection = (int) ((y - 500f - getScroll()) / (ITEM_HEIGHT)) - 2;
				if (selection >= 0 && selection < papers.size())
					delete = true;
			}

			@Override
			public void move(float x, float y) {
				mx = x;
				my = y;
				if (!isActive() || f.isOpen()) {
					hoverAdd = false;
					return;
				}
				if (AvianInput.isMouseButtonDown(0)) {
					int newSelection = (int) ((y - 500f - getScroll()) / (ITEM_HEIGHT)) - 2;
					if (selection != newSelection) {
						deleteTime = 0;
						if (newSelection >= 0 && newSelection < papers.size())
							selection = newSelection;
					}
				}
				hoverAdd = hoversOverItem(y, 500 + getScroll(), 0);
			}
		});
	}

	float addAlpha;
	boolean hoverAdd;
	static float bgAlpha = 0;

	public static void paperLogic() {
		switch (b.getSelectionIndex()) {
			case 0:
				interval = 30;
				break;
			case 1:
				interval = 60;
				break;
			case 2:
				interval = 120;
				break;
			case 3:
				interval = 300;
				break;
			case 4:
				interval = 600;
				break;
			case 5:
				interval = 900;
				break;
			case 6:
				interval = 1800;
				break;
			case 7:
				interval = 3600;
				break;
		}
		changeTime += .01f;
		if (foreground != null) {
			bgAlpha += 1f / 200f;
			if (bgAlpha > 1f) {
				bgAlpha = 1f;
				if (trash != null)
					trash = trash.release();
			}
		}
		if (papers.size() > 0 && next == null && !loading) {
			loadNextPaper();
		}
		if (((next != null && foreground == null) || changeTime > interval) && !loading) {
			rotate();
		}
	}

	@Override
	public void contentLogic() {

		allowScrolling(isActive() && !f.isOpen());

		b.restrictFocus(f.isOpen() || !isActive());
		b.logic();
		if (delete && !papers.isEmpty()) {
			deleteTime += .01f;
			if (deleteTime > 1f) {
				deleteTime = 0f;
				delete(selection);
				setContentHeight(500 + (papers.size() + 2) * ITEM_HEIGHT);
				selection = (int) ((my - 500f - getFinalScroll()) / (ITEM_HEIGHT)) - 2;
				if (!(selection >= 0 && selection < papers.size())) {
					delete = false;
				}
			}
		} else {
			deleteTime = 0;
		}
		addAlpha = AvianMath.glide(addAlpha, hoverAdd ? 50 : 0, 10f);
		f.logic();

		int items = papers.size();
		setContentHeight(500 + (items + 2) * ITEM_HEIGHT);

	}

	static AvianRectangle r = new AvianRectangle();
	static boolean doBlur = true;
	static boolean dim = true;

	static boolean doesBlur() {
		return doBlur;
	}

	static void setShouldBlur(boolean b) {
		doBlur = b;
	}

	static boolean doesDim() {
		return dim;
	}

	static void setShouldDim(boolean b) {
		dim = b;
	}

	static DecimalFormat d = new DecimalFormat("0.#");

	@Override
	public void contentRender(float yOffset, float alpha) {

		renderItemRectangle(400 + yOffset, 0, hoversOverItem(my, 400 + yOffset, 0) ? alpha : 0);
		renderItemRectangle(400 + yOffset, 1, hoversOverItem(my, 400 + yOffset, 1) ? alpha : 0);
		renderItem("Blur wallpaper", doBlur ? Images.trueCirc : Images.falseCirc, 0, 400 + yOffset, 0, alpha);
		renderItem("Dim clock", dim ? Images.trueCirc : Images.falseCirc, 0, 400 + yOffset, 1, alpha);

		renderItemRectangle(500 + yOffset, 0, hoverAdd ? alpha : 0);
		renderItem("Add backgrounds", Images.plus, 0, 500 + yOffset, 0, alpha);
		if (!papers.isEmpty()) {
			renderItem("Click and hold on a background to delete it", null, 0, 500 + yOffset, 1, alpha / 2);
		}
		r.set(mx * (1f - deleteTime) + 0, 500f + ITEM_HEIGHT * (selection + 2) + yOffset, AvianApp.getWidth() * deleteTime, ITEM_HEIGHT);
		r.render(1, 1f - deleteTime, (1f - deleteTime), 50 * alpha / 255f);

		for (int i = 0; i < papers.size(); i++) {
			float y = 500f + (i + 2) * ITEM_HEIGHT + yOffset;
			if (y < -ITEM_HEIGHT || y > AvianApp.getHeight()) {
				continue;
			}

			String paperPath = papers.get(i);
			String fileName = paperPath.substring(paperPath.lastIndexOf(File.separator) + 1);
			String name = fileName;
			if (fileName.lastIndexOf(".") != -1) {
				name = fileName.substring(0, fileName.lastIndexOf("."));
			}

			boolean hover = !f.isOpen() && hoversOverItem(my, 500 + getScroll(), i + 2);
			if (hover) {
				renderItemRectangle(500 + yOffset, i + 2, hover ? alpha : 0);
			}
			renderItem(name, Images.img, 0, 500 + yOffset, i + 2, alpha);
		}

		renderItem("Photo duration", null, 0, 200f + yOffset, 0, alpha);
		//		String i = "Oh no. Something's wrong again.";// I'm sorry... I really am! Maybe I'll bake you cookies to make up for this. Or would you rather have cake? Sorry there's just a lot of things going through my computer-programmed mind right now...";// I'm trying the best I can. Nobody is! And if you can still read this message you're either hacking into my code or you have a seriously large computer screen with some serious definition. Anyways, I love you. Keep doing great things in the world. Unless you're a murderer. Don't be a murderer.";
		//		if (interval <= 30) {
		//			i = "30 seconds";
		//		} else {
		//			i = interval / 60 + (interval / 60 == 1 ? " minute" : " minutes");
		//		}
		//		renderItem(i, null, 0, 200f + yOffset, 2, alpha / 2);
		b.set(100, 200 + yOffset + ITEM_HEIGHT * 1.5f, 300);
		b.render(alpha);
		f.render();
	}

	public void onClose() {
		f.close();
		DataHost.saveData();
	}

	private static Comparator<String> c = new Comparator<String>() {

		@Override
		public int compare(String o1, String o2) {
			o1 = o1.substring(o1.lastIndexOf(File.separator) + 1);
			if (o1.lastIndexOf(".") != -1) {
				o1 = o1.substring(0, o1.lastIndexOf("."));
			}

			o2 = o2.substring(o2.lastIndexOf(File.separator) + 1);
			if (o2.lastIndexOf(".") != -1) {
				o2 = o2.substring(0, o2.lastIndexOf("."));
			}
			return o1.compareTo(o2);
		}

	};

	public static void sortPapers() {
		Collections.sort(papers, c);
	}

	@Override
	public void onOpen() {
		sortPapers();
	}

	static boolean loading;

	private static void loadNextPaper() {
		if (next != null)
			return;

		if (queue.isEmpty()) {
			if (papers.isEmpty()) {
				return;
			}
			fillQueue();
		}

		if (queue.isEmpty())
			return;

		loading = true;

		AvianApp.startIsolatedTask(new IsolatedTask() {

			@Override
			public void task() {
				final String originalData = queue.remove(0);
				String newData = (next = new Paper(originalData)).load();
				if (!originalData.equals(newData)) {
					for (int j = 0; j < papers.size(); j++) {
						if (papers.get(j).equals(originalData)) {
							papers.set(j, newData);
							System.out.println("Original paper data \"" + originalData + "\" has been changed to \"" + newData + "\"");
						}
					}
				}
				loading = false;
			}
		});

	}

	public static void renderWallpaper() {
		if (trash != null)
			trash.render(1, doBlur);
		if (foreground != null)
			foreground.render(bgAlpha, doBlur);
	}

	private static void rotate() {
		if (trash != null) {
			trash = trash.release();
		}
		trash = foreground;
		foreground = next;
		next = null;
		changeTime = 0;
		bgAlpha = 0;
	}

	public static ArrayList<String> getPapers() {
		return papers;
	}

	static AvianImage getForeground(boolean blur) {
		if (foreground == null)
			return null;
		return blur ? foreground.getBlurredImage() : foreground.getNormalImage();
	}

	static AvianImage getBackground(boolean blur) {
		if (trash == null)
			return null;
		return blur ? trash.getBlurredImage() : trash.getNormalImage();
	}

	public static float getAlpha() {
		return bgAlpha;
	}

	static void addBackgrounds(String... strings) {
		for (int i = 0; i < strings.length; i++) {
			papers.add(strings[i]);
		}
		Collections.sort(papers);
	}

	static void delete(int index) {
		papers.remove(index);
	}

	private static void fillQueue() {
		Collections.shuffle(papers);
		queue.addAll(papers);
		Collections.sort(papers);
	}

}
