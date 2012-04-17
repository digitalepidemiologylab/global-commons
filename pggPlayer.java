/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;
import map.map;
import map.unit;

/**
 * The individual actors
 * @author toddbodnar
 */
public class pggPlayer implements unit{
    public pggPlayer(Point p, map Map, int strat, float playRange, float peerRange, float poolRange, float learnRange, boolean secRound)
    {
        strategy = strat;
        loc = p;
        m = Map;
        score = 0;
        this.learnDistance = learnRange;
        poolPunishDistance = poolRange;
        peerPunishDistance = peerRange;
        playDistance = playRange;
        this.secondOrderPunishment = secRound;
        playFlag = false;
    }

    public unit copy() {
        pggPlayer p = new pggPlayer(loc, m, strategy, playDistance, peerPunishDistance, poolPunishDistance, learnDistance, secondOrderPunishment);
        return p;
    }

    public void setLocation(Point p) {
        loc = p;
    }

    public int getStrategy()
    {
        return strategy;
    }

    public void setStrategy(int s)
    {
        strategy = s;
    }

    public Point getLocation() {
        return loc;
    }

    public String toString()
    {
        String s = strategy + ";" + loc.x + "," + loc.y +";"+ prePunishScore + ";"+ score;
        for(pggPlayer player: toPunishList)
        {
            s+=";" + player.loc.x + "," + player.loc.y;
        }
        return s;
    }

    public void draw(Graphics g) {
        switch(strategy)
        {
            case DEF: g.setColor(Color.red); break;
            case COOP: g.setColor(Color.blue); break;
            case PEER: g.setColor(Color.green); break;
            case POOL: g.setColor(Color.magenta); break;
            case NOPLAY: g.setColor(Color.yellow); break;
            default:g.setColor(Color.black);
        }
        g.fillRect( 0, 0, 10, 10);
        g.setColor(Color.black);
        g.setFont(new Font("Serif", Font.PLAIN, 2));
      //  g.drawString(score/this.games+"", 3, 5);
    }


    public int getBestOfTwoStrategy()
    {
        Vector<unit> players = m.getNear(loc, learnDistance);
        players.remove(this);
        if(players.size()==0)
        {
            System.out.println("weird");
            return strategy;
        }
        pggPlayer one;

        System.err.println(secondOrderPunishment);

        one = (pggPlayer) players.get(settings.random.nextInt( players.size()));
        System.err.println(one);
        System.err.println(one.strategy);

        int ones = one.strategy;

        if(one.games == 0 && this.games == 0)
        {
            if(settings.random.nextBoolean())
                return ones;
            else
                return strategy;
        }

        if(Math.pow(one.score/one.games - this.score/this.games,2)<.001) //if their scores are this close, it may be a rounding issue, assume equal
        {
            if(settings.random.nextBoolean())
                return ones;
            else
                return strategy;
        }

        if(one.games==0)
            return strategy;
        if(this.games ==0)
            return ones;
        if(ones<0)
            System.exit(ones);
        if(ones>5)
            System.exit(ones);

        if((one.score/one.games)>(this.score/this.games))
            return ones;
        else
            return strategy;

    }





    public void punish()
    {
        prePunishScore = score;
        if(!playFlag) return;

       

        Vector<unit> poolRange = new Vector<unit>();
        Vector<unit> peerRange = new Vector<unit>();

        for(pggPlayer p: toPunishList)
        {
            if(m.distanceBetween(p, this)<=peerPunishDistance) 
            {    
                peerRange.add(p); //we'll assume peer and pool punishment range is the same    
                poolRange.add(p);
            }
        }

        if(isDefector())
        {
            Vector<unit> toBePunishedByPeer = peerRange;
            Vector<unit> toBePunishedByPool = poolRange;
            int peerPct=0;
            int poolPct=0;

            
            for (int ct = 0; ct < toBePunishedByPeer.size(); ct++) {
                pggPlayer p = (pggPlayer) toBePunishedByPeer.get(ct);
                
                if (p.isPeerPunisher() && p.playFlag) {
                    peerPct++;
                }
                
            }


            for(int ct=0;ct<toBePunishedByPool.size();ct++)
            {
                pggPlayer p = (pggPlayer)toBePunishedByPool.get(ct);
                if(p.isPoolPunisher()&&p.playFlag)
                    poolPct++;
            }

            score -= poolPct * B;
            score -= peerPct * Beta;
        }
        
        if((isPeerPunisher()||isSimpleCoop())&&secondOrderPunishment)
        {            
            Vector<unit> toBePunishedByPool = poolRange;

            int poolPct=0;

            for(int ct=0;ct<toBePunishedByPool.size();ct++)
            {
                pggPlayer p = (pggPlayer)toBePunishedByPool.get(ct);
                if(p.isPoolPunisher()&&p.playFlag)
                    poolPct++;
            }

            score -= poolPct * B;
        }

       

        if(strategy == PEER)
        {
            Vector<unit> toBePunishedByPeer = peerRange;
            Vector<unit> toBePunishedByPool = poolRange;
            int defect=0;


            for(int ct=0;ct<toBePunishedByPeer.size();ct++)
            {
                pggPlayer p = (pggPlayer)toBePunishedByPeer.get(ct);
                if(p.isDefector()&&p.playFlag) //we assume peer punishers do not punish simple cooperators see paper for explination
                    defect++;
            }


            score -= defect * gamma;
        }

        
    }

