import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.json.JSONObject;

public class SpaceDestroyer implements Game {
    //Deklaration
    List<Integer> keys;
    Boolean Dead = false;
    BufferedImage Ship, opponent, Space, dead, Victory, EnemyShot, FriendlyShot, PowerUp;
    Rectangle ShipCol = new Rectangle(180,500,30,30);
    int tick = 0;
    List<double[]> Projektiles, Opponents;
    int Coldown = 0, PowerState = 1;
    Clip Boom;
    List<Rectangle> Particles;
    List<Color> Colors;
    JSONObject data;

    public SpaceDestroyer(JSONObject data) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        this.data = data;
        keys = new ArrayList<Integer>();
        Projektiles = new ArrayList<double[]>();
        Opponents = new ArrayList<double[]>();       
        PowerUp = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("PowerUp.png"));
        Ship = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("Ship1.png"));
        dead = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("Dead.png"));
        Victory = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("Victory.png"));
        EnemyShot = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("EnemyShot.png"));
        FriendlyShot = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("FriendlyShot.png"));
        Particles = new ArrayList<Rectangle>();
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Main.baInputStream(SpaceDestroyer.class.getClassLoader().getResourceAsStream("Boom.wav")));
        Boom = AudioSystem.getClip();
        Boom.open(audioInputStream);
        Colors = new ArrayList<Color>();
        //opponent creating
        for (int i = 0; i<3; i++) {
            for (int l = 0; l<6; l++) {
                Opponents.add(new double[] {5+70*l,10+60*i,data.getInt("enemyHealth")});
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
            //animation
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
        } catch (IOException e) {/*won't happen*/}
        g.drawImage(Space, 0, 0,400,600, null);
        
        //render
        
        for (int i = 0; i < Projektiles.size(); i++) {
        switch ((int) Projektiles.get(i)[4]) {
                case 0: AffineTransform t;
                        t = g.getTransform();        
                        t.rotate(Math.atan(Projektiles.get(i)[3]/Projektiles.get(i)[2])+Math.PI/2,Projektiles.get(i)[0]+4, Projektiles.get(i)[1]+16);
                        g.setTransform(t);  
                        g.drawImage(EnemyShot, (int) Projektiles.get(i)[0],(int) Projektiles.get(i)[1], null);
                        g.setTransform(new AffineTransform());
                        break;
                case 1: AffineTransform T;
                        T = g.getTransform();        
                        T.rotate(Math.toRadians(5*(Projektiles.get(i)[2]*4)) ,Projektiles.get(i)[0]+4, Projektiles.get(i)[1]+16);
                        g.setTransform(T);         
                        g.drawImage(FriendlyShot,(int) Projektiles.get(i)[0],(int) Projektiles.get(i)[1], null);
                        g.setTransform(new AffineTransform());
                        break;
                case 2: g.drawImage(PowerUp, (int) Projektiles.get(i)[0],(int) Projektiles.get(i)[1], null);
                        break;
                }
		
            if (Projektiles.get(i)[1] > 600 || Projektiles.get(i)[1] < -10) {
                Projektiles.remove(i);
                i--;
            }
        }
        g.setColor(Color.red);
        for (int i = 0; i < Opponents.size(); i++) {
            g.drawImage(opponent,(int) Opponents.get(i)[0],(int) Opponents.get(i)[1],40,38,null);
            if (Opponents.get(i)[2] != data.getInt("enemyHealth")){
                g.drawRect((int) Opponents.get(i)[0],(int) Opponents.get(i)[1]-12, 38, 8);
                g.fillRect((int) Opponents.get(i)[0],(int) Opponents.get(i)[1]-12, 38-(int) ((38/data.getInt("enemyHealth"))*(data.getInt("enemyHealth")-Opponents.get(i)[2])), 8);
            }
        }

        MoveProjektiles();
        Coldown --;

        Rectangle opponentcol = new Rectangle(0,0,36,38);
        try {
            for (int i=0;i < Projektiles.size();i++) {
                double[] x = Projektiles.get(i);
                switch ((int) x[4]) {
                    case 1: for(int l=0;l < Opponents.size();l++) {
                                opponentcol.setLocation((int) Opponents.get(l)[0]+2,(int) Opponents.get(l)[1]);
                                if (opponentcol.intersects(new Rectangle((int) x[0],(int) x[1],10,10))) {
                                    Projektiles.remove(i);
                                    GenParticles((int)x[0]+5,(int) x[1],3);
                                    Opponents.set(l, new double[] {Opponents.get(l)[0], Opponents.get(l)[1],Opponents.get(l)[2]-1});
                                }
                            }//Colision
                            break;
                    case 0: if (ShipCol.intersects(new Rectangle((int) x[0],(int) x[1],10,10))) {
                                if(data.getBoolean("instantDeath")) {
                                    Dead = true;
                                } else {
                                    PowerState --;
                                    try {
                                        Ship = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("Ship" + String.valueOf(PowerState) + ".png"));
                                    } catch (IOException | IllegalArgumentException e) {
                                        Ship = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("Ship1.png"));
                                    }
                                    ShipCol.grow(-5, -5);
                                    for (int j = 0;j<6;j++){
                                        for (int l = 0;l<6;l++) {
                                            GenParticles(ShipCol.x+j*5,ShipCol.y+l*5,5);
                                        }
                                    if (PowerState == 0) {
                                        Dead = true;
                                    }
                                }
                                Projektiles.remove(i);
                                Boom.start();
                                
                            }
                        break;
                    }
                    case 2: if (ShipCol.intersects(new Rectangle((int) x[0],(int) x[1],20,10))) {
                                if (PowerState != 5){
                                    PowerState++;
                                    Projektiles.remove(i);
                                    ShipCol.grow(5, 5);
                                    try {
                                        Ship = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("Ship" + String.valueOf(PowerState) + ".png"));
                                    } catch (IOException | IllegalArgumentException e) {
                                        e.printStackTrace();
                                        Ship = ImageIO.read(SpaceDestroyer.class.getResourceAsStream("Ship1"));
                                    }
                                }
                            }
                    break;
                }
            }
        } catch (IndexOutOfBoundsException | IOException e){e.printStackTrace();}
        for (int i=0;i<Opponents.size();i++) {
            if (Opponents.get(i)[2] <= 0) {
                if (new Random().nextInt(3) == 2) {
                    Projektiles.add(new double[] {Opponents.get(i)[0]+10, Opponents.get(i)[1],0, 2,2});
                }
                for (int j = 0;j<6;j++){
                    for (int l = 0;l<6;l++) {
                        GenParticles((int)Opponents.get(i)[0]+j*5,(int)Opponents.get(i)[1]+l*5,5);
                    }
                }
                Opponents.remove(i);
                i--;
                Boom.start();
                
            }
        }
        if (Boom.getFramePosition() == 44100) {
            Boom.setFramePosition(0);
            Boom.stop();
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
                double x;
                double y;
                if (data.getBoolean("homing")) {
                    x = ShipCol.getCenterX()-5 - Opponents.get(i)[0];
                    y = ShipCol.getCenterY() - Opponents.get(i)[1];
                    double Distance = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
                    x = x*(4/Distance);
                    y = y*(4/Distance);
                } else {
                    x = 0;
                    y = 4;
                }
                Projektiles.add(new double[] {Opponents.get(i)[0]+20, Opponents.get(i)[1],x,y,0});
            }
        }
        if(!Dead) {
            GenParticles((int) ShipCol.getCenterX(),(int) ShipCol.getMaxY(),0);
            if (keys.contains(KeyEvent.VK_A) || keys.contains(KeyEvent.VK_LEFT)) {
                ShipCol.setLocation((int) ShipCol.getMinX()-5,(int) ShipCol.getMinY());
            }
            if (keys.contains(KeyEvent.VK_D) || keys.contains(KeyEvent.VK_RIGHT)) {
                ShipCol.setLocation((int) ShipCol.getMinX()+5,(int) ShipCol.getMinY());
            }
            if (keys.contains(KeyEvent.VK_W) || keys.contains(KeyEvent.VK_UP)) {
                ShipCol.setLocation(ShipCol.x,ShipCol.y-5);
            }
            if (keys.contains(KeyEvent.VK_S) || keys.contains(KeyEvent.VK_DOWN)) {
                ShipCol.setLocation((int) ShipCol.getMinX(),(int) ShipCol.getMinY()+5); 
            }
            //Ship shoot
            if (keys.contains(KeyEvent.VK_SPACE)) {
                if (Coldown<0) {
                    Coldown = 15;
                    shoot(PowerState);
                }
            }
        }
        
        //Particles
        if (Particles.size() != 0) {
            for (int i = 1; i != Particles.size();i++) {
                g.setColor(Colors.get(i));
                g.fill(Particles.get(i));
                if (Colors.get(i).getAlpha()==0) {
                    Colors.remove(i);
                    Particles.remove(i);
                    i--;
                } else {
                    Colors.set(i,new Color(Colors.get(i).getRed(),Colors.get(i).getGreen(),Colors.get(i).getBlue(),Colors.get(i).getAlpha()-2));
                }
            }   
        }
        if (ShipCol.getY()<-30 && Opponents.size() == 0){
            Main.INSTANCE.switchGame(0);
        }
        ScreenColison();
        //g.setColor(Color.red);
        //g.draw(ShipCol);
        g.drawImage(Ship,ShipCol.x, ShipCol.y, null);
        if (Dead) {
            g.drawImage(dead, 50 , 250, null);
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
                Main.INSTANCE.switchGame(0);
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE || ShipCol.getCenterY()<-30) {
                Main.INSTANCE.resetData();
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
        for (int i = 0;i < Projektiles.size();i++) {
            double Y = Projektiles.get(i)[1];
            double VelY = Projektiles.get(i)[3];
            double X = Projektiles.get(i)[0];
            double VelX = Projektiles.get(i)[2];

            Projektiles.set(i, new double[] {X+VelX, Y + VelY, VelX,VelY, Projektiles.get(i)[4]});
        }
    }
    
    void GenParticles(int x ,int y, int rolls) {
        for (int i = new Random().nextInt(4)+rolls;i>0;i--) {
            Particles.add(new Rectangle(x+new Random().nextInt(10)-5,y-new Random().nextInt(12)-5,5,5));
            Color temp = new Color(Color.HSBtoRGB(14, new Random().nextInt(120) , new Random().nextInt(900)));
            Colors.add(new Color(temp.getRed(),temp.getGreen(),temp.getBlue(),90));
        }
    }
    void print(Object obj) {
        System.out.println(obj);
    }
    void shoot(int Tier) {
        switch (Tier) {
                        
            case 1: Projektiles.add(new double[] {ShipCol.getCenterX()-4,ShipCol.getMinY()-25,0,-8,1.0});
                    break;
            case 2: Projektiles.add(new double[] {ShipCol.getMinX(),ShipCol.getMinY()-25,0,-6,1.0});
                    Projektiles.add(new double[] {ShipCol.getMaxX()-8,ShipCol.getMinY()-25,0,-6,1.0});
                    break;
            case 3: Projektiles.add(new double[] {ShipCol.getCenterX()-4,ShipCol.getMinY()-25,0,-8,1.0});
                    Projektiles.add(new double[] {ShipCol.getMinX(),ShipCol.getMinY()-15,-0.25,-6.0,1.0});
                    Projektiles.add(new double[] {ShipCol.getMaxX()-8,ShipCol.getMinY()-15,0.25,-6.0,1.0});
                    break;
            case 4: Projektiles.add(new double[] {ShipCol.getCenterX()-4,ShipCol.getMinY()-25,0,-8,1.0});

                    Projektiles.add(new double[] {ShipCol.getMinX()+4,ShipCol.getMinY()-25,-0.15,-7.0,1.0});
                    Projektiles.add(new double[] {ShipCol.getMaxX()-8,ShipCol.getMinY()-25,0.15,-7.0,1.0});

                    Projektiles.add(new double[] {ShipCol.getMinX(),ShipCol.getMinY()-25,-0.5,-6.0,1.0});
                    Projektiles.add(new double[] {ShipCol.getMaxX()-4,ShipCol.getMinY()-25,0.5,-6.0,1.0});
                    break;
            case 5: Projektiles.add(new double[] {ShipCol.getCenterX()-4,ShipCol.getMinY()-25,0,-8.0,1.0});

                    Projektiles.add(new double[] {ShipCol.getMinX()+4,ShipCol.getMinY()-25,-0,15,-7.0,1.0});
                    Projektiles.add(new double[] {ShipCol.getMaxX()-8,ShipCol.getMinY()-25,-0,15,-7.0,1.0});

                    Projektiles.add(new double[] {ShipCol.getMinX(),ShipCol.getMinY()-15,-0.5,-6.0,1.0});
                    Projektiles.add(new double[] {ShipCol.getMaxX()-4,ShipCol.getMinY()-15,0.5,-6.0,1.0});

                    Projektiles.add(new double[] {ShipCol.getMinX()-4,ShipCol.getMinY()-5,0.65,-6.0,1.0});
                    Projektiles.add(new double[] {ShipCol.getMaxX(),ShipCol.getMinY()-5,-0.65,-6.0,1.0});
                    break;
            case 6: Projektiles.add(new double[] {ShipCol.getCenterX()-2.5,ShipCol.getMinY()-25,0,-12,1.0});
                    Coldown = 2;
                    break;
        }
    }
    void ScreenColison() {
        if (ShipCol.x<0) {
            ShipCol.setLocation(0, ShipCol.y);
        }
        if (ShipCol.getMaxX()>380) {
            ShipCol.setLocation(380-ShipCol.width, ShipCol.y);
        }
        if (ShipCol.getMinY()>570-ShipCol.height) {
            ShipCol.setLocation(ShipCol.x,570-ShipCol.height);
        }
    }
    void Explode(int x,int y) {

    }


    @Override
    public void mouseClicked(MouseEvent e) {
         
        
    }


    @Override
    public void mouseMoved(MouseEvent e) {
         
        
    }
} 
