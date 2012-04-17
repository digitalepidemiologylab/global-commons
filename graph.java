/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * A line graph of strategies over time
 * @author toddbodnar
 */
public class graph extends JPanel{
    public graph()
    {
        vals = new int[1000][5];
        totals = new long[5];
        for(int i=0;i<5;i++)
            totals[i]=0;
        
    }
    public void set(int t, int no, int co, int de, int pe, int po)
    {
        vals[(int)(t)][0] = no;
        vals[(int)(t)][1] = co;
        vals[(int)(t)][2] = de;
        vals[t][3] = pe;
        vals[t][4] = po;
        
        totals[0] += no;
        totals[1] += co;
        totals[2] += de;
    }
    public void paintComponent(Graphics g)
    {
        g.setColor(Color.white);
        g.fillRect(0, 0, 1000, 100);
        for(int t=1;t<1000;t++)
            for(int ct=0;ct<5;ct++)
            {
                switch(ct)
                {
                    case 0: g.setColor(Color.yellow); break;
                    case 1: g.setColor(Color.blue); break;
                    case 2: g.setColor(Color.red); break;
                    case 3: g.setColor(Color.green); break;
                    case 4: g.setColor(Color.magenta); break;
                }
                
                g.drawLine(t, 100 - (int)(vals[t][ct]), t-1, 100 - (int)(vals[t-1][ct]));
            }
    }
    
    public String toString()
    {
       
        
        return totals[0]+","+totals[1]+","+totals[2];
    }
    int vals[][];
    long totals[];
}
