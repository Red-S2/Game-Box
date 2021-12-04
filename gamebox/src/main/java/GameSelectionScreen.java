import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GameSelectionScreen implements Game {
	BufferedImage[] gameThumbnails;
	Class<?>[] gameClasses;
	int selected = 0;
	double scroll = 0;
	double anim = 0;
	double animMovement = 0;
	Color sky = new Color(52, 174, 235);
	Color selectionColor = new Color(255, 60, 0);
	BufferedImage Logo;
	int i = 0, XRow = 0;

	public GameSelectionScreen() throws IOException {
		try {
			Logo = ImageIO.read(GameSelectionScreen.class.getClassLoader().getResourceAsStream("Logo.png"));
			gameThumbnails = new BufferedImage[] { ImageIO.read(GameSelectionScreen.class.getClassLoader().getResourceAsStream("Flappy.png")) , ImageIO.read(GameSelectionScreen.class.getClassLoader().getResourceAsStream("Pong.png")), ImageIO.read(GameSelectionScreen.class.getClassLoader().getResourceAsStream("Snake.png")), ImageIO.read(GameSelectionScreen.class.getResourceAsStream("SpaceDestroyer.png")) };
			gameClasses = new Class<?>[] { FlappyBird.class , Pong.class, Snake.class, SpaceDestroyer.class};
		} catch (IOException | java.lang.IllegalArgumentException e) {
			System.out.println("Data Error");
		}
		Main.INSTANCE.frame.setIconImage(Logo);
		Main.INSTANCE.frame.setBounds(646,219,800,680);
	}

	public BufferedImage draw(Dimension size) {
		BufferedImage result = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) result.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Rendering
		g.setColor(sky);
		g.fill(new Rectangle(new Point(), size));
		i = 0;
		
		while (i<4) {
			XRow = ((int) Math.floor((i+1)/4))*300;
			g.drawImage(gameThumbnails[i], 75 + XRow, 50 + i * 190 - XRow * 2, 250, 140, null);
			if(i == selected) {
				g.setColor(selectionColor);
				int a = (int) Math.round(8 * Math.sin(anim));
				g.fillPolygon(new int[] { 35 + a + XRow, 35 + a + XRow , 55 + a + XRow }, new int[] { (int) (105 + (i + animMovement) * 190) - XRow*2, (int) (135 + (i + animMovement) * 190) - XRow*2, (int) (120 + (i + animMovement) * 190 - XRow*2) }, 3);
				g.fillPolygon(new int[] { 400 - 35 - a + XRow, 400 - 35 - a + XRow, 400 - 55 - a + XRow}, new int[] { (int) (105 + (i + animMovement) * 190) - XRow*2, (int) (135 + (i + animMovement) * 190) - XRow*2, (int) (120 + (i + animMovement) * 190) - XRow*2 }, 3);
			}
			i++;
		}
		anim += 0.09;
		animMovement *= 0.9;

		return result;
	}

	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_ENTER) {
			try {
				if (selected == 0) {
					Main.INSTANCE.currentGame = new FlappyBird();
				}
				if (selected == 1) {
					Main.INSTANCE.currentGame = new Pong();
				}
				if (selected == 2) {
					Main.INSTANCE.currentGame = new Snake();
				}
				if (selected == 3) {
					Main.INSTANCE.currentGame = new SpaceDestroyer();
				}
				//Main.INSTANCE.currentGame = (Game) gameClasses[selected].getConstructor().newInstance();
			} catch (IOException | IllegalArgumentException/* | InvocationTargetException | NoSuchMethodException | SecurityException*/ | UnsupportedAudioFileException | LineUnavailableException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		if(event.getKeyCode() == KeyEvent.VK_UP) {
			if(selected > 0) {
				selected--;
				animMovement += 1;
			}
		}
		if(event.getKeyCode() == KeyEvent.VK_DOWN) {
			if(selected < gameThumbnails.length - 1) {
				selected++;
				animMovement -= 1;
			}
		}
	}

	public void keyReleased(KeyEvent event) {}
}
