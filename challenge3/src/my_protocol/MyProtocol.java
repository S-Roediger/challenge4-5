package my_protocol;

import framework.IMACProtocol;
import framework.MediumState;
import framework.TransmissionInfo;
import framework.TransmissionType;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * A fairly trivial Medium Access Control scheme.
 *
 * @author Jaco ter Braak, University of Twente
 * @version 05-12-2013
 *
 * Copyright University of Twente, 2013-2019
 *
 **************************************************************************
 *                            Copyright notice                            *
 *                                                                        *
 *             This file may ONLY be distributed UNMODIFIED.              *
 * In particular, a correct solution to the challenge must NOT be posted  *
 * in public places, to preserve the learning effect for future students. *
 **************************************************************************
 */
public class MyProtocol implements IMACProtocol {

	int id = new Random().nextInt(10000000);
	Queue<Integer> q = new LinkedList<>();
	
	
    @Override
    public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
                                              int controlInformation, int localQueueLength) {
    	
    	if (previousMediumState == MediumState.Succes) {
    		if (q.size() > 0) {
    			q.remove(controlInformation); //remove controlInformation/id to prevent this node from blocking pipe
    		}
    	}
    	
    	if (previousMediumState == MediumState.Idle) {
    		if (q.size() > 0) {
    			q.remove(); //remove head since this node seems to have nothing to send anymore
    		}
    	}
    	
    	if (previousMediumState == MediumState.Collision) {
    		
    		if (localQueueLength == 0) { //if you don't have to send anything, send controlInformation = 0 (so you won't be added to queue again)
    			return new TransmissionInfo(TransmissionType.Silent, 0);
    		}
    		
    		if (new Random().nextInt(100) < 98 && !q.contains(id)) { //if you want to send something and node id is not already in queue
    			return new TransmissionInfo(TransmissionType.Data, id); //send id to be added in queue
    			
    		} else { //if you are already in queue, send nothing now and send controlInformation = 0 (so you won't be added to queue again)
    			return new TransmissionInfo(TransmissionType.Silent, 0);
    		}
    		
    	} else { //if this node wasn't involved in any of the above activities
    		
    		if (controlInformation != 0) { //if the sent controlInformation was unequal to 0, this node still needs to send things, 
    			q.add(controlInformation); //so add to queue
    		}
    		
    		if (localQueueLength == 0) {
    			return new TransmissionInfo(TransmissionType.Silent, 0); //if you don't have to send anything anymore .. is this necessary?
    			
    		} else if (localQueueLength != 0 && !q.contains(id)) { //if you want to send something, but you're not in line yet
    			return new TransmissionInfo(TransmissionType.Data, id); //send data with your id
    			
    		} else if (q.peek() == id) { //if you are at the front of the queue, send!
    			return new TransmissionInfo(TransmissionType.Data, id);
    			
    		} else {
    			return new TransmissionInfo(TransmissionType.Silent, 0);
    		}
    		
    		
    		
    	}
    	
    	
//        // No data to send, just be quiet
//        if (localQueueLength == 0) {
//            System.out.println("SLOT - No data to send.");
//            return new TransmissionInfo(TransmissionType.Silent, 0);
//        }
//
//        // Randomly transmit with 60% probability
//        if (new Random().nextInt(100) < 60) {
//            System.out.println("SLOT - Sending data and hope for no collision.");
//            return new TransmissionInfo(TransmissionType.Data, 0);
//        } else {
//            System.out.println("SLOT - Not sending data to give room for others.");
//            return new TransmissionInfo(TransmissionType.Silent, 0);
//        }

    }

}