    public void playOnMyCenter(int n)
    {
        Vector<unit> possiblePlayers = m.getNear(loc, playDistance);
        Vector<unit> toPlay = new Vector();

        n--;
        toPlay.add(this);
        possiblePlayers.remove(this);
        for(int ct=0;ct<n && possiblePlayers.size()>0;ct++)
        {
            unit randomPlayer = possiblePlayers.get(settings.random.nextInt(possiblePlayers.size()));
            possiblePlayers.remove(randomPlayer);
            toPlay.add(randomPlayer);
        }

        for(unit p: toPlay)
        {
            ((pggPlayer)p).play(toPlay);
        }
    }

    public void play(Vector<unit> toPlayWith)
    {

        
        games++;
        if(isNoPlayer())
        {
            
            score +=sigma;
            return;
        }

        int noPlay = 0, sCoop = 0, def = 0, peerP = 0, poolP = 0;

        for(int ct=0;ct<toPlayWith.size();ct++)
        {
            pggPlayer p = (pggPlayer)toPlayWith.get(ct);

            
                switch (p.strategy) {
                    case DEF:
                        def++;
                        break;
                    case COOP:
                        sCoop++;
                        break;
                    case PEER:
                        peerP++;
                        break;
                    case POOL:
                        poolP++;
                        break;
                    case NOPLAY:
                        noPlay++;
                        break;
                    default:
                        System.out.println("Error: Unknown strategy "+p.strategy);
                        System.exit(-1);
                }
            
        }

        if(def + sCoop + peerP + poolP <=1) //if there are not enough people to play a game with
        {
            score+= sigma;
            return;
        }

        playFlag = true;

        for(unit p: toPlayWith)
        {
            if(p!=this)
                toPunishList.add((pggPlayer)p); //note a player can be on the list more than one time (if multiple games are played together)
        }

        if(plays())
        {
            double pool = sCoop + peerP + poolP;
            if(cooperates())
                pool --;
            
            score += pool*r*c/(sCoop+peerP+poolP+def-1);
        }

        
        
        if(cooperates())
        {
            score -= c;
        }
        
        if(isPoolPunisher())
        {
            score -= G;
        }

       

        

    }


    public boolean isPeerPunisher()
    {
        return strategy == PEER;
    }

    public boolean isPoolPunisher()
    {
        return strategy == POOL;
    }

    public boolean isSimpleCoop()
    {
        return strategy == COOP;
    }

    public boolean isDefector()
    {
        return strategy == DEF;
    }

    public boolean isNoPlayer()
    {
        return strategy == NOPLAY;
    }

    public boolean cooperates()
    {
        return strategy == COOP || strategy == PEER || strategy == POOL;
    }

    public boolean plays()
    {
        return !isNoPlayer();
    }
    
    public void mutate()
    {
        strategy = settings.random.nextInt(5);
    }

    Point loc;
    public int strategy,games=0;
    map m;

    public boolean playFlag = false, secondOrderPunishment=false;
    public final static int NOPLAY = 0, DEF = 1, COOP = 2, PEER = 3, POOL = 4;
    float score=0, prePunishScore,learnDistance = 1, poolPunishDistance=1, peerPunishDistance=1, playDistance = 1;
    public static float N = 5, r = 3, c =1, sigma = 1, gamma = 0.7f, Beta = 0.7f, B = 0.7f, G = 0.7f;
    private Vector<pggPlayer> toPunishList = new Vector();

    @Override
    public void draw(Graphics g, int width, int height) {
        draw(g);
    }
}
