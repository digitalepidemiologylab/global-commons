/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package map;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Vector;

/**
 * An X by Y grid without wrapping
 * @author toddbodnar
 */
public class boundedGrid implements map{

    public boundedGrid(int x,int y)
    {
        elements = new unit[x][y];
        this.X = x;
        this.Y = y;
    }

    int X, Y;

    public Vector<unit> getNear(Point center, float distance) {
        
        //If the distance is significantly large, we know that everyone is 
        //within the range without having to calculate each agent individually
        //TODO: make sure this distance is large enough
        if(distance * distance * 2 > elements.length * elements[0].length)
            return getAllUnits();
        
        
        Vector<unit> inRange = new Vector<unit>();
        float d2 = distance * distance;

        //first build a bounding box
        int bx = (int) (center.x - distance - 1);
        int by = (int) (center.y - distance - 1);

        int ex = (int) (center.x + distance + 2);
        int ey = (int) (center.y + distance + 2);

        if(bx<0) bx = 0;
        if(by<0) by = 0;
        if(ex>elements.length) ex = elements.length;
        if(ey>elements[0].length) ey = elements[0].length;

        //for every element within the box
        for(int x=bx;x<ex;x++)
            for(int y=by;y<ey;y++)
                if(Math.pow(center.x-x, 2)+ Math.pow(center.y-y, 2) <=d2)
                    inRange.add(elements[x][y]);

        return inRange;
    }

    public void set(unit u) {
        elements[u.getLocation().x][u.getLocation().y] = u;
    }
    
    public void visualize(Graphics g)
    {
        visualize(g,400,400);
    }

    public void visualize(Graphics g, int x, int y) {
        
        try
        {
            for(int mx=0;mx<elements.length;mx++)
                for(int my=0;my<elements[0].length;my++)
                {
                    g.translate(mx*x/elements.length, my*y/elements[0].length);
                    elements[mx][my].draw(g, x/elements.length, y/elements[0].length);
                    g.translate(-mx*x/elements.length, -my*y/elements[0].length);
                }
        }
        catch(UnsupportedOperationException e)
        {
        ((Graphics2D)g).scale(x/(elements.length*10.0), y/(elements[0].length*10.0));
        for(int mx=0;mx<elements.length;mx++)
            for(int my=0;my<elements[0].length;my++)
            {
                g.translate(mx*10, my*10);
                elements[mx][my].draw(g);
                g.translate(-mx*10, -my*10);
            }

        ((Graphics2D)g).scale(1/(x/(elements.length*10.0)),1/( y/(elements[0].length*10.0)));
        }
    }

    public Vector<unit> getAllUnits() {
        Vector<unit> units = new Vector<unit>();
        for(int x=0;x<elements.length;x++)
            for(int y=0;y<elements[0].length;y++)
                units.add(elements[x][y]);

        return units;
    }

    //May be useful in the future?
    public map copy(map m) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void fillAll(unit u) {
        for(int x=0;x<elements.length;x++)
            for(int y=0;y<elements[0].length;y++)
            {
                unit add = u.copy();
                add.setLocation(new Point(x,y));
                elements[x][y] = add;
            }
    }

    private unit elements[][];

    @Override
    public float distanceBetween(unit one, unit two) {
        return (float) one.getLocation().distance(two.getLocation());
    }

}
