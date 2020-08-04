import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Link to Problem Description:          https://www.codingame.com/multiplayer/bot-programming/fantastic-bits
 
   Scheme: The algorithm first determines each of my wizards' distance to very ball. The wizard with the shortest distance will
           target that ball which is closest to it. The other wizard will also go after the ball closest to it, but it must be 
           a different ball from the one taken by the priority wizard. If there is only one ball remainng in play, then both wizards will
           target it. As soon as a wizard grabs a ball it will throw it at the center of the oppenent's goal. The angle of the throw
           is adjusted for the initial volocity of wizard. 
           
           Use of spells: I prioritize offense over defense. This means that the algorithm first determines whether there is an 
           opportunity to score using the topedo spell that will fire a shot at the ball pushing it towards the opponent's goal. 
           If the angle and distance is aligned and there is enough points to fire the topedo, the appropriate wizard will do so.
           If no such opportunity exists, then it will calculate whether an opponent wizard is about to score a goal on me.
           Next is the defensive spell. If the algorithm determines that an opponent wizard is about to score a goal on me then 
           if possible one of my wizards will use the pulling spell to pull the ball away from my goal towards me
 * 
 **/
class Player1 {
	int gameTurn = 0;
	public static void main(String args[]) {
    	Player1 game = new Player1();
        Scanner in = new Scanner(System.in);
        int myTeamId = in.nextInt(); // if 0 you need to score on the right of the map, if 1 you need to score on the left
        final int goalX;
        final int goalUpperX, goalUpperY, goalLowerX, goalLowerY;
        double angleFromUpGoal_0, angleFromLoGoal_0, angleFromUpGoal_1, angleFromLoGoal_1;
        if (myTeamId == 0){
        	goalX = 16000; goalUpperX = 16000; goalLowerX = 16000; goalUpperY = 2200; goalLowerY = 5300;
        }
        else {
        	goalX = 0; goalUpperX = 0; goalLowerX = 0; goalUpperY = 2200; goalLowerY = 5300;     	
        }
    	int leadInd = 0;
    	int secInd = 1;
        int fTargetedSnaInd = -1; // Index of braffle closest to above Wizard
        int sTargetedSnaInd = -1;
        // game loop
        while (true) {
            int myScore = in.nextInt();
            int myMagic = in.nextInt();
            int opponentScore = in.nextInt();
            int opponentMagic = in.nextInt();
            int entities = in.nextInt(); // number of entities still in game
            int[] entityId = new int[entities];
            String[] entityType = new String[entities];
            int[] x = new int[entities];
            int[] y = new int[entities];
            int[] vx = new int[entities];
            int[] vy = new int[entities];
            int[] state = new int[entities];
            for (int i = 0; i < entities; i++) {
                entityId[i] = in.nextInt(); // entity identifier
                entityType[i] = in.next(); // "WIZARD", "OPPONENT_WIZARD" or "SNAFFLE" (or "BLUDGER" after first league)
                x[i] = in.nextInt(); // position
                y[i] = in.nextInt(); // position
                vx[i] = in.nextInt(); // velocity
                vy[i] = in.nextInt(); // velocity
                state[i] = in.nextInt(); // 1 if the wizard is holding a Snaffle, 0 otherwise
            }
//---------------------------------------------------------------------------------------------------------------
            if (state[0] == 1 && state[1] == 1){
            	int[] Des_0 = new int[2];
            	int[] Des_1 = new int[2];
            	Des_0 = game.adjustingVector(0, x, y, vx, vy, goalX, 3750);
            	Des_1 = game.adjustingVector(1, x, y, vx, vy, goalX, 3750);
            	System.out.println("THROW " + Des_0[0] + " " + Des_0[1] + " 500");
            	System.out.println("THROW " + Des_1[0] + " " + Des_1[1] + " 500");
            } 
//---------------------------------------------------------------------------------------------------------------            
            else if (state[0] == 1 && state[1] == 0){
            	int[] Des_0 = new int[2];
            	Des_0 = game.adjustingVector(0, x, y, vx, vy, goalX, 3750);
            	System.out.println("THROW " + Des_0[0] + " " + Des_0[1] + " 500");
                double minDis = 16001;
                int minSnaInd = 4; // Index of braffle closest to above Wizard
            	for (int j = 4; j < entities-2; j++){
            			int width = x[1] - x[j];
            			int length = y[1] - y[j];
            			double dummy = Math.sqrt(width*width + length*length); 
            			if (dummy < minDis && (x[j] != x[0] || y[j] != y[0])){
            				minDis = dummy;
            				minSnaInd = j;
            			}
            	}
    	        angleFromUpGoal_1 = game.angleFromHiGoal(secInd, myTeamId, x, y, goalUpperX, goalUpperY);
    	        angleFromLoGoal_1 = game.angleFromDoGoal(secInd, myTeamId, x, y, goalLowerX, goalLowerY);
    	        sTargetedSnaInd = game.toFireFelipendo(entities, secInd, myTeamId, x, y, angleFromUpGoal_1, angleFromLoGoal_1, fTargetedSnaInd, entityId);
    	        if (sTargetedSnaInd == -1){
    	        	angleFromLoGoal_1 = game.reboundFelipendo(secInd, myTeamId, x, y, goalLowerX, goalLowerY, entities, goalUpperX, goalUpperY, entityId, fTargetedSnaInd);
    	        }
//    	        if (sTargetedSnaInd == -1){
//    	        	sTargetedSnaInd = game.accio(x, y, vx, vy, myTeamId, 1, -1, entities, entityId); game.reboundFelipendo(secInd, myTeamId, x, y, goalLowerX, goalLowerY, entities, goalUpperX, goalUpperY, entityId, -1);
//    	        }
    	        if (sTargetedSnaInd == -1){
    	        	sTargetedSnaInd = game.destOfSna(x, y, vx, vy, entities, myTeamId, goalUpperY, goalLowerY, goalX, entityId, -1, goalUpperX, goalLowerX);
    	        } 	
    	        if(sTargetedSnaInd == -1){
    		        	System.out.println("MOVE " + x[minSnaInd] + " " + y[minSnaInd] + " 150");	        
    	        }
            }    
//-----------------------------------------------------------------------------------------------
            else if(state[0] == 0 && state[1] == 1){
                double minDis = 16001;
                int minSnaInd = 4; // Index of braffle closest to above Wizard
            	for (int j = 4; j < entities-2; j++){
            			int width = x[0] - x[j];
            			int length = y[0] - y[j];
            			double dummy = Math.sqrt(width*width + length*length); 
            			if (dummy < minDis && (x[j] != x[1] || y[j] != y[1])){
            				minDis = dummy;
            				minSnaInd = j;
            			}
            	}
    	        angleFromUpGoal_0 = game.angleFromHiGoal(leadInd, myTeamId, x, y, goalUpperX, goalUpperY);
    	        angleFromLoGoal_0 = game.angleFromDoGoal(leadInd, myTeamId, x, y, goalLowerX, goalLowerY);
    	        fTargetedSnaInd = game.toFireFelipendo(entities, leadInd, myTeamId, x, y, angleFromUpGoal_0, angleFromLoGoal_0, fTargetedSnaInd, entityId);
    	        if (fTargetedSnaInd == -1){
    	        	fTargetedSnaInd = game.reboundFelipendo(leadInd, myTeamId, x, y, goalLowerX, goalLowerY, entities, goalUpperX, goalUpperY, entityId, -1);
    	        }
//    	        if (fTargetedSnaInd == -1){
//    	        	fTargetedSnaInd = game.accio(x, y, vx, vy, myTeamId, 0, -1, entities, entityId); 
//    	        }
    	        if (fTargetedSnaInd == -1){
    	        	fTargetedSnaInd = game.destOfSna(x, y, vx, vy, entities, myTeamId, goalUpperY, goalLowerY, goalX, entityId, -1, goalUpperX, goalLowerX);
    	        }  
    	        if(fTargetedSnaInd == -1){
		        	System.out.println("MOVE " + x[minSnaInd] + " " + y[minSnaInd] + " 150");	        
    	        }	
            	int[] Des_1 = new int[2];
            	Des_1 = game.adjustingVector(1, x, y, vx, vy, goalX, 3750);
            	System.out.println("THROW " + Des_1[0] + " " + Des_1[1] + " 500");
            }
//-------------------------------------------------------------------------------------------------
            else {
                double minDis = 16001;
                int minWizInd = 0; // Index of the wizard that is closest to any braffle
                int minSnaInd = 4; // Index of braffle closest to above Wizard
                for (int i = 0; i < 2; i++){
                	for (int j = 4; j < entities-2; j++){
                			int width = x[i] - x[j];
                			int length = y[i] - y[j];
                			double dummy = Math.sqrt(width*width + length*length); 
                			if (dummy < minDis){
                				minDis = dummy;
                				minWizInd = i;
                				minSnaInd = j;
                			}
                	}
                }
                
                int nonMinWizInd = 1 - minWizInd; // Index of wizard with a distance to any braffle that is second shortest
                double nonMinDis = 16001;
                int nonMinSnaInd = 4; // index of of the braffle targeted by the above wizard
            	for (int j = 4; j < entities-2; j++){
        			int width = x[nonMinWizInd] - x[j];
        			int length = y[nonMinWizInd] - y[j];
        			double dummy = Math.sqrt(width*width + length*length); 
        			if (dummy < nonMinDis && j != minSnaInd){
        				nonMinDis = dummy;
        				nonMinSnaInd = j;
        			}
            	}
    	        angleFromUpGoal_0 = game.angleFromHiGoal(leadInd, myTeamId, x, y, goalUpperX, goalUpperY);
    	        angleFromLoGoal_0 = game.angleFromDoGoal(leadInd, myTeamId, x, y, goalLowerX, goalLowerY);
    	        fTargetedSnaInd =  game.toFireFelipendo(entities, leadInd, myTeamId, x, y, angleFromUpGoal_0, angleFromLoGoal_0, fTargetedSnaInd, entityId);
    	        if (fTargetedSnaInd == -1){
        	        fTargetedSnaInd = game.reboundFelipendo(leadInd, myTeamId, x, y, goalLowerX, goalLowerY, entities, goalUpperX, goalUpperY, entityId, -1);
    	        }
//    	        if (fTargetedSnaInd == -1){
//    	        	fTargetedSnaInd = game.accio(x, y, vx, vy, myTeamId, 0, -1, entities, entityId); 
//    	        }
    	        if (fTargetedSnaInd == -1){
    	        	fTargetedSnaInd = game.destOfSna(x, y, vx, vy, entities, myTeamId, goalUpperY, goalLowerY, goalX, entityId, -1, goalUpperX, goalLowerX);
    	        }
    	        if (fTargetedSnaInd == -1){
    	        	if (minWizInd == 0){
        	        	System.out.println("MOVE " + x[minSnaInd] + " " + y[minSnaInd] + " 150");    	        		
    	        	}
    	        	else {
    	        		System.out.println("MOVE " + x[nonMinSnaInd] + " " + y[nonMinSnaInd] + " 150");
    	        	}
    	        }
    	        angleFromUpGoal_1 = game.angleFromHiGoal(secInd, myTeamId, x, y, goalUpperX, goalUpperY);
    	        angleFromLoGoal_1 = game.angleFromDoGoal(secInd, myTeamId, x, y, goalLowerX, goalLowerY);
    	        sTargetedSnaInd = game.toFireFelipendo(entities, secInd, myTeamId, x, y, angleFromUpGoal_1, angleFromLoGoal_1, fTargetedSnaInd, entityId); 
    	        if (sTargetedSnaInd == -1){
    	        	sTargetedSnaInd = game.reboundFelipendo(secInd, myTeamId, x, y, goalLowerX, goalLowerY, entities, goalUpperX, goalUpperY, entityId, fTargetedSnaInd);
    	        }
//    	        if (sTargetedSnaInd == -1){
//    	        	sTargetedSnaInd = game.accio(x, y, vx, vy, myTeamId, 1, fTargetedSnaInd, entities, entityId);
//    	        }  
    	        if (sTargetedSnaInd == -1){
    	        	sTargetedSnaInd = game.destOfSna(x, y, vx, vy, entities, myTeamId, goalUpperY, goalLowerY, goalX, entityId, fTargetedSnaInd, goalUpperX, goalLowerX);
    	        }
    	        if(sTargetedSnaInd == -1){
    	        	if (minWizInd == 1){
        	        	System.out.println("MOVE " + x[minSnaInd] + " " + y[minSnaInd] + " 150");    	        		
    	        	}
    	        	else {
    	        		System.out.println("MOVE " + x[nonMinSnaInd] + " " + y[nonMinSnaInd] + " 150");
    	        	}
    	        }
            }
            game.gameTurnTracker();
        }
    }
//-----------------methods-------------------angle of top pole to given wizard or snaffle-------------------------------------------------------------        
        

