
package app;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import musicaflight.avianutils.AvianApp;
import musicaflight.avianutils.AvianImage;

public class Paper implements Comparable<Paper> {

	AvianImage normal;
	AvianImage blur;
	String nPath;
	String bPath;
	boolean ready = false, loading = false;;
	private boolean destroyed = false;
	String name;
	String data;

	public Paper(String data) {
		this.data = data;
		String[] split = data.split("::");
		if (split.length < 2) {
			name = new File(data).getName();
		} else {
			name = new File(nPath = (split[0])).getName();
		}
	}

	public AvianImage getNormalImage() {
		return normal;
	}

	public AvianImage getBlurredImage() {
		return blur;
	}

	public void render(float a, boolean b) {
		if (destroyed || !ready) {
			return;
		}
		AvianImage i = b ? blur : normal;
		if (i == null)
			return;
		i.setSharpTexture(false);

		float sW = AvianApp.getWidth();
		float sH = AvianApp.getHeight();

		float pX, pY, pW, pH;

		float screenRatio = sW / sH;
		float picRatio = i.getWidth() / i.getHeight();

		if (screenRatio < picRatio) {
			pW = sH * picRatio;
			pH = sH;
			pX = sW / 2 - (pW) / 2;
			pY = 0;
		} else {
			pW = sW;
			pH = sW / picRatio;
			pX = 0;
			pY = sH / 2 - (pH) / 2;
		}
		i.uncrop();
		i.render(pX, pY, pW, pH, a);
	}

	public String getName() {
		if (destroyed)
			return null;
		return name;
	}

	public int compareTo(Paper p) {
		if (destroyed)
			return 1;
		if (p.getName() == null || getName() == null)
			return 0;
		return getName().compareTo(p.getName());
	}

	public String load() {
		loading = true;
		ready = false;
		String[] split = data.split("::");
		if (split.length < 2) {
			try {
				File f = new File(data);
				if (!f.exists()) {
					destroyed = true;
					return null;
				}
				BufferedImage img = ImageIO.read(f);
				BufferedImage canvas;

				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				double max = Math.max(screenSize.getWidth(), screenSize.getHeight());

				if (img.getWidth() > img.getHeight()) {
					canvas = new BufferedImage((int) (max * img.getWidth() / img.getHeight()), (int) max, BufferedImage.TYPE_INT_ARGB);
				} else {
					canvas = new BufferedImage((int) max, (int) (max / ((float) img.getWidth() / (float) img.getHeight())), BufferedImage.TYPE_INT_ARGB);
				}

				Graphics2D g = (Graphics2D) canvas.getGraphics();
				g.drawImage(img, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
				g.dispose();
				g = null;
				img = null;

				String n = f.getName();
				n = n.substring(0, n.lastIndexOf("."));
				f = new File(DataHost.getDataDirectory() + "papers/" + n + ".png");
				if (!f.exists()) {
					f.getParentFile().mkdirs();
					f.createNewFile();
				}
				ImageIO.write(canvas, "png", f);
				nPath = f.getCanonicalPath();

				AvianImage i = new AvianImage(canvas);
				canvas = null;
				i.gaussianBlur(20);
				f = new File(DataHost.getDataDirectory() + "papers/blurred/" + n + ".png");
				if (!f.exists()) {
					f.getParentFile().mkdirs();
					f.createNewFile();
				}
				ImageIO.write(i.getBufferedImage(), "png", f);
				bPath = f.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			bPath = (split[1]);
			nPath = (split[0]);
			if (!new File(split[0]).exists() || !new File(split[1]).exists()) {
				destroyed = true;
				return null;
			}

		}
		normal = new AvianImage(nPath);
		blur = new AvianImage(bPath);
		ready = true;
		loading = false;
		return data = nPath + "::" + bPath;
	}

	public Paper release() {
		if (destroyed)
			return null;
		if (normal != null) {
			normal = normal.destroy();
		}
		if (blur != null) {
			blur = blur.destroy();
		}
		ready = false;
		return null;
	}

	public void destroy() {
		if (destroyed)
			return;

		release();
		new File(nPath).delete();
		new File(bPath).delete();
		ready = false;
		destroyed = true;
	}

	public boolean isDestroyed() {

		return destroyed;
	}

	public String getNormalFilepath() {
		if (destroyed)
			return null;

		return nPath;
	}

	public String getBlurredFilepath() {
		if (destroyed)
			return null;

		return bPath;
	}

	public boolean isLoaded() {
		if (destroyed)
			return false;

		return ready && !loading;
	}

}
