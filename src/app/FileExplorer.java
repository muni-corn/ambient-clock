
package app;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import musicaflight.avianutils.*;
import musicaflight.avianutils.AvianEase.Ease;

public abstract class FileExplorer {

	static float fontHeight = AvianApp.Vegur().getHeight();
	static float listFileHeight = fontHeight * 1.5f;

	static AvianRectangle r = new AvianRectangle();

	File directory;
	static float fileOffset = 0;
	ListFile[] files;
	//	private ArrayList<ListFile> trash = new ArrayList<ListFile>();
	float sourceY;

	static AvianImage folder = new AvianImage("/res/photos/icons/folder.png");
	private static AvianImage up = new AvianImage("/res/photos/icons/upFolder.png");

	private float upAlpha;
	boolean hoverUp;

	float finalFileOffset;

	private AvianEase fade = new AvianEase(.35f, Ease.QUADRATIC);

	private static FileFilter imgs = new FileFilter() {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			String[] split = f.getName().split("\\.");
			String extension = split[split.length - 1];
			if (extension != null && (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpeg"))) {
				return true;
			}
			return false;
		}

	};
	private static FileFilter music = new FileFilter() {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			String[] split = f.getName().split("\\.");
			String extension = split[split.length - 1];
			if (extension != null && (extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("mp3") || extension.equalsIgnoreCase("wav"))) {
				return true;
			}
			return false;
		}

	};
	private static Comparator<ListFile> sorter = new Comparator<ListFile>() {

		@Override
		public int compare(ListFile file1, ListFile file2) {

			if (file1.isDirectory() && file2.isFile()) {
				return -1;
			}
			if (file1.isDirectory() && file2.isDirectory()) {
				return 0;
			}
			if (file1.isFile() && file2.isFile()) {
				return 0;
			}
			return 1;
		}
	};

	static enum FileTypes {
		IMAGES,
		MUSIC;
	}

	FileTypes ft;

	public FileExplorer(FileTypes ft) {
		this.ft = ft;
		File f = new File(System.getProperty("user.home"));
		//		while (f.getParent() != null) {
		//			f = f.getParentFile();
		//		}
		directory = f;
		AvianApp.addKeyListener(new AvianKeyboard() {

			@Override
			public void press(int key) {
				if (key == AvianInput.KEY_DOWN) {
					finalFileOffset -= AvianApp.Vegur().getHeight() * 1.5f;
				} else if (key == AvianInput.KEY_UP) {
					finalFileOffset += AvianApp.Vegur().getHeight() * 1.5f;
				}
			}

			@Override
			public void type(char text) {
				// TODO Auto-generated method stub

			}

			@Override
			public void repeat(int key) {
				if (key == AvianInput.KEY_DOWN) {
					finalFileOffset -= AvianApp.Vegur().getHeight() * 1.5f;
				} else if (key == AvianInput.KEY_UP) {
					finalFileOffset += AvianApp.Vegur().getHeight() * 1.5f;
				}

			}

			@Override
			public void release(int key) {
				// TODO Auto-generated method stub

			}

		});
		AvianApp.addMouseListener(new AvianMouse() {

			@Override
			public void press(int button, float x, float y) {

			}

			@Override
			public void release(int button, float x, float y) {
				if (!open || button != 0)
					return;
				if (y > 100 - listFileHeight - fontHeight / 4f) {
					if (y < 100 - fontHeight / 4f && directory.getParentFile() != null) {
						directory = directory.getParentFile();
						sourceY = 0;
						files = null;
						finalFileOffset = fileOffset = 26 + 64;

					} else if (files != null) {
						for (int i = 0; i < files.length; i++) {
							if (files[i].toggleSelect(y) && files[i].isDirectory()) {
								directory = files[i];
								sourceY = files[i].y + fileOffset;
								files = null;
								finalFileOffset = fileOffset = 0;
								break;
							}
						}

					}

				} else {
					int selection = (int) (x * 3 / AvianApp.getWidth());
					switch (selection) {
						case 0:
							close();

							break;
						case 1:
							addSelection();
							break;
						case 2:
							addDirectory();
							break;
						case 3:
							//							addDirectoryAndSubs();
							break;
					}
				}
			}

			@Override
			public void move(float x, float y) {
				if (!open)
					return;
				if (files == null || !open)
					return;
				for (int i = 0; i < files.length; i++) {
					files[i].hover(y);
				}
				hoverUp = y > 100 - listFileHeight - fontHeight / 4f && y < 100 - fontHeight / 4f;

			}

			@Override
			public void scroll(int count) {
				if (!open)
					return;
				finalFileOffset += AvianApp.Vegur().getHeight() * 1.5 * count;
			}

		});
	}

	void addSelection() {
		boolean somethingSelected = false;

		for (int i = 0; i < files.length; i++) {
			if (files[i].selected) {
				somethingSelected = true;
				break;
			}
		}

		if (!somethingSelected)
			return;

		ArrayList<String> newFiles = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory() && files[i].selected) {
				newFiles.add(files[i].getPath());
			}
		}
		handleFiles(newFiles.toArray(new String[newFiles.size()]));

	}

	void addDirectory() {
		boolean hasFiles = false;

		for (int i = 0; i < files.length; i++) {
			if (!hasFiles && !files[i].isDirectory()) {
				hasFiles = true;
				break;
			}
		}

		if (!hasFiles)
			return;

		ArrayList<String> newFiles = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory()) {
				newFiles.add(files[i].getPath());
			}
		}

		handleFiles(newFiles.toArray(new String[newFiles.size()]));

	}

	//	private void addDirectoryAndSubs() {
	//		if (directory.getParent() == null)
	//			return;
	//
	//		ArrayList<String> newFiles = new ArrayList<String>();
	//		addDirectoryAndSubs(newFiles, directory);
	//		handlingFiles = true;
	//		handleFiles(newFiles.toArray(new String[newFiles.size()]));
	//	}
	//
	//	private void addDirectoryAndSubs(ArrayList<String> list, File directory) {
	//		File[] newFiles = directory.listFiles(ft == FileTypes.IMAGES ? imgs : music);
	//		if (newFiles == null)
	//			return;
	//		for (int i = 0; i < newFiles.length; i++) {
	//			if (newFiles[i].isDirectory()) {
	//				addDirectoryAndSubs(list, newFiles[i]);
	//			} else {
	//				list.add(newFiles[i].getPath());
	//			}
	//		}
	//	}

	boolean open;

	void open() {
		open = true;
	}

	void close() {
		open = false;
		sourceY = 0;
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				files[i].selected = false;
			}
		}
	}

	boolean isOpen() {
		return open;
	}

	public void logic() {
		if (open) {
			fade.forward();
			if (files == null) {
				File[] newFiles = directory.listFiles(ft == FileTypes.IMAGES ? imgs : music);
				if (newFiles == null) {
					files = new ListFile[0];
				} else {
					files = new ListFile[newFiles.length];

					for (int i = 0; i < newFiles.length; i++) {
						files[i] = new ListFile(newFiles[i], sourceY);
					}
					Arrays.sort(files, sorter);
				}
			}
			if (finalFileOffset < AvianApp.getHeight() - 100 - files.length * listFileHeight) {
				finalFileOffset = AvianApp.getHeight() - 100 - files.length * listFileHeight;
			}
			if (finalFileOffset > 0)
				finalFileOffset = 0;
			fileOffset = AvianMath.glide(fileOffset, finalFileOffset, 5f);
			for (int i = 0; i < files.length; i++) {
				files[i].logic(i);
			}
			upAlpha = AvianMath.glide(upAlpha, hoverUp ? .2f : 0, 10f);

		} else
			fade.rewind();
		//		Iterator<ListFile> iter = trash.iterator();
		//		while (iter.hasNext()) {
		//			ListFile l = iter.next();
		//			l.trashLogic();
		//			if (l.isGone())
		//				iter.remove();
		//		}

	}

	public void render() {
		float fade1 = this.fade.result() * Settings.getAlpha();

		r.set(0, 0, AvianApp.getWidth(), AvianApp.getHeight());
		r.render(0, (200f * fade1) / 255f);
		AvianFont f = AvianApp.Vegur();
		//		if (trash.size() > 0) {
		//			for (int i = 0; i < trash.size(); i++) {
		//				trash.get(i).render(fade);
		//			}
		//		}
		if (files != null) {

			boolean somethingSelected = false;
			boolean hasFiles = false;

			for (int i = 0; i < files.length; i++) {
				if (!hasFiles && !files[i].isDirectory()) {
					hasFiles = true;
				}
				if (files[i].selected) {
					somethingSelected = true;
					break;
				}
			}

			AvianFont.setAlignment(AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);
			f.drawString(AvianApp.getWidth() / 6, 32, "Cancel", fade1);
			f.drawString(AvianApp.getWidth() * 3 / 6, 32, "Add selected", somethingSelected ? fade1 : 100f * fade1 / 255f);
			f.drawString(AvianApp.getWidth() * 5 / 6, 32, "Add all in folder", hasFiles ? fade1 : 100f * fade1 / 255f);
			//			f.drawString("Add all in folder and subfolders", AvianApp.getWidth() * 7 / 8, 32, AvianColor.white(directory.getParent() != null ? 255 * fade : 100 * fade), AvianFont.ALIGN_CENTER, AvianFont.ALIGN_CENTER);

			AvianFont.setAlignment(AvianFont.ALIGN_LEFT, AvianFont.ALIGN_TOP);
			if (directory.getParentFile() != null) {
				r.set(0, 100 - listFileHeight - fontHeight / 4f, AvianApp.getWidth(), listFileHeight);
				r.render(upAlpha * fade1);
				up.render(50, 101 - listFileHeight, 15, 15, fade1);
				f.drawString(74 + f.getWidth(directory.getParent()), 100 - listFileHeight, directory.getPath().substring(directory.getParent().length()), fade1);
				f.drawString(74, 100 - listFileHeight, directory.getParent(), .6f * fade1);

			} else {
				up.render(50, 101 - listFileHeight, 15, 15, 100 * fade1 / 255f);
				f.drawString(74 + f.getWidth(directory.getParent()), 100 - listFileHeight, directory.getPath(), fade1);
			}

			if (files.length > 0 && files != null) {
				for (int i = 0; i < files.length; i++) {
					files[i].render(fade1);
				}
			} else {
				AvianFont.setVerticalAlignment(AvianFont.ALIGN_CENTER);
				f.drawString(AvianApp.getHeight() / 2, "Nothing here", fade1);
			}
		}
	}

	class ListFile extends File {
		private static final long serialVersionUID = -6563117063582909355L;

		boolean selected, hover;
		float y;
		float alpha;

		float phase, vel = .01f;
		int num;

		public ListFile(File file, float y) {
			super(file.getPath());
			this.y = y;

		}

		public boolean toggleSelect(float my) {
			if (!trash && hover(my)) {
				selected = !selected;
			}
			return selected;
		}

		public boolean hover(float my) {
			if (!trash && my > 100 - fontHeight / 4f & my > 100f + num * listFileHeight + fileOffset - fontHeight / 4f && my < 100f + num * listFileHeight + listFileHeight + fileOffset - fontHeight / 4f) {
				return hover = true;
			}
			return hover = false;
		}

		public void logic(int n) {
			this.num = n;
			phase -= 1f;
			y = AvianMath.glide(y, 100f + n * fontHeight * 1.5f, 10f);
			alpha = AvianMath.glide(alpha, (selected ? (AvianMath.sin(phase + 10f * n) + 1f) / 2f * 20f + 50 : 0) + (hover ? 50 : 0), 10f);
		}

		boolean trash;

		public void trashLogic() {
			trash = true;
			if (y > sourceY) {
				y = AvianMath.glide(y, AvianApp.getHeight() + fontHeight / 4f, 10f);
			} else {
				y = AvianMath.glide(y, 70 - listFileHeight * 2, 10f);

			}
		}

		public boolean isGone() {
			return trash && y > AvianApp.getHeight();
		}

		public void render(float f) {
			if (y + fileOffset > AvianApp.getHeight() || y + fileOffset < 70 - fontHeight / 4f)
				return;
			AvianApp.Vegur();
			AvianFont.setVerticalAlignment(AvianFont.ALIGN_TOP);

			if (!trash) {
				r.set(0, y + fileOffset - fontHeight / 4f, AvianApp.getWidth(), listFileHeight);
				r.render((f * alpha * (y + fileOffset < 95 ? ((y + fileOffset - 90) / 5f) : 1)) / 255f);
				float textAlpha = f;
				if (y + fileOffset < 100 - fontHeight / 4f) {
					textAlpha = ((y + fileOffset - 70f) / 30f);
				}
				if (isDirectory()) {
					folder.render(100, (y + 1) + fileOffset, 15, 15, textAlpha);
				} else {
					if (ft == FileTypes.IMAGES) {
						Images.img.render(100, (y + 1) + fileOffset, 15, 15, textAlpha);
					} else {
						Images.note.render(100, (y + 1) + fileOffset, 15, 15, textAlpha);
					}
				}
				AvianApp.Vegur().drawString(124, y + fileOffset, getName(), textAlpha);
			} else {
				float textAlpha = .4f;
				if (y < 100 - fontHeight / 4f) {
					textAlpha = ((y - 70f) / 30f) * 100f * f / 255f;
				}
				if (isDirectory()) {
					folder.render(100, (y + 1), 15, 15, textAlpha);
				} else {
					if (ft == FileTypes.IMAGES) {
						Images.img.render(100, (y + 1), 15, 15, textAlpha);
					} else {
						Images.note.render(100, (y + 1), 15, 15, textAlpha);
					}
				}
				AvianApp.Vegur().drawString(124, y, getName(), textAlpha);
			}
		}

	}

	public abstract void handleFiles(String[] paths);

}