    double angleFromHiGoal(int wizInd, int myTeamId, int[] x, int[] y, int goalUpperX, int goalUpperY){
        int widthToUpGoal = x[wizInd] - goalUpperX;
        int lengthToUpGoal = Math.abs(y[wizInd] - goalUpperY);
        double distToUpGoal = Math.sqrt(widthToUpGoal*widthToUpGoal + lengthToUpGoal*lengthToUpGoal);
        double angleFromUpGoal = Math.asin(lengthToUpGoal/distToUpGoal);
        if (myTeamId == 1){
	        if (y[wizInd] >= goalUpperY) {angleFromUpGoal = Math.PI - angleFromUpGoal;}
	        else {angleFromUpGoal = Math.PI + angleFromUpGoal;}		        	
        }
        else {
	        if (y[wizInd] < goalUpperY) {angleFromUpGoal = 2*Math.PI - angleFromUpGoal;}
	        else {angleFromUpGoal = 2*Math.PI + angleFromUpGoal;}			        	
        }
        return angleFromUpGoal;
    }
//---------------------------------------angle of low pole to given wizard or snaffle---------------------------------------
    double angleFromDoGoal(int wizInd, int myTeamId, int[] x, int[] y, int goalLowerX, int goalLowerY){
        int widthToLoGoal = x[wizInd] - goalLowerX;
        int lengthToLoGoal = Math.abs(y[wizInd] - goalLowerY);
        double distToLoGoal = Math.sqrt(widthToLoGoal*widthToLoGoal + lengthToLoGoal*lengthToLoGoal);
        double angleFromLoGoal = Math.asin(lengthToLoGoal/distToLoGoal);
        if (myTeamId == 1){
	        if (y[wizInd] > goalLowerY) {angleFromLoGoal = Math.PI - angleFromLoGoal;}
	        else {angleFromLoGoal = Math.PI + angleFromLoGoal;}		        	
        }
        else {
	        if (y[wizInd] >= goalLowerY) {angleFromLoGoal = 2*Math.PI + angleFromLoGoal;}
	        else {angleFromLoGoal = 2*Math.PI - angleFromLoGoal;}			        	
        }
        return angleFromLoGoal;
    }
//---------------------------------------------determine if it is right to fight felipendo at braffle given angle from wizard-----------------------------------
    int toFireFelipendo(int entities, int wizInd, int myTeamId, int[] x, int[] y, double angleFromUpGoal, double angleFromLoGoal, double indToAvoid, int[] entityId){
    	int TargetedSnaInd = -1;
        for (int j = 4; j < entities-2; j++){
        	if ((( myTeamId == 0 && x[wizInd] - x[j] < 0 ) || (myTeamId == 1 && x[wizInd] - x[j] > 0)) && j != indToAvoid
        			&& (x[j] != x[1-wizInd] || y[j] != y[1-wizInd])){
	        	int width = x[wizInd] - x[j];
	        	int length = Math.abs(y[wizInd] - y[j]);
	            double distToClosSna = Math.sqrt(width*width + length*length);
	            double angleFromClosSna = Math.asin(length/distToClosSna);
	            if (x[wizInd] < x[j] && y[wizInd] >= y[j]){
	            	angleFromClosSna = 2*Math.PI + angleFromClosSna;
	            }
	            else if (x[wizInd] < x[j] && y[wizInd] < y[j]){
	            	angleFromClosSna = 2*Math.PI - angleFromClosSna;
	            }		
	            else if (x[wizInd] > x[j] && y[wizInd] >= y[j]){
	            	angleFromClosSna = Math.PI - angleFromClosSna;
	            }
	            else {
	            	angleFromClosSna = Math.PI + angleFromClosSna;
	            }
	            
	            if (((myTeamId == 0 && angleFromClosSna <= angleFromUpGoal && angleFromClosSna >= angleFromLoGoal) || 
	            		(myTeamId == 1 && angleFromClosSna >= angleFromUpGoal && angleFromClosSna <= angleFromLoGoal)) && gameTurn >= 20
	            		&& distToClosSna <= 5000){
	            	TargetedSnaInd = j;	
	            	gameTurn -= 20;
	            	System.out.println("FLIPENDO " + entityId[TargetedSnaInd]);
	            	break;
	            }
        	}
        }
        return TargetedSnaInd;
    }
//------------------calculates the adjusting vector to compensate for initial vector----------------------------------------------------------------------------------------------------

