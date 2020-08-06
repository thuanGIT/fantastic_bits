import java.util.*;
import java.io.*;
import java.math.*;
import java.util.ArrayList;

/**
 * Grab Snaffles and try to throw them through the opponent's goal!
 * Move towards a Snaffle and use your team id to determine where you need to throw it.
 **/
class Player {
    
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int myTeamId = in.nextInt(); // if 0 you need to score on the right of the map, if 1 you need to score on the left
        
        int[][] target = new int[][] {{(myTeamId == 0)? 16000:0, 2000},  //Upper pole
                                      {(myTeamId == 0)? 16000:0, 6000}}; // Lower pole

        int goalMidY = 3750; // middle of the goal

        int gauges = 0;
        // game loop
        while (true) {
            ArrayList<Wizard> wizards = new ArrayList<Wizard>();
            ArrayList<Snaffle> snaffles = new ArrayList<Snaffle>();
            ArrayList<Wizard> wizardsOpp = new ArrayList<Wizard>();
            ArrayList<Bludgers> bludgers = new ArrayList<Bludgers>();
            
            //Game information (not necessary)
            int myScore = in.nextInt();
            int myMagic = in.nextInt();
            int opponentScore = in.nextInt();
            int opponentMagic = in.nextInt();

            int entities = in.nextInt(); // number of entities still in game
            for (int i = 0; i < entities; i++) {
                int entityId = in.nextInt(); // entity identifier
                String entityType = in.next(); // "WIZARD", "OPPONENT_WIZARD" or "SNAFFLE" (or "BLUDGER" after first league)
                int x = in.nextInt(); // position
                int y = in.nextInt(); // position
                int vx = in.nextInt(); // velocity
                int vy = in.nextInt(); // velocity
                int state = in.nextInt(); // 1 if the wizard is holding a Snaffle, 0 otherwise

                if (entityType.equalsIgnoreCase("WIZARD")) {
                    wizards.add(new Wizard(entityId,x,y,vx,vy,state));
                } else if (entityType.equalsIgnoreCase("SNAFFLE")) {
                    snaffles.add(new Snaffle(entityId,x,y,vx,vy,state));
                } else if (entityType.equalsIgnoreCase("OPPONENT_WIZARD")) {
                    wizardsOpp.add(new Wizard(entityId,x,y,vx,vy,state));
                } else if (entityType.equalsIgnoreCase("BLUDGER")) {
                    bludgers.add(new Bludgers(entityId,x,y,vx,vy,state));

                }
            }


            // Case 1: the one the wizard has the ball
            for (int i = 0; i < wizards.size(); i++) { 
                if (wizards.get(i).state == 1) {
                    if (wizards.get(i).x <= 16001/2) {
                        wizards.get(i).throwTo(target[0][0], goalMidY, 500);
                    }
                    else {
                        // If the wizard is higher than the lower pole and lower than the high pole
                        // Higher pole: target[0][0], target[0][1]
                        // Lower pole: target[1][0], target[1][1]

                        // If the wizard is higher than the lower pole and lower than the high pole
                        if (wizards.get(i).y < target[1][1] && wizards.get(i).y > target[0][1])
                            wizards.get(i).throwTo(target[0][0], wizards.get(i).y, 500);
                        else if (wizards.get(i).y >= target[1][1]) // If the wizard is below the low pole
                            wizards.get(i).throwTo(target[0][0], target[1][1] - 1750, 500);
                        else if (wizards.get(i).y <= target[0][1]) // If the wizard is above the high pole
                            wizards.get(i).throwTo(target[0][0], target[0][1] + 1750, 500);
                        else 
                            wizards.get(i).throwTo(target[0][0], goalMidY, 500);
                    }

                    
                } else {
                    wizards.get(i).action(snaffles, target, gauges, myTeamId, wizardsOpp, wizards.get(1));
                }
            }
            //Keep track on the gauges
            gauges++;     
        }

    }




    
}

abstract class Entity {
    public int ID,x,y,state,vx,vy;
    public Entity(int ID, int x, int y, int vx, int vy, int state) {
        this.ID = ID;
        this.x = x;
        this.y = y;
        this.state = state;
        this.vx = vx;
        this.vy = vy;
    }
}
//Snaffle class
class Snaffle extends Entity {
    public Snaffle(int ID, int x, int y, int vx, int vy, int state) {
        super(ID,x,y,vx,vy,state);
    }

    //Predict the movement of the snaffle
    public int[] predictedDes() {
        // The new position is calculated by adding (x,y) and speed vector (vx,vy) respectively
        int[] des = new int[]{vx + this.x, vy + this.y};
        return des;
    }

}
//Bludgers
class Bludgers extends Entity {
    public Bludgers (int ID, int x, int y,int vx, int vy, int state) {
      super(ID,x,y,vx,vy,state);
    }

    public int[] predictedDes() {
        // The new position is calculated by adding (x,y) and speed vector (vx,vy) respectively
        int[] des = new int[]{vx + this.x, vy + this.y};
        return des;
    }
}
// Wizard class
class Wizard extends Entity {
    
