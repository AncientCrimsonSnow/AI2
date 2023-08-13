package Utils;

import Data.PathCell;

import java.util.Queue;

public class Debug {
    public static <T> void Log(T value){
        System.out.println(value);
    }

    public static void PrintFinalPath(Queue<PathCell> path){
        var result = "";
        for(var cell : path){
            result += "->" + cell.pos + "|" + cell.value;
        }
        Log(result);
    }
}