    int[] adjustingVector(int wizInd, int[] x, int[] y, int[] vx, int[] vy, int goalX, int goalY){
    	int aX = goalX - x[wizInd];
    	int aY = goalY - y[wizInd];
    	int bX = aX - vx[wizInd];
    	int bY = aY - vy[wizInd];
    	int desX = x[wizInd] + bX;
    	int desY = y[wizInd] + bY;
    	int[] Destination = new int[2];
    	Destination[0] = desX;
    	Destination[1] = desY;
    	return Destination;	
    }

//-----------------------------------------calculates snaffles that are within the right angle to rebound off bounderies and into the goal
    int reboundFelipendo(int wizInd, int myTeamId, int[] x, int[] y, int goalLowerX, int goalLowerY, int entities, int goalUpperX, int goalUpperY, int[] entityId, int indToAvoid){
    	double minDis = 4000;
    	int targetSna = -1;
    	for (int i = 4; i < entities-2; i++){
	    	if (x[wizInd] != x[i] && i != indToAvoid && (x[i] != x[1-wizInd] || y[i] != y[1-wizInd])){
	    		float rise = y[wizInd] - y[i];
	    		float run = x[wizInd] - x[i];
	    		double length = Math.sqrt(rise*rise + run*run);
	    		float slope = (rise/run);
	    		int xBound = -1;
	    		double angleWallSna = -1;
	    		double angleToHiGoal = -1;
	    		double angleToLoGoal = -1;
	    		if (myTeamId == 0 && x[i] > x[wizInd] && y[i] < y[wizInd]) {
	    			xBound = Math.round(((0-y[wizInd])/slope)+x[wizInd]);
	    			angleWallSna = angleCalculation(xBound, 0, x[i], y[i]);
	    			angleToHiGoal = angleOfPointFromHiGoal(xBound, 0, myTeamId, goalUpperX, goalUpperY);
	    			angleToLoGoal = angleOfPointFromDoGoal(xBound, 0, myTeamId, goalLowerX, goalLowerY);
	    			if (angleWallSna >= angleToLoGoal && angleWallSna <= angleToHiGoal && length < minDis){
	    				minDis = length;
	    				targetSna = i;
	    			}
	    		}
	    		else if (myTeamId == 0 && x[i] > x[wizInd] && y[i] > y[wizInd]) {
	    			xBound = Math.round(((7500-y[wizInd])/slope)+x[wizInd]);
	    			angleWallSna = angleCalculation(xBound, 7500, x[i], y[i]);
	    			angleToHiGoal = angleOfPointFromHiGoal(xBound, 7500, myTeamId, goalUpperX, goalUpperY);
	    			angleToLoGoal = angleOfPointFromDoGoal(xBound, 7500, myTeamId, goalLowerX, goalLowerY);
	    			if (angleWallSna >= angleToLoGoal && angleWallSna <= angleToHiGoal && length < minDis){
	    				minDis = length;
	    				targetSna = i;
	    			}    			
	    		}
	    		else if(myTeamId == 1 && x[i] < x[wizInd] && y[i] > y[wizInd]){
	    			xBound = Math.round(((7500-y[wizInd])/slope)+x[wizInd]);
	    			angleWallSna = angleCalculation(xBound, 7500, x[i], y[i]);
	    			angleToHiGoal = angleOfPointFromHiGoal(xBound, 7500, myTeamId, goalUpperX, goalUpperY);
	    			angleToLoGoal = angleOfPointFromDoGoal(xBound, 7500, myTeamId, goalLowerX, goalLowerY);
	    			if (angleWallSna <= angleToLoGoal && angleWallSna >= angleToHiGoal && length < minDis){
	    				minDis = length;
	    				targetSna = i;
	    			}
	    		}
	    		else if(myTeamId == 1 && x[i] < x[wizInd] && y[i] < y[wizInd]){
	    			xBound = Math.round(((0-y[wizInd])/slope)+x[wizInd]);
	    			angleWallSna = angleCalculation(xBound, 0, x[i], y[i]);
	    			angleToHiGoal = angleOfPointFromHiGoal(xBound, 0, myTeamId, goalUpperX, goalUpperY);
	    			angleToLoGoal = angleOfPointFromDoGoal(xBound, 0, myTeamId, goalLowerX, goalLowerY);
	    			if (angleWallSna <= angleToLoGoal && angleWallSna >= angleToHiGoal && length < minDis){
	    				minDis = length;
	    				targetSna = i;
	    			}    			
	    		}
	    	}
    	}
    	if (gameTurn >= 20 && targetSna != -1){
        	System.out.println("FLIPENDO " + entityId[targetSna]);
        	gameTurn -= 20;
    	}
    	else { targetSna = -1;}
    	return targetSna;
    }
//------------------------------determines the reflected angle of boundary point with snaffle------------------------------------
    
