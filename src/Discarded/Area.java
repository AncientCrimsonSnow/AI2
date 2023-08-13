package Discarded;

import Data.Integer2;

import java.util.HashSet;

public class Area {
    public int occupation = -1;
    public HashSet<Integer2> cells = new HashSet();

    @Override
    public String toString(){
        return "(" + occupation + " Size: " + cells.size() + ")";
    }
}
