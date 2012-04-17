/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package map;

import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * A JPanel that draws a map
 * @author toddbodnar
 */
public class mapPanel extends JPanel{
    public mapPanel(map m)
    {
        myMap = m;
    }
    @Override
    public void paintComponent(Graphics g)
    {
        myMap.visualize(g,this.getWidth(),this.getHeight());
    }

    
    map myMap;
}