    double angleCalculation(int xRef, int yRef, int xObj, int yObj){
    	int width = xRef - xObj;
    	int length = Math.abs(yRef - yObj);
        double distToClosSna = Math.sqrt(width*width + length*length);
        double angleFromClosSna = Math.asin(length/distToClosSna);
        if (xRef < xObj && yRef >= yObj){
        	angleFromClosSna = Math.PI - angleFromClosSna;
        }
        else if (xRef < xObj && yRef < yObj){
        	angleFromClosSna = Math.PI + angleFromClosSna;
        }		
        else if (xRef > xObj && yRef >= yObj){
        	angleFromClosSna = 2*Math.PI + angleFromClosSna;
        }
        else {
        	angleFromClosSna = 2*Math.PI - angleFromClosSna;
        }
        return angleFromClosSna;
    }
//---------------------------calculates angle of point on boundary with lower pole of goal-------------------------------------
    double angleOfPointFromDoGoal(int x, int y, int myTeamId, int goalLowerX, int goalLowerY){
        int widthToLoGoal = x - goalLowerX;
        int lengthToLoGoal = Math.abs(y - goalLowerY);
        double distToLoGoal = Math.sqrt(widthToLoGoal*widthToLoGoal + lengthToLoGoal*lengthToLoGoal);
        double angleFromLoGoal = Math.asin(lengthToLoGoal/distToLoGoal);
        if (myTeamId == 1){
	        if (y > goalLowerY) {angleFromLoGoal = Math.PI - angleFromLoGoal;}
	        else {angleFromLoGoal = Math.PI + angleFromLoGoal;}		        	
        }
        else {
	        if (y >= goalLowerY) {angleFromLoGoal = 2*Math.PI + angleFromLoGoal;}
	        else {angleFromLoGoal = 2*Math.PI - angleFromLoGoal;}			        	
        }
        return angleFromLoGoal;
    }
//-------------------------calculates angle of point on boundary with upper pole of goal-------------------------------------
    double angleOfPointFromHiGoal(int x, int y, int myTeamId,int goalUpperX, int goalUpperY){
        int widthToUpGoal = x - goalUpperX;
        int lengthToUpGoal = Math.abs(y - goalUpperY);
        double distToUpGoal = Math.sqrt(widthToUpGoal*widthToUpGoal + lengthToUpGoal*lengthToUpGoal);
        double angleFromUpGoal = Math.asin(lengthToUpGoal/distToUpGoal);
        if (myTeamId == 1){
	        if (y >= goalUpperY) {angleFromUpGoal = Math.PI - angleFromUpGoal;}
	        else {angleFromUpGoal = Math.PI + angleFromUpGoal;}		        	
        }
        else {
	        if (y < goalUpperY) {angleFromUpGoal = 2*Math.PI - angleFromUpGoal;}
	        else {angleFromUpGoal = 2*Math.PI + angleFromUpGoal;}			        	
        }
        return angleFromUpGoal;
    }
//--------------------------------calculates destination of snaffle given position and vector--------------------------------------------
    int destOfSna(int [] x, int[] y, int [] vx, int[] vy, int entities, int myTeamId, int goalUpperY, int goalLowerY, int goalX, int[] entityId, int snaIndToAvoid, int goalUpperX, int goalLowerX){
    	int targetSna = -1;
    	for (int j = 4; j < entities-2; j++){
        	if (j != snaIndToAvoid && (vx[j] != 0 || vy[j] != 0)){
	    		int rise = vy[j];
	    		int run = vx[j];
	    		double velocity = Math.sqrt(rise*rise + run*run);
	    		double angle = angleCalculationOfVector(x[j], y[j], x[j] + run, y[j] + rise);
	    		double angleFromUpGoal = angleOfPointFromHiMyGoal(x[j], y[j], myTeamId, goalUpperX, goalUpperY);
	    		double angleFromLoGoal = angleOfPointFromDoMyGoal(x[j], y[j], myTeamId, goalLowerX, goalLowerY);
	    		if (myTeamId == 0){
	    			if (angle > angleFromUpGoal && angle < angleFromLoGoal && x[j] < 5000 && y[j] > goalUpperY && y[j] < goalLowerY && velocity > 100){
	    				targetSna = j;
	    				break;
	    			}
	    		}
	    		else {
	    			if (angle < angleFromUpGoal && angle > angleFromLoGoal && x[j] > 12000 && y[j] > goalUpperY && y[j] < goalLowerY && velocity > 100){
	    				targetSna = j;
	    				break;
	    			}	    			
	    		}
	    	}
        }
    	if (gameTurn >= 10 && targetSna != -1){
        	System.out.println("PETRIFICUS " + entityId[targetSna]);
        	gameTurn -= 10;
    	}
    	else {targetSna = -1;}
    	return targetSna;
    }
//------------------------------determines the angle one entities makes with another entity------------------------------------
    
