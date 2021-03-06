import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class GameSelectionScreen implements Game {
	JSONArray games;
	BufferedImage[] preloadedGameThumbnails;
	int selected = 0;
	double scroll = 0;
	double anim = 0;
	double animMovement = 0;
	Color sky = new Color(52, 174, 235);
	Color selectionColor = new Color(255, 60, 0);
	BufferedImage Logo;
	int i = 0, XRow = 0;
	boolean settings = false;
	BufferedImage Switch, SwitchOff;
	JSONObject data;
	BufferedImage gear;
	static final Logger log = LoggerFactory.getLogger(Main.class);
	String[] classes;
	int urzr = 0;

	public GameSelectionScreen(String[]gameNames) throws IOException {
		Main.INSTANCE.frame.setBounds(0, 0, 800, 600);
		gear = ImageIO.read(GameSelectionScreen.class.getClassLoader().getResourceAsStream("Gear.png"));
		String[] games = new String[gameNames.length -1]; 
		for (int i = 0; i<games.length; i++) {games[i] = gameNames[i+1];}
		//log.debug("Games in Selection Screen");
		//for (String h : games) {System.out.println(h);}
		classes = gameNames;
		int i = 0;
		Switch = ImageIO.read(GameSelectionScreen.class.getClassLoader().getResourceAsStream("Switch.png"));
		SwitchOff = ImageIO.read(GameSelectionScreen.class.getClassLoader().getResourceAsStream("SwitchOff.png"));
		preloadedGameThumbnails = new BufferedImage[gameNames.length-1];
		for(String x : games) {
			//System.out.println(x+"-thumbnail.png");
			preloadedGameThumbnails[i] = ImageIO.read(GameSelectionScreen.class.getClassLoader().getResourceAsStream(x+"-thumbnail.png"));
		}
	}

	public BufferedImage draw(Dimension size) {
		
		BufferedImage result = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) result.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if (urzr ==0 ) {
			System.out.println(g);
			urzr =1;
		}
		// Rendering
		g.setColor(sky);
		g.fill(new Rectangle(new Point(), size));
		g.drawImage(gear, 710,20, 50,50 ,null);

		if(!settings) {
			for (int i = 0; i < preloadedGameThumbnails.length; i++) {
				XRow = ((int) Math.floor((i+1)/4))*300;
				g.drawImage(preloadedGameThumbnails[i], 75 + XRow, 50 + i * 190 - XRow * 2, 250, 140, null);
				if(i == selected) {
					g.setColor(selectionColor);
					int a = (int) Math.round(8 * Math.sin(anim));
					g.fillPolygon(new int[] { 35 + a + XRow, 35 + a + XRow , 55 + a + XRow }, new int[] { (int) (105 + (i + animMovement) * 190) - XRow*2, (int) (135 + (i + animMovement) * 190) - XRow*2, (int) (120 + (i + animMovement) * 190 - XRow*2) }, 3);
					g.fillPolygon(new int[] { 400 - 35 - a + XRow, 400 - 35 - a + XRow, 400 - 55 - a + XRow}, new int[] { (int) (105 + (i + animMovement) * 190) - XRow*2, (int) (135 + (i + animMovement) * 190) - XRow*2, (int) (120 + (i + animMovement) * 190) - XRow*2 }, 3);
				}
			}
			anim += 0.09;
			animMovement *= 0.9;
		} else {
			/*String[] arr = data.toMap().entrySet().toArray()[0].toString().substring(7).split("}, {");
			//print(arr[0]);

			//for(String x:data.keys()) {
				
			//}
			//((x,y) -> {
				g.drawString(x, 50, 50+20*i);
				if (types.charAt(i) == 'd') {
					g.drawString(String.valueOf((Double)y), 100, 50+20*i);
				} else {
					if ((Boolean)y) {
						g.drawImage(Switch, 100, 50+20*i, null);
					} else {
						g.drawImage(SwitchOff, 100, 50+20*i, null);
					}
					
				}
			});*/
		}

		return result;
	}

	private void print(Object obj) {
		System.out.println(obj);
	}

	public void keyPressed(KeyEvent event) {
		if(!settings) {
			if(event.getKeyCode() == KeyEvent.VK_ENTER) {
				Main.INSTANCE.switchGame(selected+1);
			}
			if(event.getKeyCode() == KeyEvent.VK_UP) {
				if(selected > 0) {
					selected--;
					animMovement += 1;
				}
			}
			if(event.getKeyCode() == KeyEvent.VK_DOWN) {
				if(selected < classes.length - 1) {
					selected++;
					animMovement -= 1;
				}
			}
			//System.out.println(selected);
		}
	}

	public void keyReleased(KeyEvent event) {}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (new Rectangle(710,20,50,50).contains(e.getPoint())) {
			settings = !settings;
		}
		if(!settings) {
			for (int i = 0; i<Math.floor(games.length()*3);i++){
				for (int j = 0;j<3;j++){
					if (new Rectangle(3+300*i,3+190*j,250,140).contains(e.getPoint())) {
						selected = i*3+j;
						Main.INSTANCE.switchGame(selected);
					}
				}
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}
}