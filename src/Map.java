import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Map extends JFrame {

    ArrayList<Coordinate> points;
    ImageIcon icon = new ImageIcon("./src/Resources/Ireland_(MODIS).jpg");

    public Map(ArrayList<Coordinate> points){
        this.points = points;
        setResizable(false);
        setBounds(0,0,800,800);
        repaint();
        setVisible(true);
    }
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        Stroke defaultStroke = ((Graphics2D) g).getStroke();
        Stroke dotStroke = new BasicStroke(4.0f);
        g.drawImage(icon.getImage(),0,0,800,800,null);
        Coordinate prevNode = null;
        for(Coordinate x: points){
            if(prevNode != null){
                g2.setColor(Color.black);
                g2.setStroke(defaultStroke);
                g2.drawLine(convertLon(prevNode.lon),convertLat(prevNode.lat),convertLon(x.lon),convertLat(x.lat));
            }
            g2.setColor(Color.red);
            g2.setStroke(dotStroke);
            //g2.drawLine(convertLon(x.lon),convertLat(x.lat),convertLon(x.lon),convertLat(x.lat));
            g2.drawOval(convertLon(x.lon)-2,convertLat(x.lat)-2,4,4);
            prevNode = x;
        }
    }
    //Roughly maps co-ordinate points onto image of Ireland
    private int convertLat(double lat){
        return (int)Math.round((56-lat)*150);
    }
    private int convertLon(double lon){
        return (int)Math.round((lon+11+1.4)*100);
    }
}