    double angleCalculationOfVector(int xRef, int yRef, int xObj, int yObj){
    	int width = xRef - xObj;
    	int length = Math.abs(yRef - yObj);
        double distToClosSna = Math.sqrt(width*width + length*length);
        double angleFromClosSna = Math.asin(length/distToClosSna);
        if (xRef < xObj && yRef >= yObj){
        	angleFromClosSna = 2*Math.PI + angleFromClosSna;
        }
        else if (xRef < xObj && yRef < yObj){
        	angleFromClosSna = 2*Math.PI - angleFromClosSna;
        }		
        else if (xRef > xObj && yRef >= yObj){
        	angleFromClosSna = Math.PI - angleFromClosSna;
        }
        else {
        	angleFromClosSna = Math.PI + angleFromClosSna;
        }
        return angleFromClosSna;
    }   
  //---------------------------calculates angle of point on boundary with lower pole of my goal-------------------------------------
    double angleOfPointFromDoMyGoal(int x, int y, int myTeamId, int goalLowerX, int goalLowerY){
        int widthToLoGoal = x - (16000-goalLowerX);
        int lengthToLoGoal = Math.abs(y - goalLowerY);
        double distToLoGoal = Math.sqrt(widthToLoGoal*widthToLoGoal + lengthToLoGoal*lengthToLoGoal);
        double angleFromLoGoal = Math.asin(lengthToLoGoal/distToLoGoal);
        if (myTeamId == 1){
	        if (y > goalLowerY) {angleFromLoGoal = 2*Math.PI + angleFromLoGoal;}
	        else {angleFromLoGoal = 2*Math.PI - angleFromLoGoal;}		        	
        }
        else {
	        if (y >= goalLowerY) {angleFromLoGoal = Math.PI - angleFromLoGoal;}
	        else {angleFromLoGoal = Math.PI + angleFromLoGoal;}			        	
        }
        return angleFromLoGoal;
    }
//-------------------------calculates angle of point on boundary with upper pole of my goal-------------------------------------
    double angleOfPointFromHiMyGoal(int x, int y, int myTeamId,int goalUpperX, int goalUpperY){
        int widthToUpGoal = x - (16000-goalUpperX);
        int lengthToUpGoal = Math.abs(y - goalUpperY);
        double distToUpGoal = Math.sqrt(widthToUpGoal*widthToUpGoal + lengthToUpGoal*lengthToUpGoal);
        double angleFromUpGoal = Math.asin(lengthToUpGoal/distToUpGoal);
        if (myTeamId == 1){
	        if (y >= goalUpperY) {angleFromUpGoal = 2*Math.PI + angleFromUpGoal;}
	        else {angleFromUpGoal = 2*Math.PI - angleFromUpGoal;}		        	
        }
        else {
	        if (y < goalUpperY) {angleFromUpGoal = Math.PI + angleFromUpGoal;}
	        else {angleFromUpGoal = Math.PI - angleFromUpGoal;}			        	
        }
        return angleFromUpGoal;
    }   
//------------------ to target the enemy wizards close by------------------------------------------------------
    int freezeEnemyWiz(int[] x, int[] y, int entities, int wizToAvoidInd, int[] entityId){
    	int enWizInd = -1;
    	for (int i = 0; i < 2; i++){
    		boolean checker = false;
    		for (int j = 2; j < 4; j++){
    	    	int width = x[i] - x[j];
    	    	int length = Math.abs(y[i] - y[j]);
    	        double disToClosWiz = Math.sqrt(width*width + length*length);
    	        if (disToClosWiz < 1000){
    	        	enWizInd = j;
    	        	checker = true;
    	        	break;
    	        }
    		}
    		if (checker = true){break;}
    	}
    	if (gameTurn >= 10 && enWizInd != -1){
        	System.out.println("PETRIFICUS " + entityId[enWizInd]);
        	gameTurn -= 10;
    	}
    	else {enWizInd = -1;}
    	return enWizInd;
    }
//-----------------------------------accio method--------------------------------------------------------------
    int accio(int[] x, int[] y, int[] vx, int[] vy, int myTeamId, int wizInd, int snaIndToAvoid, int entities, int[] entityId){
    	int targetedSnaInd = -1;
    	for (int i = 4; i < entities-2; i++){
        	int width = x[wizInd] - x[i];
        	int length = Math.abs(y[wizInd] - y[i]);
            double distToClosSna = Math.sqrt(width*width + length*length);
    		if (myTeamId == 0){
    			if (x[i] < x[wizInd] && distToClosSna < 5000){
    				targetedSnaInd = i;
    				break;
    			}
    		}
    		else{
    			if (x[i] > x[wizInd] && distToClosSna < 5000){
    				targetedSnaInd = i;
    				break;
    			}
    		}
    	}
    	if (gameTurn >= 20 && targetedSnaInd != -1){
        	System.out.println("ACCIO " + entityId[targetedSnaInd]);
        	gameTurn -= 20;
    	}
    	else {targetedSnaInd = -1;}
    	return targetedSnaInd;
    }
//-------------------keeps track of spell gauge-----------------------------------------------------------------------------------------------------
    
