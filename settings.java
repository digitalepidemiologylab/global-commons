/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.security.SecureRandom;
import java.util.Random;

/**
 *
 * @author toddbodnar
 */
public class settings {
    public static int ROUNDS = 1000;
    public static int SKIP = 000000; //how many rounds to skip before recording
    public static double mutation = 0.001;
    public static int population = 100;
    public static Random random = new SecureRandom(); //may be unnecessary
}
