import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.awt.geom.Ellipse2D;

public class SpaceDestroyer implements Game {
    List<Integer> keys;
    Boolean Dead = false;
    BufferedImage Ship, opponent, Space, dead, Victory;
    Clip Pew,Pew2;
    Rectangle ShipCol = new Rectangle(180,500,40,32);
    int tick = 0;
    List<double[]> Projektiles, Opponents;
    int Coldown = 0;
    double[] temp;
    

    SpaceDestroyer() throws IOException {
        keys = new ArrayList<Integer>();
        Projektiles = new ArrayList<double[]>();
        Opponents = new ArrayList<double[]>();
        Main.INSTANCE.frame.setBounds(646,219,400,600);        
        Ship = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("Ship.png"));
        dead = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("Dead.png"));
        Victory = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("Victory.png")); 

        for (int i = 0; i<2; i++) {
            for (int l = 0; l<7; l++) {
                temp = new double[] {10+50*l,100+80*i,10};
                Opponents.add(temp);
            }
        }
    }
    

    public BufferedImage draw(Dimension size) {
        BufferedImage result = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) result.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.white);

        try {
            if (tick == 0) {
                Space = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("Space.png"));
            }
            if (tick == -50 || tick == 0 || tick == 25 || tick == 50) {
                opponent = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("opponent1.png"));
            }
            if (tick == -25 || tick == 25 || tick == 75) {
                opponent = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("opponent2.png"));
            }
            if (tick == 75) {
                Space = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("Space2.png"));
                tick = -75;
            }
            tick ++;
        } catch (IOException e) {}
        g.drawImage(Space, 0, 0,400,600, null);

        g.drawImage(Ship,(int) ShipCol.getMinX(), (int) ShipCol.getMinY(), 40,32, null);

        for (int i = 0; i < Projektiles.size(); i++) {
            g.fill(new Ellipse2D.Double(Projektiles.get(i)[0], Projektiles.get(i)[1],10,10));
            if (Projektiles.get(i)[1] > 600 || Projektiles.get(i)[1] < -10) {
                Projektiles.remove(i);
                i--;
            }
        }
        for (int i = 0; i < Opponents.size(); i++) {
            g.drawImage(opponent,(int) Opponents.get(i)[0],(int) Opponents.get(i)[1],40,38,null);
        }

        MoveProjektiles();
        Coldown --;

        Rectangle opponentcol = new Rectangle(0,0,40,38);
        try {
            for (int i=0;i < Projektiles.size();i++) {
                if (Projektiles.get(i)[3] == 1.0) {
                    for(int l=0;l < Opponents.size();l++) {
                        opponentcol.setLocation((int) Opponents.get(l)[0],(int) Opponents.get(l)[1]);
                        if (opponentcol.intersects(new Rectangle((int) Projektiles.get(i)[0],(int) Projektiles.get(i)[1],10,10))) {
                            double[] temp = new double[] {Opponents.get(l)[0], Opponents.get(l)[1],Opponents.get(l)[2]-1};
                            Projektiles.remove(i);
                            Opponents.set(l, temp);
                        }
                    }
                } else {
                    if (ShipCol.intersects(new Rectangle((int) Projektiles.get(i)[0],(int) Projektiles.get(i)[1],10,10))) {
                        Dead = true;
                    }
                }
            }
        } catch (IndexOutOfBoundsException e){e.printStackTrace();}
        for (int i=0;i<Opponents.size();i++) {
            if (Opponents.get(i)[2] == 0) {
                Opponents.remove(i);
                i--;
            }
        }

        if (Opponents.size() == 0){
            g.drawImage(Victory, 0,250, 400, 100, null);
            if (keys.size() != 0){
                keys.remove(0);
            }
            ShipCol.setLocation((int) ShipCol.getMinX(),(int) ShipCol.getMinY()-4);
        }

        //Op shoot
        for (int i = 0; i<Opponents.size(); i++) {
            if (new Random().nextInt(360) == 0 && !Dead) {
                double[] temp = new double[] {Opponents.get(i)[0]+20, Opponents.get(i)[1], 4.0,0.0};
                Projektiles.add(temp);
            }
        }
        if (Dead) {
            g.drawImage(dead, 50 , 250, null);
        }
        if(!Dead) {
            if (keys.contains(KeyEvent.VK_A)) {
                ShipCol.setLocation((int) ShipCol.getMinX()-5,(int) ShipCol.getMinY());
            }
            if (keys.contains(KeyEvent.VK_D)) {
                ShipCol.setLocation((int) ShipCol.getMinX()+5,(int) ShipCol.getMinY());
            }
            if (keys.contains(KeyEvent.VK_W)) {
                ShipCol.setLocation((int) ShipCol.getMinX(),(int) ShipCol.getMinY()-5);
            }
            if (keys.contains(KeyEvent.VK_S)) {
                ShipCol.setLocation((int) ShipCol.getMinX(),(int) ShipCol.getMinY()+5);
            }
            //Ship shoot
            if (keys.contains(KeyEvent.VK_SPACE)) {
                if (Coldown<0) {
                    double[] temp  = {ShipCol.getCenterX(),ShipCol.getMinY(),-5.0,1.0};
                    Projektiles.add(temp);
                    Coldown = 10;
                }
            }
        }
        if (ShipCol.getY()<-30){
            try {
                Main.INSTANCE.currentGame = new GameSelectionScreen();
            } catch (IOException f) {}
        }
        return result;
    }





    public void keyPressed(KeyEvent e) throws IOException {
        if(!Dead) {
            if (!keys.contains(e.getKeyCode())) {
                keys.add(e.getKeyCode());
            }
        } else {
            if (e.getKeyCode() == KeyEvent.VK_ENTER || ShipCol.getCenterY()<-30) {
                try {
                    Main.INSTANCE.currentGame = new GameSelectionScreen();
                } catch (IOException f) {}
            }
        }
    }
    public void keyReleased(KeyEvent e) {
        if (!Dead) {
            if (keys.contains(e.getKeyCode())) {
            keys.remove(keys.indexOf(e.getKeyCode()));
            }
        }
    }
    void MoveProjektiles() {
        double Y,VelY;
        for (int i = 0;i < Projektiles.size();i++) {
            Y = Projektiles.get(i)[1];
            VelY = Projektiles.get(i)[2];

            double[] temp = {Projektiles.get(i)[0], Y + VelY, VelY, Projektiles.get(i)[3]};
            Projektiles.set(i, temp);
        }
    }
} 