import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

class Main {
	public JFrame frame;
	public Canvas canvas;
	public BufferedImage Flappybird;
	public Game currentGame;

	public static void main(String[] args) throws IOException {
		Main main = new Main();
		main.init();
		while(true) {
			render(main.canvas, main.draw(main.canvas.getSize()));
		}
	}

	public void init() throws IOException {
		frame = new JFrame();
		frame.setTitle("GameBox");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setBackground(Color.WHITE);
		canvas = new Canvas();
		frame.add(canvas);
		canvas.setPreferredSize(new Dimension(400, 600));
		frame.pack();
		frame.setVisible(true);
		canvas.createBufferStrategy(2);
		Flappybird = ImageIO.read(new File("Flappybird.png"));
	}

	public BufferedImage draw(Dimension size) {
		BufferedImage result = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) result.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Rendering
		if(currentGame == null) {
			g.setColor(Color.white);
			g.fill(new Rectangle(new Point(),size));
			g.drawImage(Flappybird, 75, 50, null);
		} else g.drawImage(currentGame.draw(size), 0, 0, null);

		return result;
	}

	public static void render(Canvas canvas, BufferedImage img) {
		BufferStrategy bs = canvas.getBufferStrategy();
		Graphics g = bs.getDrawGraphics();
		g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
		g.dispose();
		bs.show();
	}
}
