package Testing;

import Data.Integer2;
import Discarded.*;
import Systems.BoardSystem;
import Systems.GameSystem;
import Systems.PathSystem;
import Utils.ConsoleColors;
import Utils.Debug;
import lenz.htw.duktus.net.Update;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Tester {

    public static void main(String[] args) {
        var game = GameSystem.InitGame(new MyClient());
        //TestInit(game);
        //UpdateTest(game);
        TestPathConverter();
    }

    private static void TestInit(Data.Game game){
        for(var p = 0; p != Data.Player.PLAYER_COUNT; p++){
            for(var b = 0; b != Data.Bot.BOT_COUNT; b++){
                var testPos = TestData.TEST_BOT_POS[p][b].Multiply(Data.Board.SCALE);

                if(testPos.x == Data.Board.BOARD_SIDE_LENGTH)
                    testPos.x--;
                if(testPos.y == Data.Board.BOARD_SIDE_LENGTH)
                    testPos.y--;

                EqualTest(game.player[p].bots[b].pos, testPos, String.format("Player[%d], Bot[%d] Position", p, b));
            }
        }
        EqualTest(BoardSystem.GetAreaCount(game.board), 10, "Init Area Count");

        var areaCellCount = 0;
        for(var area : game.board.areas)
            areaCellCount += area.cells.size();

        EqualTest(areaCellCount, game.board.cellAreaMap.size() - game.board.staticWalls.cells.size(), "Cell Count Init Check");
    }
    
    private static void UpdateTest(Data.Game game){
        var trueAreaSideLength = Data.Board.DEFAULT_BOARD_SIDE_LENGTH / 3f;
        var areaSideLength = Data.Board.BOARD_SIDE_LENGTH / 3f;

        var botsAlive = new boolean[]{true, true, true, true, true, true, true, true, true};
        var didCheckBotDead = new boolean[]{false, false, false, false, false, false, false, false, false};
        
        for(var frame = 0; CheckIfAnyTrue(botsAlive); frame++){
            var updateP0B0 = new Update();
            updateP0B0.player = 0;
            updateP0B0.bot = 0;
            updateP0B0.x = frame;
            updateP0B0.y = Math.round(trueAreaSideLength * 0.5f);

            var updateP0B1 = new Update();
            updateP0B1.player = 0;
            updateP0B1.bot = 1;
            updateP0B1.x = Math.max(frame - 16, 0);
            updateP0B1.y = Math.round(trueAreaSideLength * 1.5f);

            var updateP0B2 = new Update();
            updateP0B2.player = 0;
            updateP0B2.bot = 2;
            updateP0B2.x = Math.max(frame - 16, 0);
            updateP0B2.y = Math.round(trueAreaSideLength * 2.5f);
            
            var updateP1B0 = new Update();
            updateP1B0.player = 1;
            updateP1B0.bot = 0;
            updateP1B0.x = Math.round(trueAreaSideLength/3f);
            updateP1B0.y = Math.max(frame - 16, 0);

            var updateP1B1 = new Update();
            updateP1B1.player = 1;
            updateP1B1.bot = 1;
            updateP1B1.x = Math.round(trueAreaSideLength + Math.round(trueAreaSideLength * 2f / 3f));
            updateP1B1.y = frame;

            var updateP1B2 = new Update();
            updateP1B2.player = 1;
            updateP1B2.bot = 2;
            updateP1B2.x = Math.round(2 * trueAreaSideLength + Math.round(trueAreaSideLength / 3f));
            updateP1B2.y = Math.max(frame - 16, 0);;

            var updateP2B0 = new Update();
            updateP2B0.player = 2;
            updateP2B0.bot = 0;
            updateP2B0.x = Math.round(trueAreaSideLength * 2f / 3f);
            updateP2B0.y = Board.DEFAULT_BOARD_SIDE_LENGTH - frame - 1;

            var updateP2B1 = new Update();
            updateP2B1.player = 2;
            updateP2B1.bot = 1;
            updateP2B1.x = Math.round(trueAreaSideLength + Math.round(trueAreaSideLength / 3f));
            updateP2B1.y = Board.DEFAULT_BOARD_SIDE_LENGTH - frame - 1;

            var updateP2B2 = new Update();
            updateP2B2.player = 2;
            updateP2B2.bot = 2;
            updateP2B2.x = Board.DEFAULT_BOARD_SIDE_LENGTH - Math.round(trueAreaSideLength / 3f);
            updateP2B2.y = Board.DEFAULT_BOARD_SIDE_LENGTH - frame - 1;

            if(botsAlive[6] && (Math.round(updateP2B0.y * Board.SCALE) <= Math.round(areaSideLength * (2f+4f/5f)))){
                botsAlive[6] = false;
            }
            if(botsAlive[7] && (Math.round(updateP2B1.y * Board.SCALE) <= Math.round(areaSideLength * (2f+4f/5f)))){
                botsAlive[7] = false;
            }
            if(botsAlive[3] && (Math.round(updateP1B0.y * Board.SCALE) >= Math.round(areaSideLength * 0.5f))){
                botsAlive[3] = false;
            }
            if(botsAlive[0] && (Math.round(updateP0B0.x * Board.SCALE) >= Math.round(areaSideLength + Math.round(areaSideLength * 2f / 3f)))){
                botsAlive[0] = false;
            }
            if(botsAlive[1] && (Math.round(updateP0B1.x * Board.SCALE) >= Math.round(areaSideLength + Math.round(areaSideLength * 2f / 3f)))){
                botsAlive[1] = false;
            }
            if(botsAlive[4] && (Math.round(updateP1B1.y * Board.SCALE) >= Math.round(areaSideLength * 2.5f))){
                botsAlive[4] = false;
            }
            if(botsAlive[5] && (Math.round(updateP1B2.y * Board.SCALE) >= Math.round(areaSideLength * 2.5f))){
                botsAlive[5] = false;
            }

            if(botsAlive[2] && (Math.round(updateP0B2.x * Board.SCALE) >= Math.round(areaSideLength * (2f + 2f/3f)) + 1)){
                botsAlive[2] = false;
            }
            if(botsAlive[8] && (Math.round(updateP2B2.y * Board.SCALE) <= 0)){
                botsAlive[8] = false;
                break;
            }

            if(botsAlive[0]){
                var startTime = System.nanoTime();
                GameSystem.Update(game, updateP0B0);
                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;
                SmallerEqualTest(executionTime/1000000000f, 0.003f, "UpdateDurationTest");
            }
            if(botsAlive[1]){
                var startTime = System.nanoTime();
                GameSystem.Update(game, updateP0B1);
                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;
                SmallerEqualTest(executionTime/1000000000f, 0.003f, "UpdateDurationTest");
            }
            if(botsAlive[2]){
                var startTime = System.nanoTime();
                GameSystem.Update(game, updateP0B2);
                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;
                SmallerEqualTest(executionTime/1000000000f, 0.003f, "UpdateDurationTest");
            }
            if(botsAlive[3]){
                var startTime = System.nanoTime();
                GameSystem.Update(game, updateP1B0);
                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;
                SmallerEqualTest(executionTime/1000000000f, 0.003f, "UpdateDurationTest");
            }
            if(botsAlive[4]){
                var startTime = System.nanoTime();
                GameSystem.Update(game, updateP1B1);
                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;
                SmallerEqualTest(executionTime/1000000000f, 0.003f, "UpdateDurationTest");
            }

            if(botsAlive[5]){
                var startTime = System.nanoTime();
                GameSystem.Update(game, updateP1B2);
                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;
                SmallerEqualTest(executionTime/1000000000f, 0.003f, "UpdateDurationTest");
            }
            if(botsAlive[6]){
                var startTime = System.nanoTime();
                GameSystem.Update(game, updateP2B0);
                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;
                SmallerEqualTest(executionTime/1000000000f, 0.003f, "UpdateDurationTest");
            }
            if(botsAlive[7]){
                var startTime = System.nanoTime();
                GameSystem.Update(game, updateP2B1);
                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;
                SmallerEqualTest(executionTime/1000000000f, 0.003f, "UpdateDurationTest");
            }
            if(botsAlive[8]){
                var startTime = System.nanoTime();
                GameSystem.Update(game, updateP2B2);
                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;
                SmallerEqualTest(executionTime/1000000000f, 0.003f, "UpdateDurationTest");
            }

            var i = 0;
            for(var p = 0; p != Data.Player.PLAYER_COUNT; p++){
                for(var b = 0; b != Data.Bot.BOT_COUNT; b++, i++){
                    EqualTest(game.player[p].bots[b].alive, botsAlive[i], String.format("Player[%d], Bot[%d] Alive Check", p, b));
                    if(!botsAlive[i] && !didCheckBotDead[i]){
                        didCheckBotDead[i] = true;
                        CheckBotDead(new Integer2(p, b), areaSideLength, game);
                    }
                }
            }
        }
    }

    private static void CheckBotDead(Integer2 bot, float areaSideLength, Data.Game game){
        if(bot.equals(new Integer2(2,0))){
            EqualTest(BoardSystem.GetAreaCount(game.board), 12, "Bot[2,0] AreaCountCheck");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[2,0] Dead, Area[0,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), 0)).occupation, -1, "Bot[2,0] Dead, Area[1,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), 0)).occupation, -1, "Bot[2,0] Dead, Area[2,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength))).occupation, -1, "Bot[2,0] Dead, Area[0,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength))).occupation, -1, "Bot[2,0] Dead, Area[1,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), Math.round(areaSideLength))).occupation, -1, "Bot[2,0] Dead, Area[2,1] Occupation");


            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 2))).occupation, -1, "Bot[2,0] Dead, Area1[0,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2f / 3f) + 1, Math.round(areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[2,0] Dead, Area2[0,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 2))).occupation, -1, "Bot[2,0] Dead, Area1[1,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength + Math.round(areaSideLength / 3f)), Math.round(areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[2,0] Dead, Area2[1,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), Math.round(areaSideLength * 2))).occupation, -1, "Bot[2,0] Dead, Area[2,2] Occupation");
        }
        else if(bot.equals(new Integer2(2,1))){
            EqualTest(BoardSystem.GetAreaCount(game.board), 12, "Bot[2,1] AreaCountCheck");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[2,1] Dead, Area[0,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), 0)).occupation, -1, "Bot[2,1] Dead, Area[1,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), 0)).occupation, -1, "Bot[2,1] Dead, Area[2,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength))).occupation, -1, "Bot[2,1] Dead, Area[0,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength))).occupation, -1, "Bot[2,1] Dead, Area[1,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), Math.round(areaSideLength))).occupation, -1, "Bot[2,1] Dead, Area[2,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 2))).occupation, -1, "Bot[2,1] Dead, Area1[0,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2f / 3f) + 1, Math.round(areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[2,1] Dead, Area2[0,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 2))).occupation, -1, "Bot[2,1] Dead, Area1[1,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength + Math.round(areaSideLength / 3f)), Math.round(areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[2,1] Dead, Area2[1,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), Math.round(areaSideLength * 2))).occupation, -1, "Bot[2,1] Dead, Area[2,2] Occupation");
        }
        else if(bot.equals(new Integer2(1,0))){
            EqualTest(BoardSystem.GetAreaCount(game.board), 13, "Bot[1,0] AreaCountCheck");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[1,0] Dead, Area1[0,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 0.5f + 1))).occupation, -1, "Bot[1,0] Dead, Area2[0,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), 0)).occupation, -1, "Bot[1,0] Dead, Area[0,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), 0)).occupation, -1, "Bot[1,0] Dead, Area[0,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength))).occupation, -1, "Bot[1,0] Dead, Area[1,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength))).occupation, -1, "Bot[1,0] Dead, Area[1,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), Math.round(areaSideLength))).occupation, -1, "Bot[1,0] Dead, Area[2,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 2))).occupation, -1, "Bot[1,0] Dead, Area1[0,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2f / 3f) + 1, Math.round(areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[1,0] Dead, Area2[0,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 2))).occupation, -1, "Bot[1,0] Dead, Area1[1,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength + Math.round(areaSideLength / 3f)), Math.round(areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[1,0] Dead, Area2[1,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), Math.round(areaSideLength * 2))).occupation, -1, "Bot[1,0] Dead, Area[2,2] Occupation");
        }
        else if(bot.equals(new Integer2(0,0))){
            EqualTest(BoardSystem.GetAreaCount(game.board), 20, "Bot[0,0] AreaCountCheck");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[0,0] Dead, Area1[0,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 0.5f + 1))).occupation, -1, "Bot[0,0] Dead, Area2[0,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength/3f) + 1, 0)).occupation, -1, "Bot[0,0] Dead, Area3[0,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), 0)).occupation, 0, "Bot[0,0] Dead, Area1[1,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[0,0] Dead, Area2[1,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (1f + 2f/3f)) + 3, 0)).occupation, -1, "Bot[0,0] Dead, Area3[1,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), 0)).occupation, -1, "Bot[0,0] Dead, Area1[2,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, -1, "Bot[0,0] Dead, Area2[2,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength))).occupation, -1, "Bot[0,0] Dead, Area[0,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[0,0] Dead, Area[0,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength))).occupation, 0, "Bot[0,0] Dead, Area[1,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), Math.round(areaSideLength))).occupation, 1, "Bot[0,0] Dead, Area[2,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 2))).occupation, -1, "Bot[0,0] Dead, Area1[0,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2f / 3f) + 1, Math.round(areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[0,0] Dead, Area2[0,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[0,0] Dead, Area3[0,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 2))).occupation, 0, "Bot[0,0] Dead, Area1[1,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 3)- 1)).occupation, -1, "Bot[0,0] Dead, Area2[1,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), Math.round(areaSideLength * 2))).occupation, -1, "Bot[0,0] Dead, Area1[2,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 3)- 1, Math.round(areaSideLength * 3)- 1)).occupation, -1, "Bot[0,0] Dead, Area2[2,2] Occupation");
        }
        else if(bot.equals(new Integer2(0,1))){
            EqualTest(BoardSystem.GetAreaCount(game.board), 21, "Bot[0,1] AreaCountCheck");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[0,1] Dead, Area1[0,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 0.5f + 1))).occupation, -1, "Bot[0,1] Dead, Area2[0,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength/3f) + 1, 0)).occupation, -1, "Bot[0,1] Dead, Area3[0,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), 0)).occupation, 0, "Bot[0,1] Dead, Area1[1,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[0,1] Dead, Area2[1,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (1f + 2f/3f)) + 3, 0)).occupation, -1, "Bot[0,1] Dead, Area3[1,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), 0)).occupation, -1, "Bot[0,1] Dead, Area1[2,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, -1, "Bot[0,1] Dead, Area2[2,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength))).occupation, -1, "Bot[0,1] Dead, Area[0,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[0,1] Dead, Area[0,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength))).occupation, 0, "Bot[0,1] Dead, Area1[1,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 1.5f) + 1)).occupation, 0, "Bot[0,1] Dead, Area2[1,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), Math.round(areaSideLength))).occupation, 1, "Bot[0,1] Dead, Area[2,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 2))).occupation, -1, "Bot[0,1] Dead, Area1[0,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2f / 3f) + 1, Math.round(areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[0,1] Dead, Area2[0,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[0,1] Dead, Area3[0,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 2))).occupation, 0, "Bot[0,1] Dead, Area1[1,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 3)- 1)).occupation, -1, "Bot[0,1] Dead, Area2[1,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), Math.round(areaSideLength * 2))).occupation, -1, "Bot[0,1] Dead, Area1[2,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 3)- 1, Math.round(areaSideLength * 3)- 1)).occupation, -1, "Bot[0,1] Dead, Area2[2,2] Occupation");
        }
        else if(bot.equals(new Integer2(1,1))){
            EqualTest(BoardSystem.GetAreaCount(game.board), 26, "Bot[1,1] AreaCountCheck");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[1,1] Dead, Area1[0,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 0.5f + 1))).occupation, -1, "Bot[1,1] Dead, Area2[0,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength/3f) + 1, 0)).occupation, -1, "Bot[1,1] Dead, Area3[0,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), 0)).occupation, 0, "Bot[1,1] Dead, Area1[1,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[1,1] Dead, Area2[1,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (1f + 2f/3f)) + 3, 0)).occupation, -1, "Bot[1,1] Dead, Area3[1,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), 0)).occupation, -1, "Bot[1,1] Dead, Area1[2,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, 2, "Bot[1,1] Dead, Area2[2,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength))).occupation, -1, "Bot[1,1] Dead, Area[0,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[1,1] Dead, Area[0,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength))).occupation, 0, "Bot[1,1] Dead, Area1[1,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 1.5f) + 1)).occupation, 0, "Bot[1,1] Dead, Area2[1,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (1f + 2f/3f)) + 1, Math.round(areaSideLength))).occupation, 0, "Bot[1,1] Dead, Area3[1,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), Math.round(areaSideLength))).occupation, 1, "Bot[1,1] Dead, Area1[2,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (2f + 1f/3f) + 1), Math.round(areaSideLength))).occupation, 1, "Bot[1,1] Dead, Area2[2,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 3)- 1, Math.round(areaSideLength))).occupation, 1, "Bot[1,1] Dead, Area3[2,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 2))).occupation, -1, "Bot[1,1] Dead, Area1[0,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2f / 3f) + 1, Math.round(areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[1,1] Dead, Area2[0,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[1,1] Dead, Area3[0,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 2))).occupation, 1, "Bot[1,1] Dead, Area1[1,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 3)- 1)).occupation, -1, "Bot[1,1] Dead, Area2[1,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2f) - 1, Math.round(areaSideLength * 3f) - 1)).occupation, 1, "Bot[1,1] Dead, Area3[1,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (1f + 2f/3f) + 1), Math.round(areaSideLength * 2))).occupation, 1, "Bot[1,1] Dead, Area4[1,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), Math.round(areaSideLength * 2))).occupation, 1, "Bot[1,1] Dead, Area1[2,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 3)- 1, Math.round(areaSideLength * 3)- 1)).occupation, -1, "Bot[1,1] Dead, Area2[2,2] Occupation");
        }

        else if(bot.equals(new Integer2(1,2))){
            EqualTest(BoardSystem.GetAreaCount(game.board), 27, "Bot[1,2] AreaCountCheck");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[1,2] Dead, Area1[0,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 0.5f + 1))).occupation, -1, "Bot[1,2] Dead, Area2[0,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength/3f) + 1, 0)).occupation, -1, "Bot[1,2] Dead, Area3[0,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), 0)).occupation, 0, "Bot[1,2] Dead, Area1[1,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[1,2] Dead, Area2[1,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (1f + 2f/3f)) + 3, 0)).occupation, -1, "Bot[1,2] Dead, Area3[1,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), 0)).occupation, -1, "Bot[1,2] Dead, Area1[2,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, 2, "Bot[1,2] Dead, Area2[2,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength))).occupation, -1, "Bot[1,2] Dead, Area[0,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[1,2] Dead, Area[0,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength))).occupation, 0, "Bot[1,2] Dead, Area1[1,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 1.5f) + 1)).occupation, 0, "Bot[1,2] Dead, Area2[1,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (1f + 2f/3f)) + 1, Math.round(areaSideLength))).occupation, 0, "Bot[1,2] Dead, Area3[1,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), Math.round(areaSideLength))).occupation, 1, "Bot[1,2] Dead, Area1[2,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (2f + 1f/3f) + 1), Math.round(areaSideLength))).occupation, 1, "Bot[1,2] Dead, Area2[2,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 3)- 1, Math.round(areaSideLength))).occupation, 1, "Bot[1,2] Dead, Area3[2,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 2))).occupation, -1, "Bot[1,2] Dead, Area1[0,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2f / 3f) + 1, Math.round(areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[1,2] Dead, Area2[0,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[1,2] Dead, Area3[0,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 2))).occupation, 1, "Bot[1,2] Dead, Area1[1,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 3)- 1)).occupation, -1, "Bot[1,2] Dead, Area2[1,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2f) - 1, Math.round(areaSideLength * 3f) - 1)).occupation, 1, "Bot[1,2] Dead, Area3[1,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (1f + 2f/3f) + 1), Math.round(areaSideLength * 2))).occupation, 1, "Bot[1,2] Dead, Area4[1,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), Math.round(areaSideLength * 2))).occupation, 1, "Bot[1,2] Dead, Area1[2,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 3)- 1, Math.round(areaSideLength * 3)- 1)).occupation, -1, "Bot[1,2] Dead, Area2[2,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2.5f), Math.round(areaSideLength * 2))).occupation, 1, "Bot[1,2] Dead, Area3[2,2] Occupation");
        }
        else if(bot.equals(new Integer2(0,2))){
            EqualTest(BoardSystem.GetAreaCount(game.board), 28, "Bot[0,2] AreaCountCheck");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[0,2] Dead, Area1[0,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 0.5f + 1))).occupation, -1, "Bot[0,2] Dead, Area2[0,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength/3f) + 1, 0)).occupation, -1, "Bot[0,2] Dead, Area3[0,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), 0)).occupation, 0, "Bot[0,2] Dead, Area1[1,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[0,2] Dead, Area2[1,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (1f + 2f/3f)) + 3, 0)).occupation, -1, "Bot[0,2] Dead, Area3[1,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), 0)).occupation, -1, "Bot[0,2] Dead, Area1[2,0] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, 2, "Bot[0,2] Dead, Area2[2,0] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength))).occupation, -1, "Bot[0,2] Dead, Area[0,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[0,2] Dead, Area[0,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength))).occupation, 0, "Bot[0,2] Dead, Area1[1,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 1.5f) + 1)).occupation, 0, "Bot[0,2] Dead, Area2[1,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (1f + 2f/3f)) + 1, Math.round(areaSideLength))).occupation, 0, "Bot[0,2] Dead, Area3[1,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), Math.round(areaSideLength))).occupation, 1, "Bot[0,2] Dead, Area1[2,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (2f + 1f/3f) + 1), Math.round(areaSideLength))).occupation, 1, "Bot[0,2] Dead, Area2[2,1] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 3)- 1, Math.round(areaSideLength))).occupation, 1, "Bot[0,2] Dead, Area3[2,1] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 2))).occupation, -1, "Bot[0,2] Dead, Area1[0,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2f / 3f) + 1, Math.round(areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[0,2] Dead, Area2[0,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(0, Math.round(areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[0,2] Dead, Area3[0,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 2))).occupation, 1, "Bot[0,2] Dead, Area1[1,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength), Math.round(areaSideLength * 3)- 1)).occupation, -1, "Bot[0,2] Dead, Area2[1,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2f) - 1, Math.round(areaSideLength * 3f) - 1)).occupation, 1, "Bot[0,2] Dead, Area3[1,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * (1f + 2f/3f) + 1), Math.round(areaSideLength * 2))).occupation, 1, "Bot[0,2] Dead, Area4[1,2] Occupation");

            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2), Math.round(areaSideLength * 2))).occupation, 1, "Bot[0,2] Dead, Area1[2,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 3)- 1, Math.round(areaSideLength * 3)- 1)).occupation, -1, "Bot[0,2] Dead, Area2[2,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2.5f), Math.round(areaSideLength * 2))).occupation, 1, "Bot[0,2] Dead, Area3[2,2] Occupation");
            EqualTest(game.board.cellAreaMap.get(new Integer2(Math.round(areaSideLength * 2.5f), Math.round(areaSideLength * 3) - 1)).occupation, 1, "Bot[0,2] Dead, Area3[2,2] Occupation");
        }
        else if(bot.equals(new Integer2(2,2))){}
    }

    private static void SetAreas(int horizontalAreaCount, int verticalAreaCount){
        var areaWidth = (float)Board.BOARD_SIDE_LENGTH/horizontalAreaCount;
        var areaHeight = (float)Board.BOARD_SIDE_LENGTH/verticalAreaCount;

        Board.Instance.cellAreaMap = new HashMap<>();
        for(var y = 0; y != horizontalAreaCount; y++){
            for(var x = 0; x != verticalAreaCount; x++){
                var area = new Area();
                area.cells = new HashSet();
                Board.Instance.areas.add(area);
                var min = new Integer2((int) (x * areaWidth), (int) (y * areaHeight));
                var max = new Integer2((int) ((x+1) * areaWidth - 1), (int) ((y+1) * areaHeight - 1));

                for(var cy = min.y; cy <= max.y; cy++){
                    for(var cx = min.x; cx <= max.x; cx++){
                        var pos = new Integer2(cx, cy);
                        area.cells.add(pos);
                        Board.Instance.cellAreaMap.put(pos, area);
                    }
                }
            }
        }
    }

    private static void SetStaticWalls(Area area){
        Board.Instance.staticWalls = area;
        Board.Instance.areas.add(area);

        var iterator = area.cells.iterator();
        while(iterator.hasNext()){
            Board.Instance.RemapCellToArea(iterator.next(), area);
        }
    }

    private static void TestSplit(int horizontalAreaCount, int verticalAreaCount){
        new Game();
        SetAreas(verticalAreaCount,horizontalAreaCount);

        var expectedResult = verticalAreaCount * horizontalAreaCount;
        EqualTest(Board.Instance.GetAreaCount(), expectedResult, "SIMPLE SPLIT");
    }

    private static void TestBotMove(){
        for(var p = 0; p != Game.Instance.player.length; p++){
            Debug.Log("Player == " + p);
            Game.Instance.player[p] = new Player(p);
            Game.Instance.player[p].playerNumber = p;
            for(var b = 0; b != Game.Instance.player[0].bots.length; b++){
                Debug.Log("Bot == " + b);
                var game = new Game();
                SetAreas(1, 2);

                for(var i = 0; i != 3; i++){
                    game.player[i] = new Player(i);
                    game.player[i].playerNumber = i;
                }
                game.player[p].bots[b] = new Bot(new Integer2(Board.BOARD_SIDE_LENGTH/2 - 1, 0), p);

                var update = new Update();
                update.bot = b;
                update.player = p;
                update.x = (int) (Board.BOARD_SIDE_LENGTH/Board.SCALE/2);
                update.y = 0;

                EqualTest(game.player[p].bots[b].pos, new Integer2(Board.BOARD_SIDE_LENGTH/2 - 1,0),"PreUpdateBotPos");
                EqualTest(game.player[p].bots[b].crrArea, Board.Instance.cellAreaMap.get(new Integer2(0,0)),"PreUpdateArea");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0,0)).occupation,-1,"PreUpdateArea0Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(Board.BOARD_SIDE_LENGTH - 1,0)).occupation, -1,"PreUpdateArea1Occupation");

                var startTime = System.nanoTime();
                game.Update(update);
                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;

                EqualTest(game.player[p].bots[b].pos, new Integer2(update.x, update.y),"PostUpdateBotPos");
                EqualTest(game.player[p].bots[b].crrArea, Board.Instance.cellAreaMap.get(new Integer2(Board.BOARD_SIDE_LENGTH - 1,0)),"PostUpdateArea");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(Board.BOARD_SIDE_LENGTH - 1,0)).occupation, p,"PostUpdateArea1Occupation");

                SmallerEqualTest(executionTime/1000000000f, 0.003f, "UpdateDurationTest");
            }
        }
    }

    private static void TotalTest(){
        var game = new Game();
        SetAreas(3,3);

        
        var areaSideLength = Board.BOARD_SIDE_LENGTH / 3f;
        var trueAreaSideLength = Board.DEFAULT_BOARD_SIDE_LENGTH / 3f;
        //INIT

        Game.Instance.player[0] = new Player(0);
        Game.Instance.player[0].bots[0] = new Bot(new Integer2(0, (int) (areaSideLength * .5f)), 0);
        Game.Instance.player[0].bots[1] = new Bot(new Integer2(0,(int) (areaSideLength * 1.5f)), 0);
        Game.Instance.player[0].bots[2] = new Bot(new Integer2(0,(int) (areaSideLength * 2.5f)), 0);
        Board.Instance.areas.add(Game.Instance.player[0].walls);
        Game.Instance.player[0].walls.cells.add(Game.Instance.player[0].bots[0].pos);
        Game.Instance.player[0].walls.cells.add(Game.Instance.player[0].bots[1].pos);
        Game.Instance.player[0].walls.cells.add(Game.Instance.player[0].bots[2].pos);
        Board.Instance.RemapCellToArea(Game.Instance.player[0].bots[0].pos, Game.Instance.player[0].walls);
        Board.Instance.RemapCellToArea(Game.Instance.player[0].bots[1].pos, Game.Instance.player[0].walls);
        Board.Instance.RemapCellToArea(Game.Instance.player[0].bots[2].pos, Game.Instance.player[0].walls);
        Game.Instance.player[1] = new Player(1);
        Game.Instance.player[1].bots[0] = new Bot(new Integer2((int) (areaSideLength/3f),0), 1);
        Game.Instance.player[1].bots[1] = new Bot(new Integer2((int) (areaSideLength + (int) (areaSideLength * 2f / 3f)),0), 1);
        Game.Instance.player[1].bots[2] = new Bot(new Integer2((int) (2 * areaSideLength + (int) (areaSideLength / 3f)),0), 1);
        Board.Instance.areas.add(Game.Instance.player[1].walls);
        Game.Instance.player[1].walls.cells.add(Game.Instance.player[1].bots[0].pos);
        Game.Instance.player[1].walls.cells.add(Game.Instance.player[1].bots[1].pos);
        Game.Instance.player[1].walls.cells.add(Game.Instance.player[1].bots[2].pos);
        Board.Instance.RemapCellToArea(Game.Instance.player[1].bots[0].pos, Game.Instance.player[1].walls);
        Board.Instance.RemapCellToArea(Game.Instance.player[1].bots[1].pos, Game.Instance.player[1].walls);
        Board.Instance.RemapCellToArea(Game.Instance.player[1].bots[2].pos, Game.Instance.player[1].walls);

        Game.Instance.player[2] = new Player(2);
        Game.Instance.player[2].bots[0] = new Bot(new Integer2((int) (areaSideLength * 2f / 3f), Board.BOARD_SIDE_LENGTH - 1), 2);
        Game.Instance.player[2].bots[1] = new Bot(new Integer2((int) (areaSideLength + (int) (areaSideLength / 3f)), Board.BOARD_SIDE_LENGTH - 1), 2);
        Game.Instance.player[2].bots[2] = new Bot(new Integer2(Board.BOARD_SIDE_LENGTH - (int) (areaSideLength / 3f), Board.BOARD_SIDE_LENGTH - 1), 2);
        Board.Instance.areas.add(Game.Instance.player[2].walls);
        Game.Instance.player[2].walls.cells.add(Game.Instance.player[2].bots[0].pos);
        Game.Instance.player[2].walls.cells.add(Game.Instance.player[2].bots[1].pos);
        Game.Instance.player[2].walls.cells.add(Game.Instance.player[2].bots[2].pos);
        Board.Instance.RemapCellToArea(Game.Instance.player[2].bots[0].pos, Game.Instance.player[2].walls);
        Board.Instance.RemapCellToArea(Game.Instance.player[2].bots[1].pos, Game.Instance.player[2].walls);
        Board.Instance.RemapCellToArea(Game.Instance.player[2].bots[2].pos, Game.Instance.player[2].walls);

        //Static Walls
        var staticWalls = new Area();
        for(var x = (int) (areaSideLength/2f); x < areaSideLength * 1.5f; x++){
            staticWalls.cells.add(new Integer2(x, (int) (areaSideLength * (2f+2f/3f))));
        }

        SetStaticWalls(staticWalls);

        EqualTest(Board.Instance.GetAreaCount(), 9, "AreaCountCheck");

        var botsAlive = new boolean[]{true, true, true, true, true, true, true, true, true};
        //Updates:

        for(var frame = 1; CheckIfAnyTrue(botsAlive); frame++){
            var updateP0B0 = new Update();
            updateP0B0.player = 0;
            updateP0B0.bot = 0;
            updateP0B0.x = frame;
            updateP0B0.y = (int) (trueAreaSideLength * 0.5f);

            var updateP0B1 = new Update();
            updateP0B1.player = 0;
            updateP0B1.bot = 1;
            updateP0B1.x = Math.max(frame - 16, 0);
            updateP0B1.y = (int) (trueAreaSideLength * 1.5f);

            var updateP0B2 = new Update();
            updateP0B2.player = 0;
            updateP0B2.bot = 2;
            updateP0B2.x = Math.max(frame - 32, 0);
            updateP0B2.y = (int) (trueAreaSideLength * 2.5f);


            var updateP1B0 = new Update();
            updateP1B0.player = 1;
            updateP1B0.bot = 0;
            updateP1B0.x = (int) (trueAreaSideLength/3f);
            updateP1B0.y = Math.max(frame - 16, 0);

            var updateP1B1 = new Update();
            updateP1B1.player = 1;
            updateP1B1.bot = 1;
            updateP1B1.x = (int) (trueAreaSideLength + (int) (trueAreaSideLength * 2f / 3f));
            updateP1B1.y = frame;

            var updateP1B2 = new Update();
            updateP1B2.player = 1;
            updateP1B2.bot = 2;
            updateP1B2.x = (int) (2 * trueAreaSideLength + (int) (trueAreaSideLength / 3f));
            updateP1B2.y = Math.max(frame - 16, 0);;

            var updateP2B0 = new Update();
            updateP2B0.player = 2;
            updateP2B0.bot = 0;
            updateP2B0.x = (int) (trueAreaSideLength * 2f / 3f);
            updateP2B0.y = Board.DEFAULT_BOARD_SIDE_LENGTH - frame;

            var updateP2B1 = new Update();
            updateP2B1.player = 2;
            updateP2B1.bot = 1;
            updateP2B1.x = (int) (trueAreaSideLength + (int) (trueAreaSideLength / 3f));
            updateP2B1.y = Board.DEFAULT_BOARD_SIDE_LENGTH - frame;

            var updateP2B2 = new Update();
            updateP2B2.player = 2;
            updateP2B2.bot = 2;
            updateP2B2.x = Board.DEFAULT_BOARD_SIDE_LENGTH - (int) (trueAreaSideLength / 3f);
            updateP2B2.y = Board.DEFAULT_BOARD_SIDE_LENGTH - frame;

            if(botsAlive[6] && (updateP2B0.y * Board.SCALE <= areaSideLength * (2f+2f/3f))){
                botsAlive[6] = false;

                EqualTest(Board.Instance.GetAreaCount(), 11, "Bot[2,0] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[2,0] Dead, Area[0,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, 0)).occupation, -1, "Bot[2,0] Dead, Area[1,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[2,0] Dead, Area[2,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) areaSideLength)).occupation, -1, "Bot[2,0] Dead, Area[0,1] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) areaSideLength)).occupation, -1, "Bot[2,0] Dead, Area[1,1] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, -1, "Bot[2,0] Dead, Area[2,1] Occupation");
                

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[2,0] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[2,0] Dead, Area2[0,2] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, -1, "Bot[2,0] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[2,0] Dead, Area2[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, -1, "Bot[2,0] Dead, Area[2,2] Occupation");
            }
            if(botsAlive[7] && (updateP2B1.y * Board.SCALE <= areaSideLength * (2f+2f/3f))){
                botsAlive[7] = false;

                EqualTest(Board.Instance.GetAreaCount(), 11, "Bot[2,1] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[2,1] Dead, Area[0,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, 0)).occupation, -1, "Bot[2,1] Dead, Area[1,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[2,1] Dead, Area[2,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) areaSideLength)).occupation, -1, "Bot[2,1] Dead, Area[0,1] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) areaSideLength)).occupation, -1, "Bot[2,1] Dead, Area[1,1] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, -1, "Bot[2,1] Dead, Area[2,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[2,1] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[2,1] Dead, Area2[0,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, -1, "Bot[2,1] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[2,1] Dead, Area2[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, -1, "Bot[2,1] Dead, Area[2,2] Occupation");
            }

            if(botsAlive[3] && (updateP1B0.y * Board.SCALE >= areaSideLength * 0.5f)){
                botsAlive[3] = false;
                
                EqualTest(Board.Instance.GetAreaCount(), 12, "Bot[1,0] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[1,0] Dead, Area1[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 0.5f + 1))).occupation, -1, "Bot[1,0] Dead, Area2[0,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, 0)).occupation, -1, "Bot[1,0] Dead, Area[0,1] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[1,0] Dead, Area[0,2] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) areaSideLength)).occupation, -1, "Bot[1,0] Dead, Area[1,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) areaSideLength)).occupation, -1, "Bot[1,0] Dead, Area[1,1] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, -1, "Bot[1,0] Dead, Area[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[1,0] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[1,0] Dead, Area2[0,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, -1, "Bot[1,0] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[1,0] Dead, Area2[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, -1, "Bot[1,0] Dead, Area[2,2] Occupation");
            }

            if(botsAlive[0] && (updateP0B0.x * Board.SCALE >= areaSideLength + (int) (areaSideLength * 2f / 3f))){
                botsAlive[0] = false;

                EqualTest(Board.Instance.GetAreaCount(), 18, "Bot[0,0] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[0,0] Dead, Area1[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 0.5f + 1))).occupation, -1, "Bot[0,0] Dead, Area2[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength/3f), 0)).occupation, -1, "Bot[0,0] Dead, Area3[0,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, 0)).occupation, 0, "Bot[0,0] Dead, Area1[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[0,0] Dead, Area2[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (1f + 2f/3f)) + 1, 0)).occupation, -1, "Bot[0,0] Dead, Area3[1,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[0,0] Dead, Area1[2,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, -1, "Bot[0,0] Dead, Area2[2,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) areaSideLength)).occupation, -1, "Bot[0,0] Dead, Area[0,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[0,0] Dead, Area[0,1] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) areaSideLength)).occupation, 0, "Bot[0,0] Dead, Area[1,1] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, 1, "Bot[0,0] Dead, Area[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[0,0] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[0,0] Dead, Area2[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[0,0] Dead, Area3[0,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, 0, "Bot[0,0] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[0,0] Dead, Area2[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, -1, "Bot[0,0] Dead, Area1[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 2f/3f)) + 1, (int) (areaSideLength * 2))).occupation, -1, "Bot[0,0] Dead, Area2[2,2] Occupation");
            }
            if(botsAlive[1] && (updateP0B1.x * Board.SCALE >= areaSideLength + (int) (areaSideLength * 2f / 3f))){
                botsAlive[1] = false;

                EqualTest(Board.Instance.GetAreaCount(), 19, "Bot[0,1] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[0,1] Dead, Area1[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 0.5f + 1))).occupation, -1, "Bot[0,1] Dead, Area2[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength/3f), 0)).occupation, -1, "Bot[0,1] Dead, Area3[0,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, 0)).occupation, 0, "Bot[0,1] Dead, Area1[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[0,1] Dead, Area2[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (1f + 2f/3f)) + 1, 0)).occupation, -1, "Bot[0,1] Dead, Area3[1,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[0,1] Dead, Area1[2,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, -1, "Bot[0,1] Dead, Area2[2,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) areaSideLength)).occupation, -1, "Bot[0,1] Dead, Area[0,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[0,1] Dead, Area[0,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) areaSideLength)).occupation, 0, "Bot[0,1] Dead, Area1[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 1.5f) + 1)).occupation, 0, "Bot[0,1] Dead, Area2[1,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, 1, "Bot[0,1] Dead, Area[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[0,1] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[0,1] Dead, Area2[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[0,1] Dead, Area3[0,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, 0, "Bot[0,1] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[0,1] Dead, Area2[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, -1, "Bot[0,1] Dead, Area1[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 2f/3f)) + 1, (int) (areaSideLength * 2))).occupation, -1, "Bot[0,1] Dead, Area2[2,2] Occupation");
            }

            if(botsAlive[4] && (updateP1B1.y * Board.SCALE >= (int) (areaSideLength * 2.5f))){
                botsAlive[4] = false;

                EqualTest(Board.Instance.GetAreaCount(), 25, "Bot[1,1] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[1,1] Dead, Area1[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 0.5f + 1))).occupation, -1, "Bot[1,1] Dead, Area2[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength/3f), 0)).occupation, -1, "Bot[1,1] Dead, Area3[0,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, 0)).occupation, 0, "Bot[1,1] Dead, Area1[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[1,1] Dead, Area2[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (1f + 2f/3f)) + 1, 0)).occupation, -1, "Bot[1,1] Dead, Area3[1,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[1,1] Dead, Area1[2,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, 2, "Bot[1,1] Dead, Area2[2,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) areaSideLength)).occupation, -1, "Bot[1,1] Dead, Area[0,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[1,1] Dead, Area[0,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) areaSideLength)).occupation, 0, "Bot[1,1] Dead, Area1[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 1.5f) + 1)).occupation, 0, "Bot[1,1] Dead, Area2[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (1f + 2f/3f)) + 1, (int) areaSideLength)).occupation, 0, "Bot[1,1] Dead, Area3[1,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, 1, "Bot[1,1] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 1f/3f) + 1), (int) areaSideLength)).occupation, 1, "Bot[1,1] Dead, Area2[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 2f/3f) + 1), (int) areaSideLength)).occupation, 1, "Bot[1,1] Dead, Area3[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[1,1] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[1,1] Dead, Area2[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[1,1] Dead, Area3[0,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, 1, "Bot[1,1] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[1,1] Dead, Area2[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 2.5f) + 1)).occupation, 0, "Bot[1,1] Dead, Area3[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (1f + 2f/3f) + 1), (int) (areaSideLength * 2))).occupation, 1, "Bot[1,1] Dead, Area3[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, 0, "Bot[1,1] Dead, Area1[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 2f/3f)) + 1, (int) (areaSideLength * 2))).occupation, -1, "Bot[1,1] Dead, Area2[2,2] Occupation");
            }

            if(botsAlive[5] && (updateP1B2.y * Board.SCALE >= (int) (areaSideLength * 2.5f))){
                botsAlive[5] = false;

                EqualTest(Board.Instance.GetAreaCount(), 26, "Bot[1,1] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[1,2] Dead, Area1[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 0.5f + 1))).occupation, -1, "Bot[1,2] Dead, Area2[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength/3f), 0)).occupation, -1, "Bot[1,2] Dead, Area3[0,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, 0)).occupation, 0, "Bot[1,2] Dead, Area1[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[1,2] Dead, Area2[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (1f + 2f/3f)) + 1, 0)).occupation, -1, "Bot[1,2] Dead, Area3[1,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[1,2] Dead, Area1[2,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, 2, "Bot[1,2] Dead, Area2[2,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) areaSideLength)).occupation, -1, "Bot[1,2] Dead, Area[0,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[1,2] Dead, Area[0,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) areaSideLength)).occupation, 0, "Bot[1,2] Dead, Area1[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 1.5f) + 1)).occupation, 0, "Bot[1,2] Dead, Area2[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (1f + 2f/3f)) + 1, (int) areaSideLength)).occupation, 0, "Bot[1,2] Dead, Area3[1,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, 1, "Bot[1,2] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 1f/3f) + 1), (int) areaSideLength)).occupation, 1, "Bot[1,2] Dead, Area2[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 2f/3f) + 1), (int) areaSideLength)).occupation, 1, "Bot[1,2] Dead, Area3[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[1,2] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[1,2] Dead, Area2[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[1,2] Dead, Area3[0,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, 1, "Bot[1,2] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[1,2] Dead, Area2[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 2.5f) + 1)).occupation, 0, "Bot[1,2] Dead, Area3[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (1f + 2f/3f) + 1), (int) (areaSideLength * 2))).occupation, 1, "Bot[1,2] Dead, Area3[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, 0, "Bot[1,2] Dead, Area1[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 2f/3f)) + 1, (int) (areaSideLength * 2))).occupation, -1, "Bot[1,2] Dead, Area2[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) (areaSideLength * 2.5f) + 1)).occupation, 0, "Bot[1,2] Dead, Area3[2,2] Occupation");
            }

            if(botsAlive[2] && (updateP0B2.x * Board.SCALE >= (int) (areaSideLength * (2f + 2f/3f)))){
                botsAlive[2] = false;

                EqualTest(Board.Instance.GetAreaCount(), 27, "Bot[1,1] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[0,2] Dead, Area1[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 0.5f + 1))).occupation, -1, "Bot[0,2] Dead, Area2[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength/3f), 0)).occupation, -1, "Bot[0,2] Dead, Area3[0,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, 0)).occupation, 0, "Bot[0,2] Dead, Area1[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[0,2] Dead, Area2[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (1f + 2f/3f)) + 1, 0)).occupation, -1, "Bot[0,2] Dead, Area3[1,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[0,2] Dead, Area1[2,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, 2, "Bot[0,2] Dead, Area2[2,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) areaSideLength)).occupation, -1, "Bot[0,2] Dead, Area[0,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[0,2] Dead, Area[0,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) areaSideLength)).occupation, 0, "Bot[0,2] Dead, Area1[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 1.5f) + 1)).occupation, 0, "Bot[0,2] Dead, Area2[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (1f + 2f/3f)) + 1, (int) areaSideLength)).occupation, 0, "Bot[0,2] Dead, Area3[1,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, 1, "Bot[0,2] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 1f/3f) + 1), (int) areaSideLength)).occupation, 1, "Bot[0,2] Dead, Area2[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 2f/3f) + 1), (int) areaSideLength)).occupation, 1, "Bot[0,2] Dead, Area3[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[0,2] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[0,2] Dead, Area2[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[0,2] Dead, Area3[0,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, 1, "Bot[0,2] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[0,2] Dead, Area2[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 2.5f) + 1)).occupation, 0, "Bot[0,2] Dead, Area3[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (1f + 2f/3f) + 1), (int) (areaSideLength * 2))).occupation, 1, "Bot[0,2] Dead, Area3[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, 0, "Bot[0,2] Dead, Area1[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 2f/3f)) + 1, (int) (areaSideLength * 2))).occupation, -1, "Bot[0,2] Dead, Area2[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) (areaSideLength * 2.5f) + 1)).occupation, 0, "Bot[0,2] Dead, Area3[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 1f/3f) + 1), (int) (areaSideLength * 2))).occupation, 0, "Bot[0,2] Dead, Area4[2,2] Occupation");
            }

            if(botsAlive[8] && (updateP2B2.y * Board.SCALE <= 0)){
                botsAlive[8] = false;

                EqualTest(Board.Instance.GetAreaCount(), 28, "Bot[1,1] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, 0)).occupation, -1, "Bot[2,2] Dead, Area1[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 0.5f + 1))).occupation, -1, "Bot[2,2] Dead, Area2[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength/3f), 0)).occupation, -1, "Bot[2,2] Dead, Area3[0,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, 0)).occupation, 0, "Bot[2,2] Dead, Area1[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[2,2] Dead, Area2[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (1f + 2f/3f)) + 1, 0)).occupation, -1, "Bot[2,2] Dead, Area3[1,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[2,2] Dead, Area1[2,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, 2, "Bot[2,2] Dead, Area2[2,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 2f/3f)) + 1,  0)).occupation, 2, "Bot[2,2] Dead, Area3[2,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) areaSideLength)).occupation, -1, "Bot[2,2] Dead, Area[0,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[2,2] Dead, Area[0,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) areaSideLength)).occupation, 0, "Bot[2,2] Dead, Area1[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 1.5f) + 1)).occupation, 0, "Bot[2,2] Dead, Area2[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (1f + 2f/3f)) + 1, (int) areaSideLength)).occupation, 0, "Bot[2,2] Dead, Area3[1,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, 1, "Bot[2,2] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 1f/3f) + 1), (int) areaSideLength)).occupation, 1, "Bot[2,2] Dead, Area2[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 2f/3f) + 1), (int) areaSideLength)).occupation, 1, "Bot[2,2] Dead, Area3[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[2,2] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[2,2] Dead, Area2[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2(0, (int) (areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[2,2] Dead, Area3[0,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, 1, "Bot[2,2] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[2,2] Dead, Area2[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) areaSideLength, (int) (areaSideLength * 2.5f) + 1)).occupation, 0, "Bot[2,2] Dead, Area3[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (1f + 2f/3f) + 1), (int) (areaSideLength * 2))).occupation, 1, "Bot[2,2] Dead, Area3[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, 0, "Bot[2,2] Dead, Area1[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 2f/3f)) + 1, (int) (areaSideLength * 2))).occupation, -1, "Bot[2,2] Dead, Area2[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * 2), (int) (areaSideLength * 2.5f) + 1)).occupation, 0, "Bot[2,2] Dead, Area3[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new Integer2((int) (areaSideLength * (2f + 1f/3f) + 1), (int) (areaSideLength * 2))).occupation, 0, "Bot[2,2] Dead, Area4[2,2] Occupation");
            }

            if(botsAlive[0])
                game.Update(updateP0B0);
            if(botsAlive[1])
                game.Update(updateP0B1);
            if(botsAlive[2])
                game.Update(updateP0B2);

            if(botsAlive[3])
                game.Update(updateP1B0);
            if(botsAlive[4])
                game.Update(updateP1B1);
            if(botsAlive[5])
                game.Update(updateP1B2);

            if(botsAlive[6])
                game.Update(updateP2B0);
            if(botsAlive[7])
                game.Update(updateP2B1);
            if(botsAlive[8])
                game.Update(updateP2B2);

            EqualTest(Game.Instance.player[0].bots[0].alive, botsAlive[0], "[0,0] Alive Check");
            EqualTest(Game.Instance.player[0].bots[1].alive, botsAlive[1], "[0,1] Alive Check");
            EqualTest(Game.Instance.player[0].bots[2].alive, botsAlive[2], "[0,2] Alive Check");
            EqualTest(Game.Instance.player[1].bots[0].alive, botsAlive[3], "[1,0] Alive Check");
            EqualTest(Game.Instance.player[1].bots[1].alive, botsAlive[4], "[1,1] Alive Check");
            EqualTest(Game.Instance.player[1].bots[2].alive, botsAlive[5], "[1,2] Alive Check");
            EqualTest(Game.Instance.player[2].bots[0].alive, botsAlive[6], "[2,0] Alive Check");
            EqualTest(Game.Instance.player[2].bots[1].alive, botsAlive[7], "[2,1] Alive Check");
            EqualTest(Game.Instance.player[2].bots[2].alive, botsAlive[8], "[2,2] Alive Check");
        }
    }

    public static void TestPathConverter(){

        var testPath = new ArrayList<Integer2>();
        var i = 0;
        testPath.add(i++, new Integer2(50, 50));
        testPath.add(i++, new Integer2(51, 50));
        testPath.add(i++, new Integer2(51, 51));

        EqualTest(PathSystem.ConvertPath(testPath).peek().value, 51, "H -> D");

        i = 0;
        testPath.clear();
        testPath.add(i++, new Integer2(50, 50));
        testPath.add(i++, new Integer2(51, 50));
        testPath.add(i++, new Integer2(51, 49));

        EqualTest(PathSystem.ConvertPath(testPath).peek().value, -51, "H -> U");

        i = 0;
        testPath.clear();
        testPath.add(i++, new Integer2(50, 50));
        testPath.add(i++, new Integer2(49, 50));
        testPath.add(i++, new Integer2(49, 51));

        EqualTest(PathSystem.ConvertPath(testPath).peek().value, -49, "!H -> D");

        i = 0;
        testPath.clear();
        testPath.add(i++, new Integer2(50, 50));
        testPath.add(i++, new Integer2(49, 50));
        testPath.add(i++, new Integer2(49, 49));

        EqualTest(PathSystem.ConvertPath(testPath).peek().value, 49, "!H -> U");

        i = 0;
        testPath.add(i++, new Integer2(50, 50));
        testPath.add(i++, new Integer2(50, 49));
        testPath.add(i++, new Integer2(49, 49));

        EqualTest(PathSystem.ConvertPath(testPath).peek().value, -49, "V -> L");

        i = 0;
        testPath.clear();
        testPath.add(i++, new Integer2(50, 50));
        testPath.add(i++, new Integer2(50, 49));
        testPath.add(i++, new Integer2(51, 49));

        EqualTest(PathSystem.ConvertPath(testPath).peek().value, 49, "V -> R");

        i = 0;
        testPath.clear();
        testPath.add(i++, new Integer2(50, 50));
        testPath.add(i++, new Integer2(50, 51));
        testPath.add(i++, new Integer2(49, 51));

        EqualTest(PathSystem.ConvertPath(testPath).peek().value, 51, "!V -> L");

        i = 0;
        testPath.clear();
        testPath.add(i++, new Integer2(50, 50));
        testPath.add(i++, new Integer2(50, 51));
        testPath.add(i++, new Integer2(51, 51));

        EqualTest(PathSystem.ConvertPath(testPath).peek().value, -51, "!V -> R");

    }

    private static String FormatBool(boolean value, String postfix){
        return (value? ConsoleColors.GREEN_BOLD + "TRUE" : ConsoleColors.RED_BOLD + "FALSE") + ConsoleColors.RESET + postfix;
    }

    public static <T> void EqualTest(T value, T expectedValue, String testName){
        var bool = value.equals(expectedValue);
        var result = FormatBool(bool, ": " + testName + " [" + value + "/" + expectedValue + "]");
        if(!bool)
            Debug.Log(result);
    }

    public static <T> void SmallerEqualTest(Comparable<T> value, T max, String testName) {
        var bool = value.compareTo(max) <= 0;
        var result = FormatBool(bool, ": " + testName + " [" + value + "/" + max + "]");
        if(!bool)
            Debug.Log(result);
    }

    public static boolean CheckIfAnyTrue(boolean[] arr) {
        for (boolean value : arr) {
            if (value) {
                return true;
            }
        }
        return false;
    }

    public static void PrintPath(ArrayList<Integer2> path, Data.Board board){
        var image = new BufferedImage(Data.Board.BOARD_SIDE_LENGTH, Data.Board.BOARD_SIDE_LENGTH, BufferedImage.TYPE_INT_RGB);

        for (var y = 0; y < Data.Board.BOARD_SIDE_LENGTH; y++) {
            for (var x = 0; x < Data.Board.BOARD_SIDE_LENGTH; x++) {
                int colorWhite = 0xFFFFFF;
                image.setRGB(x, y, colorWhite);
            }
        }

        for(var cell : path)
            image.setRGB(cell.x, cell.y, 0x999999);

        for(var cell : board.staticWalls.cells)
            image.setRGB(cell.x, cell.y, 0);

        for(var cell : (HashSet<Integer2>)board.playerWalls[0].cells.clone())
            image.setRGB(cell.x, cell.y, 0xFF0000);
        for(var cell : (HashSet<Integer2>)board.playerWalls[1].cells.clone())
            image.setRGB(cell.x, cell.y, 0x00FF00);
        for(var cell : (HashSet<Integer2>)board.playerWalls[2].cells.clone())
            image.setRGB(cell.x, cell.y, 0x0000FF);


        var outputFile = new File("output.png");
        try {
            ImageIO.write(image, "png", outputFile);
            System.out.println("Bild erfolgreich erstellt: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Fehler beim Speichern des Bildes: " + e.getMessage());
        }
    }
}
