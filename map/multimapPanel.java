/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package map;

import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * A JPanel that draws a 2d array of maps
 * @author tjb5215
 */
public class multimapPanel extends JPanel{
    public multimapPanel(map m[][])
    {
        myMap = m;
    }
    @Override
    public void paintComponent(Graphics g)
    {
        for(int y = 0; y < myMap.length; y++)
            for(int x=0; x<myMap[0].length;x++)
            {
                g.translate(x*425,y*425);
                myMap[y][x].visualize(g);
                g.translate(-x*425, -y*425);
            }
    }


    map myMap[][];

}
