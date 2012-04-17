/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.util.Vector;
import javax.swing.JFrame;
import map.*;

/**
 *
 * @author toddbodnar
 */
public class Main {

    public static void main(String args[]) throws InterruptedException, IOException {

        //default distances, can be replaced in the args
        float play = 1000.0f, peer = 1000.0f, pool = 1000.0f, learn = 1000.0f;
        
        //extra settings: verbose outputs each generation instead of just at the end
        //graphics displays a window
        //disable allows the user to disable individual strategies
        boolean verbose = false, graphics = false, emulate = false, disable[];

        disable = new boolean[5];
        for(int ct=0;ct<5;ct++)
            disable[ct]=false;
        
      
        graph g = new graph();
        

        int width = (int)Math.sqrt(settings.population);
        boolean secondOrder = false;

        try {
            if (args.length > 0) {
                play = Float.parseFloat(args[0]);
                peer = Float.parseFloat(args[1]);
                pool = Float.parseFloat(args[2]);
                learn = Float.parseFloat(args[3]);
                width = Integer.parseInt(args[4]);
                secondOrder = Integer.parseInt(args[5]) == 1;
                String flags = "";
                
                //The rest of the args do not require a certain order
                for (int ct = 6; ct < args.length; ct++) {
                    flags += args[ct];
                }
                
                if (flags.contains("v")) 
                    verbose = true;
                

                if (flags.contains("n")) 
                    disable[0] = true;
                

                if (flags.contains("d")) 
                    disable[1] = true;
                

                if (flags.contains("c")) 
                    disable[2] = true;
                

                if (flags.contains("P")) 
                    disable[3] = true;
                

                if (flags.contains("p")) 
                    disable[4] = true;
                

                if (flags.contains("g")) 
                    graphics = true;
                
                if (flags.contains("e"))
                    emulate = true;
                

            }
        } catch (Exception e) {
            System.out.println("Error could not format text.");
            System.out.println("Requires either 0, 1, 6 or 7 args");
            System.out.println("\nZero args: \tsets play = 1, peer = 1, pool = 1, learn = 1,\n\t\t width = 10, no second round coop");
            System.out.println("1 arg:\tPrints this help info");
            System.out.println("6 args:\n\nArg Number\tDescription");
            System.out.println("0\t\tPlay distance");
            System.out.println("1\t\tPeer Punish distance");
            System.out.println("2\t\tPool Punish distance");
            System.out.println("3\t\tLearning Distance");
            System.out.println("4\t\tWidth/height of the playing field");
            System.out.println("5\t\tSet to 1 for second order punishment, otherwise only 1st order");
            System.out.println("6\t\tFlags (See below)");
            System.out.println("\n\nFlags:");
            System.out.println("n\tDisable non-participants");
            System.out.println("c\tDisable simple cooperators");
            System.out.println("p\tDisable pool punishers");
            System.out.println("P\tDisable peer punishers");
            System.out.println("d\tDisable defectors");
            System.out.println("v\tVerbose mode");
            System.out.println("g\tEnable graphics");
            System.out.println("e\tRun sims with a given coverage cooeficient");
            System.exit(-1);
        }


        map world = new gridTorus(width, width);
        
        
        int gamesPerGen = width * width , playersPerMatch = 5;
        JFrame f = null;

        if (verbose) {
            System.out.println(width * width + " players");
            System.out.println("Play Distance = " + play);
            System.out.println("Pool Punish Distance = " + pool);
            System.out.println("Peer Punish Distance = " + peer);
            System.out.println("Learn Distance = " + learn);
            System.out.println("Second Order? " + secondOrder);
            System.out.println("\nDisable...");
            System.out.println("\tNon-Players? " + disable[0]);
            System.out.println("\tDefectors? " + disable[1]);
            System.out.println("\tCooperators? " + disable[2]);
            System.out.println("\tPeer Punishers? " + disable[3]);
            System.out.println("\tPool Punishers? " + disable[4]);
        }

        if (disable[0] && disable[1] && disable[2] && disable[3] && disable[4]) {
            System.out.println("Error! Cannot Disable All Strategies");
            System.exit(-5);
        }

        
        double emuDistance = pool;
        if(emulate)
        {
            pool = 99999;
            play = 99999;
            peer = 99999;
            
            pggPlayer.B *= emuDistance;
            pggPlayer.Beta *= emuDistance;
            pggPlayer.gamma *= emuDistance;
            
            
        }
        
        JFrame fr = new JFrame();
        fr.add(g);
        if(graphics)
        fr.setVisible(true);


        world.fillAll(new pggPlayer(null, world, pggPlayer.DEF, play, peer, pool, learn, secondOrder));

        if (graphics) {

            if (verbose) {
                System.out.println("Giving a view window");
            }
            f = new JFrame();

            f.add(new mapPanel(world));
            f.setVisible(true);
            f.setSize(400, 400);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



            Thread.sleep(1000);
        }

        //either keep a running total
        //or the current time step's total (if verbose is on)
        double no = 0, def = 0, peerc = 0, poolc = 0, coop = 0;
        long timeStep = 0;
        for (timeStep = 0; timeStep < settings.ROUNDS; timeStep++) {

            Vector<unit> v = world.getAllUnits();

           // ((pggPlayer)v.firstElement()).strategy=pggPlayer.DEF;

            //play games
            for (int ct = 0; ct < gamesPerGen; ct++) {
                pggPlayer p = (pggPlayer) v.get((int) (Math.random() * v.size()));
                p.playOnMyCenter(playersPerMatch);
            }

            //everyone punishes at the end of the round
            for (unit u : v) {
                ((pggPlayer) u).punish();
            }


            Vector<pggPlayer> nextGeneration = new Vector<pggPlayer>();
            int thisChange = 0;
            for (int ct = 0; ct < v.size(); ct++) {
                pggPlayer parent = (pggPlayer) v.get(ct);
                pggPlayer child = (pggPlayer) parent.copy();
                int bestStrat = parent.getBestOfTwoStrategy();
                if (bestStrat != parent.getStrategy()) {
                    thisChange++;
                }

                child.setStrategy(bestStrat);
                child.score = 0;
                child.playDistance = parent.playDistance;
                if (settings.random.nextDouble() < settings.mutation) {
                    int newstrat;
                    do {
                        newstrat = settings.random.nextInt(5);
                    } while (disable[newstrat]);
                    child.setStrategy(newstrat);
                }
                nextGeneration.add(child);
            }
            
            if (graphics) 
            {
                f.repaint(0, 0, 9999, 9999);
               // Thread.sleep(1000);
            }


            //replace the current players with the next generation of players
            for (int ct = 0; ct < nextGeneration.size(); ct++) {
                world.set(nextGeneration.get(ct));

            }

            
            


            v = world.getAllUnits();
            if (verbose) {
                no = def = coop = peerc = poolc = 0;
            }
            if (timeStep >= settings.SKIP) {
                for (int ct = 0; ct < v.size(); ct++) {
                    pggPlayer p = (pggPlayer) v.get(ct);
                    if (p.isNoPlayer()) {
                        no++;
                    }

                    if (p.isDefector()) {
                        def++;
                    }

                    if (p.isSimpleCoop()) {
                        coop++;
                    }

                    if (p.isPeerPunisher()) {
                        peerc++;
                    }

                    if (p.isPoolPunisher()) {
                        poolc++;
                    }

                }
                if (verbose) {
                    g.set((int)timeStep, (int)no, (int)coop, (int)def, (int)peerc, (int)poolc);
                    fr.repaint(1000, 0, 0, 1000, 1000);
                    System.out.println(timeStep + "," + play + "," + peer + "," + pool + "," + learn + "," + width * width + "," + secondOrder + "," + 0 + "," + 0 + "," + no + "," + def + "," + coop + "," + peerc + "," + poolc);
                }
               
            }
        }

        if (verbose) {
            System.out.println("Done!");
        } else {
            if(emulate)
                System.out.println(emuDistance+","+secondOrder +","+no + "," + def + "," + coop + "," + peerc + "," + poolc);
            else
                System.out.println(play + "," + peer + "," + pool + "," + learn + "," + width * width + "," + secondOrder + "," + 0 + "," + 0 + "," + no + "," + def + "," + coop + "," + peerc + "," + poolc);
        }
        
    }
}
