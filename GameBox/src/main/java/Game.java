import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public interface Game {
	public BufferedImage draw(Dimension size);
	public void keyPressed(KeyEvent event) throws IOException;
	public void keyReleased(KeyEvent event);
    public void mouseClicked(MouseEvent e);
    public void mouseMoved(MouseEvent e);
}