/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package map;

import java.awt.Graphics;
import java.awt.Point;

/**
 * Interface for a generic object on a map
 * @author toddbodnar
 */
public interface unit {
    public unit copy();
    public void setLocation(Point p);
    public Point getLocation();
    public void draw(Graphics g);
    public void draw(Graphics g, int width, int height);
    @Override
    public boolean equals(Object o);
}
