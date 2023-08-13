package Utils;

import Data.Board;
import Data.Integer2;

import java.util.ArrayList;

public interface INeighbourFinder {
    ArrayList<Integer2> GetNeighbours(Board board, int playerNumber, Integer2 center);
}
