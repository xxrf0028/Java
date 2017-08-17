package castle;

import java.util.HashMap;

public class Room {
    private String description;
//    private Room northExit;
//    private Room southExit;
//    private Room eastExit;
//    private Room westExit;
    
    private HashMap<String,Room> exits = new HashMap<String,Room>();

    public Room(String description) {
        this.description = description;
    }

    public void setExit(String dir,Room room){
    	exits.put(dir, room);
    }
    		
//    public void setExits(Room north, Room east, Room south, Room west) {
//        if(north != null)
//            northExit = north;
//        if(east != null)
//            eastExit = east;
//        if(south != null)
//            southExit = south;
//        if(west != null)
//            westExit = west;
//    }

    @Override
    public String toString(){
        return description;
    }
    
    public String getExitDesc(){
    	StringBuffer ret = new StringBuffer();
    	for(String dir:exits.keySet()){
    		ret.append(dir);
    		ret.append(" ");
    	}
//    	 if(northExit != null)
//    		 ret.append("north ");
//         if(eastExit != null)
//        	 ret.append("east ");
//         if(southExit != null)
//        	 ret.append("south ");
//         if(westExit != null)
//        	 ret.append("west ");
    	return ret.toString();
    }
    
    public Room getExitRoom(String direction){
    	Room nextRoom = exits.get(direction);
//    	if(direction.equals("north")) {
//            nextRoom = northExit;
//        }
//        if(direction.equals("east")) {
//            nextRoom = eastExit;
//        }
//        if(direction.equals("south")) {
//            nextRoom = southExit;
//        }
//        if(direction.equals("west")) {
//            nextRoom = westExit;
//        }
    	return nextRoom;
    }
}