    public Wizard(int ID, int x, int y,int vx, int vy, int state) {
        super(ID,x,y,vx,vy, state);
    }
    public void throwTo(int x, int y, int pow) {System.out.println("THROW " + x + " " + y + " " + pow);}
    public void moveTo(int x, int y, int thrust) {System.out.println("MOVE " + x + " " + y + " " + thrust);}
    
    
    public boolean accio(Snaffle snaffle, int gauges){
    	if (gauges >= 15 && snaffle != null){
            System.out.println("ACCIO " + snaffle.ID);
            return true;
        }
        return false;
    }
    public boolean obliviate(int ID){
        System.out.println("OBLIVIATE " + ID);
        return true;
    }
    //Freeze an opponent wizard
    public boolean petrificus(ArrayList<Wizard> wizardsOpp, ArrayList<Wizard> wizards, int gauges){
    	int enWizInd = -1;
    	for (int i = 0; i < wizards.size(); i++){
    		boolean found = false; // bludgers are checkers
    		for (int j = 0; j < wizardsOpp.size(); j++){
    	    	int width = wizards.get(i).x - wizardsOpp.get(j).x;
    	    	int length = Math.abs(wizards.get(i).y - wizards.get(i).y);
    	        double disToClosWiz = Math.sqrt(width*width + length*length);
    	        if (disToClosWiz < 1000){
    	        	enWizInd = j;
    	        	found = true;
    	        	break;
    	        }
            }
            
    		if (found){break;}
        }

    	if (gauges >= 10 && enWizInd != -1){
            System.out.println("PETRIFICUS " + wizardsOpp.get(enWizInd));
            return true;
        }
        return false;
    }


    public Snaffle closestSnap(ArrayList<Snaffle> snaffles, Wizard teamMate) {

        boolean found = false;
        int minIndex = -1;
        int avoid = -1;
        
        do {
            double min = Integer.MAX_VALUE;
            for (int i = 0; i < snaffles.size(); i++) {
                if (i != minIndex) {
                    double d = Math.sqrt(Math.pow(snaffles.get(i).x - this.x,2) + Math.pow(snaffles.get(i).y - this.y,2));          
                    if (d < min) {
                        min = d;
                        minIndex = i;
                    }
                }
               
            }

            if (minIndex == -1) {
                double distanceToMate = Math.sqrt(Math.pow(snaffles.get(minIndex).x - teamMate.x,2) + Math.pow(snaffles.get(minIndex).y - teamMate.y,2));
                if (min < distanceToMate) found = true;
                else avoid = minIndex;

            } else found = true;
            
            


        } while (!found);
        

        return snaffles.get(minIndex);
    }


    public boolean felipendo (Snaffle snaffle, int gauges, int[][] goal) {
        if (snaffle == null) return false;
        
        //Determine whether the wizard, snaffle and the goal are aligned
        double slope = (snaffle.y - this.y)/(snaffle.x - this.x);
        double intersect_y = slope*(goal[0][0] - this.x) + this.y;
        
        if (gauges >= 20 && (intersect_y < goal[1][1] - 700 && intersect_y > goal[0][1] + 700)) {
            System.out.println("FLIPENDO " + snaffle.ID);
            return true;
        }
        return false;
    }

    public void action(ArrayList<Snaffle> snaffles, int[][] target, int gauges, int myTeamId, ArrayList<Wizard> wizardsOpp, Wizard teamMate) {
        // Closest snaffle
        Snaffle closestSnap = closestSnap(snaffles, teamMate);
        //So that no wizard target the same target but target the last snaffle
        int[] des = closestSnap.predictedDes();

        if (snaffles.size() > 1)
            snaffles.remove(closestSnap);
        
        // Snaffle that is too close to the my goal
        int tooClose = -1;
        for (int k = 0; k < snaffles.size(); k++) {
            if (snaffles.get(k).x < 2000) {
                tooClose = k;
                break;
            }
        }

        Snaffle needToSaveSnap = null;
        if (tooClose != -1)
                needToSaveSnap = snaffles.get(tooClose);

        double distanceToMyWiz = Math.sqrt(Math.pow(this.x - closestSnap.x,2) + Math.pow(this.y - closestSnap.y,2));
        double distanceToOppWiz_0 = Math.sqrt(Math.pow(wizardsOpp.get(0).x - closestSnap.x,2) + Math.pow(wizardsOpp.get(0).y - closestSnap.y,2));
        double distanceToOppWiz_1 = Math.sqrt(Math.pow(wizardsOpp.get(1).x - closestSnap.x,2) + Math.pow(wizardsOpp.get(1).y - closestSnap.y,2));

        // Check if the opp wizard is closer to the snaffle
        boolean snaffleCloseToOpp = distanceToMyWiz > distanceToOppWiz_0 || distanceToMyWiz > distanceToOppWiz_1;

        // Check if the ball is behind
        boolean behind = (myTeamId == 0 && this.x < closestSnap.x) || (myTeamId == 1 && this.x > closestSnap.x);

        if (behind) {
            // Shoot the ball towards goal
            if (!felipendo(closestSnap, gauges, target)) 
                moveTo(des[0], des[1], 150);
        } else {
            // Accio the ball towards my wizards
            if (!(snaffleCloseToOpp && behind && accio(needToSaveSnap, gauges))) 
                moveTo(des[0], des[1], 150); // Move towards the closest snaffle
        }

     
    }

}


    

  
    

