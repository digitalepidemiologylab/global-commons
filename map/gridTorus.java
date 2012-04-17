/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package map;

import java.awt.Point;
import java.util.Vector;

/**
 * An X by Y grid with wrapping
 * @author toddbodnar
 */
public class gridTorus extends boundedGrid {
    public gridTorus(int x,int y)
    {
        super(x,y);
    }
    public Vector<unit> getNear(Point center, float distance)
    {
        Vector<unit> all = new Vector<unit>();
        Vector<unit> part = new Vector<unit>();

        if(distance>super.X && distance > super.Y)
            return super.getAllUnits();

        for(int xoff = -1; xoff<=1; xoff++)
            for(int yoff = -1; yoff<=1; yoff++)
            {
                Point place = new Point(center.x+super.X*xoff, center.y+super.Y*yoff);
                part = super.getNear(place, distance);

                for(unit u: part)
                {
                    if(!all.contains(u))
                        all.add(u);
                }
            }

        return all;
    }
    public float distanceBetween(unit one, unit two)
    {
        float minDist = Float.POSITIVE_INFINITY;
        for(int xoff = -1; xoff<=1; xoff++)
            for(int yoff = -1; yoff<=1; yoff++)
            {
                Point place = new Point(one.getLocation().x+super.X*xoff, one.getLocation().y+super.Y*yoff);
                if(place.distance(two.getLocation())<minDist)
                    minDist = (float) place.distance(two.getLocation());
            }

        return minDist;
    }
}
