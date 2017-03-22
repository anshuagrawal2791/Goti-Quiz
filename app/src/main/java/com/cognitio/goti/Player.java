package com.cognitio.goti;

import java.util.HashMap;

/**
 * Created by anshu on 22/03/17.
 */

public class Player implements Comparable{
    private String Name,IP;
    private HashMap<String,String> map;

    public Player(String name, String IP) {
        Name = name;
        this.IP = IP;
    }

    public String getName() {
        return Name;
    }

    public String getIP() {
        return IP;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }


    @Override
    public String toString() {
        return "Player{" +
                "Name='" + Name + '\'' +
                ", IP='" + IP + '\'' +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        Player p = (Player)o;
//        if(p.getIP().equals(IP))
//            return 0;
        return 1;
    }
}