    void gameTurnTracker(){
    	gameTurn += 1;
    }
//---------------------------------------------------------------------------------------------------------------------------------------------
}

/*
//--------------------------------calculates destination of snaffle given position and vector--------------------------------------------
    int destOfSna(int [] x, int[] y, int [] vx, int[] vy, int entities, int myTeamId, int goalUpperY, int goalLowerY, int goalX, int[] entityId, int snaIndToAvoid){
    	double distToGoal = 5000;
    	int targetSna = -1;
    	for (int j = 4; j < entities-2; j++){
        	if (j != snaIndToAvoid){
	    		int width = x[j] - goalX;
	        	int lengthG = Math.abs(y[j] - 3750);
	            double snaIdisToGoal = Math.sqrt(width*width + lengthG*lengthG);
	    		float rise = vy[j];
	    		float run = vx[j];
	    		float slope = rise/run;
	    		double length = Math.sqrt(rise*rise + run*run);
	    		double angle = Math.asin(Math.abs(rise/length));
	    		int turnsToZero = 0;
	    		if (length > 1){
		    		for (int i = 1; i < 400; i++){
		    			double dumm = length*Math.pow(.75, i);
		    			if (dumm < .5){
		    				turnsToZero = i;
		    				break;
		    			}	
		    		}
		    		double displacement = length*turnsToZero + .5*.75*turnsToZero*turnsToZero;
		    		long xDis = Math.round(Math.cos(angle)*displacement);
		    		long yDis = Math.round(Math.sin(angle)*displacement);
		    		if ( vx[j] >= 0 && vy[j] >= 0){
		    			xDis = Math.round(x[j] + xDis);
		    			yDis = Math.round(y[j] + yDis);
		    		}
		    		else if (vx[j] >= 0 && vy[j] < 0){
		    			xDis = Math.round(x[j] + xDis);
		    			yDis = Math.round(y[j] - yDis);    			
		    		}
		    		else if (vx[j] < 0 && vy[j] < 0){
		    			xDis = Math.round(x[j] - xDis);
		    			yDis = Math.round(y[j] - yDis);   			
		    		}    
		    		else if (vx[j] < 0 && vy[j] >= 0){
		    			xDis = Math.round(x[j] - xDis);
		    			yDis = Math.round(y[j] + yDis);     			
		    		}
		    		if (myTeamId == 0){
		    			if (xDis <0){
		    				yDis = Math.round(slope*(0-x[j])+y[j]);
		    			} 
		    			if (xDis <= 0 && yDis >= goalLowerY && yDis <= goalUpperY && snaIdisToGoal < distToGoal){
		    				distToGoal = snaIdisToGoal;
		    				targetSna = j;
		    			}
		    		}
		    		else {
		    			if (xDis > 16000){
		    				yDis = Math.round(slope*(16000-x[j])+y[j]);
		    			} 
		    			if (xDis >= 16000 && yDis >= goalLowerY && yDis <= goalUpperY && snaIdisToGoal < distToGoal){
		    				distToGoal = snaIdisToGoal;
		    				targetSna = j;
		    			}    			
		    		}
	    		}
	    	}
        }
    	if (gameTurn >= 10 && targetSna != -1){
        	System.out.println("ACCIO " + entityId[targetSna]);
        	gameTurn -= 10;
    	}
    	else { targetSna = -1;}
    	return targetSna;
    }
      
 
 */

