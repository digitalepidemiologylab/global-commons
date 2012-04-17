/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package map;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;

/**
 * Interface for various ways to place units
 * @author toddbodnar
 */
public interface map {

    /**
     * Gets units that are near a specified point
     * @param center the specified point
     * @param distance the distance to search
     * @return an unsorted vector of units
     */
    public Vector<unit> getNear(Point center, float distance);

    /**
     * Adds unit u to the map
     * @param u the unit to add
     */
    public void set(unit u);

    /**
     * Draws the map to a 400x400 image
     * @param g
     */
    public void visualize(Graphics g);
    
    /**
     * Draws the map to a x by y image
     * @param g
     */
    public void visualize(Graphics g, int x, int y);

    /**
     * Returns a vector of all units in the map
     * @return
     */
    public Vector<unit> getAllUnits();

    /**
     * Copies the map
     * @param m
     * @return
     */
    public map copy(map m);

    /**
     * Fills all of the map with a unit
     * @param u
     */
    public void fillAll(unit u);

    /**
     * Determines the distance between two units
     */
    public float distanceBetween(unit one, unit two);
}
