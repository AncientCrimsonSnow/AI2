package Testing;

import Core.*;
import Systems.BoardSystem;
import Systems.GameSystem;
import Utils.ConsoleColors;
import Utils.Debug;
import Utils.int2;
import lenz.htw.duktus.net.Update;

import java.util.HashMap;
import java.util.HashSet;

public class Tester {

    public static void main(String[] args) {
        var game = GameSystem.InitGame(new MyClient());
        TestInit(game);
    }

    private static void TestInit(Data.Game game){
        for(var p = 0; p != Data.Player.PLAYER_COUNT; p++){
            for(var b = 0; b != Data.Bot.BOT_COUNT; b++){
                EqualTest(game.player[p].bots[b].pos, TestData.TEST_BOT_POS[p][b].Multiply(Data.Board.SCALE), String.format("Player[%d], Bot[%d] Position", p, b));
                var oldAreaCell = TestData.TEST_BOT_POS[p][b].Multiply(Board.SCALE);
                oldAreaCell.x++;
                EqualTest(game.board.botCrrAreas[p][b], game.board.cellAreaMap.get(oldAreaCell), String.format("Player[%d], Bot[%d] Area", p, b));
            }
        }
        EqualTest(BoardSystem.GetAreaCount(game.board), 10, "Init Area Count");

        var areaCellCount = 0;
        for(var area : game.board.areas)
            areaCellCount += area.cells.size();

        EqualTest(areaCellCount, game.board.cellAreaMap.size() - game.board.staticWalls.cells.size(), "Cell Count Init Check");
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
                var min = new int2((int) (x * areaWidth), (int) (y * areaHeight));
                var max = new int2((int) ((x+1) * areaWidth - 1), (int) ((y+1) * areaHeight - 1));

                for(var cy = min.y; cy <= max.y; cy++){
                    for(var cx = min.x; cx <= max.x; cx++){
                        var pos = new int2(cx, cy);
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
                game.player[p].bots[b] = new Bot(new int2(Board.BOARD_SIDE_LENGTH/2 - 1, 0), p);

                var update = new Update();
                update.bot = b;
                update.player = p;
                update.x = (int) (Board.BOARD_SIDE_LENGTH/Board.SCALE/2);
                update.y = 0;

                EqualTest(game.player[p].bots[b].pos, new int2(Board.BOARD_SIDE_LENGTH/2 - 1,0),"PreUpdateBotPos");
                EqualTest(game.player[p].bots[b].crrArea, Board.Instance.cellAreaMap.get(new int2(0,0)),"PreUpdateArea");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0,0)).occupation,-1,"PreUpdateArea0Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(Board.BOARD_SIDE_LENGTH - 1,0)).occupation, -1,"PreUpdateArea1Occupation");

                var startTime = System.nanoTime();
                game.Update(update);
                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;

                EqualTest(game.player[p].bots[b].pos, new int2(update.x, update.y),"PostUpdateBotPos");
                EqualTest(game.player[p].bots[b].crrArea, Board.Instance.cellAreaMap.get(new int2(Board.BOARD_SIDE_LENGTH - 1,0)),"PostUpdateArea");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(Board.BOARD_SIDE_LENGTH - 1,0)).occupation, p,"PostUpdateArea1Occupation");

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
        Game.Instance.player[0].bots[0] = new Bot(new int2(0, (int) (areaSideLength * .5f)), 0);
        Game.Instance.player[0].bots[1] = new Bot(new int2(0,(int) (areaSideLength * 1.5f)), 0);
        Game.Instance.player[0].bots[2] = new Bot(new int2(0,(int) (areaSideLength * 2.5f)), 0);
        Board.Instance.areas.add(Game.Instance.player[0].walls);
        Game.Instance.player[0].walls.cells.add(Game.Instance.player[0].bots[0].pos);
        Game.Instance.player[0].walls.cells.add(Game.Instance.player[0].bots[1].pos);
        Game.Instance.player[0].walls.cells.add(Game.Instance.player[0].bots[2].pos);
        Board.Instance.RemapCellToArea(Game.Instance.player[0].bots[0].pos, Game.Instance.player[0].walls);
        Board.Instance.RemapCellToArea(Game.Instance.player[0].bots[1].pos, Game.Instance.player[0].walls);
        Board.Instance.RemapCellToArea(Game.Instance.player[0].bots[2].pos, Game.Instance.player[0].walls);
        Game.Instance.player[1] = new Player(1);
        Game.Instance.player[1].bots[0] = new Bot(new int2((int) (areaSideLength/3f),0), 1);
        Game.Instance.player[1].bots[1] = new Bot(new int2((int) (areaSideLength + (int) (areaSideLength * 2f / 3f)),0), 1);
        Game.Instance.player[1].bots[2] = new Bot(new int2((int) (2 * areaSideLength + (int) (areaSideLength / 3f)),0), 1);
        Board.Instance.areas.add(Game.Instance.player[1].walls);
        Game.Instance.player[1].walls.cells.add(Game.Instance.player[1].bots[0].pos);
        Game.Instance.player[1].walls.cells.add(Game.Instance.player[1].bots[1].pos);
        Game.Instance.player[1].walls.cells.add(Game.Instance.player[1].bots[2].pos);
        Board.Instance.RemapCellToArea(Game.Instance.player[1].bots[0].pos, Game.Instance.player[1].walls);
        Board.Instance.RemapCellToArea(Game.Instance.player[1].bots[1].pos, Game.Instance.player[1].walls);
        Board.Instance.RemapCellToArea(Game.Instance.player[1].bots[2].pos, Game.Instance.player[1].walls);

        Game.Instance.player[2] = new Player(2);
        Game.Instance.player[2].bots[0] = new Bot(new int2((int) (areaSideLength * 2f / 3f), Board.BOARD_SIDE_LENGTH - 1), 2);
        Game.Instance.player[2].bots[1] = new Bot(new int2((int) (areaSideLength + (int) (areaSideLength / 3f)), Board.BOARD_SIDE_LENGTH - 1), 2);
        Game.Instance.player[2].bots[2] = new Bot(new int2(Board.BOARD_SIDE_LENGTH - (int) (areaSideLength / 3f), Board.BOARD_SIDE_LENGTH - 1), 2);
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
            staticWalls.cells.add(new int2(x, (int) (areaSideLength * (2f+2f/3f))));
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

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, 0)).occupation, -1, "Bot[2,0] Dead, Area[0,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, 0)).occupation, -1, "Bot[2,0] Dead, Area[1,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[2,0] Dead, Area[2,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) areaSideLength)).occupation, -1, "Bot[2,0] Dead, Area[0,1] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) areaSideLength)).occupation, -1, "Bot[2,0] Dead, Area[1,1] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, -1, "Bot[2,0] Dead, Area[2,1] Occupation");
                

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[2,0] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[2,0] Dead, Area2[0,2] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, -1, "Bot[2,0] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[2,0] Dead, Area2[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, -1, "Bot[2,0] Dead, Area[2,2] Occupation");
            }
            if(botsAlive[7] && (updateP2B1.y * Board.SCALE <= areaSideLength * (2f+2f/3f))){
                botsAlive[7] = false;

                EqualTest(Board.Instance.GetAreaCount(), 11, "Bot[2,1] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, 0)).occupation, -1, "Bot[2,1] Dead, Area[0,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, 0)).occupation, -1, "Bot[2,1] Dead, Area[1,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[2,1] Dead, Area[2,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) areaSideLength)).occupation, -1, "Bot[2,1] Dead, Area[0,1] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) areaSideLength)).occupation, -1, "Bot[2,1] Dead, Area[1,1] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, -1, "Bot[2,1] Dead, Area[2,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[2,1] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[2,1] Dead, Area2[0,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, -1, "Bot[2,1] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[2,1] Dead, Area2[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, -1, "Bot[2,1] Dead, Area[2,2] Occupation");
            }

            if(botsAlive[3] && (updateP1B0.y * Board.SCALE >= areaSideLength * 0.5f)){
                botsAlive[3] = false;
                
                EqualTest(Board.Instance.GetAreaCount(), 12, "Bot[1,0] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, 0)).occupation, -1, "Bot[1,0] Dead, Area1[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 0.5f + 1))).occupation, -1, "Bot[1,0] Dead, Area2[0,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, 0)).occupation, -1, "Bot[1,0] Dead, Area[0,1] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[1,0] Dead, Area[0,2] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) areaSideLength)).occupation, -1, "Bot[1,0] Dead, Area[1,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) areaSideLength)).occupation, -1, "Bot[1,0] Dead, Area[1,1] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, -1, "Bot[1,0] Dead, Area[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[1,0] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[1,0] Dead, Area2[0,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, -1, "Bot[1,0] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[1,0] Dead, Area2[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, -1, "Bot[1,0] Dead, Area[2,2] Occupation");
            }

            if(botsAlive[0] && (updateP0B0.x * Board.SCALE >= areaSideLength + (int) (areaSideLength * 2f / 3f))){
                botsAlive[0] = false;

                EqualTest(Board.Instance.GetAreaCount(), 18, "Bot[0,0] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, 0)).occupation, -1, "Bot[0,0] Dead, Area1[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 0.5f + 1))).occupation, -1, "Bot[0,0] Dead, Area2[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength/3f), 0)).occupation, -1, "Bot[0,0] Dead, Area3[0,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, 0)).occupation, 0, "Bot[0,0] Dead, Area1[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[0,0] Dead, Area2[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (1f + 2f/3f)) + 1, 0)).occupation, -1, "Bot[0,0] Dead, Area3[1,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[0,0] Dead, Area1[2,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, -1, "Bot[0,0] Dead, Area2[2,0] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) areaSideLength)).occupation, -1, "Bot[0,0] Dead, Area[0,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[0,0] Dead, Area[0,1] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) areaSideLength)).occupation, 0, "Bot[0,0] Dead, Area[1,1] Occupation");
                
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, 1, "Bot[0,0] Dead, Area[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[0,0] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[0,0] Dead, Area2[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[0,0] Dead, Area3[0,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, 0, "Bot[0,0] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[0,0] Dead, Area2[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, -1, "Bot[0,0] Dead, Area1[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 2f/3f)) + 1, (int) (areaSideLength * 2))).occupation, -1, "Bot[0,0] Dead, Area2[2,2] Occupation");
            }
            if(botsAlive[1] && (updateP0B1.x * Board.SCALE >= areaSideLength + (int) (areaSideLength * 2f / 3f))){
                botsAlive[1] = false;

                EqualTest(Board.Instance.GetAreaCount(), 19, "Bot[0,1] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, 0)).occupation, -1, "Bot[0,1] Dead, Area1[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 0.5f + 1))).occupation, -1, "Bot[0,1] Dead, Area2[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength/3f), 0)).occupation, -1, "Bot[0,1] Dead, Area3[0,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, 0)).occupation, 0, "Bot[0,1] Dead, Area1[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[0,1] Dead, Area2[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (1f + 2f/3f)) + 1, 0)).occupation, -1, "Bot[0,1] Dead, Area3[1,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[0,1] Dead, Area1[2,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, -1, "Bot[0,1] Dead, Area2[2,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) areaSideLength)).occupation, -1, "Bot[0,1] Dead, Area[0,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[0,1] Dead, Area[0,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) areaSideLength)).occupation, 0, "Bot[0,1] Dead, Area1[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 1.5f) + 1)).occupation, 0, "Bot[0,1] Dead, Area2[1,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, 1, "Bot[0,1] Dead, Area[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[0,1] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[0,1] Dead, Area2[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[0,1] Dead, Area3[0,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, 0, "Bot[0,1] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[0,1] Dead, Area2[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, -1, "Bot[0,1] Dead, Area1[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 2f/3f)) + 1, (int) (areaSideLength * 2))).occupation, -1, "Bot[0,1] Dead, Area2[2,2] Occupation");
            }

            if(botsAlive[4] && (updateP1B1.y * Board.SCALE >= (int) (areaSideLength * 2.5f))){
                botsAlive[4] = false;

                EqualTest(Board.Instance.GetAreaCount(), 25, "Bot[1,1] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, 0)).occupation, -1, "Bot[1,1] Dead, Area1[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 0.5f + 1))).occupation, -1, "Bot[1,1] Dead, Area2[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength/3f), 0)).occupation, -1, "Bot[1,1] Dead, Area3[0,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, 0)).occupation, 0, "Bot[1,1] Dead, Area1[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[1,1] Dead, Area2[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (1f + 2f/3f)) + 1, 0)).occupation, -1, "Bot[1,1] Dead, Area3[1,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[1,1] Dead, Area1[2,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, 2, "Bot[1,1] Dead, Area2[2,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) areaSideLength)).occupation, -1, "Bot[1,1] Dead, Area[0,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[1,1] Dead, Area[0,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) areaSideLength)).occupation, 0, "Bot[1,1] Dead, Area1[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 1.5f) + 1)).occupation, 0, "Bot[1,1] Dead, Area2[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (1f + 2f/3f)) + 1, (int) areaSideLength)).occupation, 0, "Bot[1,1] Dead, Area3[1,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, 1, "Bot[1,1] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 1f/3f) + 1), (int) areaSideLength)).occupation, 1, "Bot[1,1] Dead, Area2[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 2f/3f) + 1), (int) areaSideLength)).occupation, 1, "Bot[1,1] Dead, Area3[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[1,1] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[1,1] Dead, Area2[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[1,1] Dead, Area3[0,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, 1, "Bot[1,1] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[1,1] Dead, Area2[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 2.5f) + 1)).occupation, 0, "Bot[1,1] Dead, Area3[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (1f + 2f/3f) + 1), (int) (areaSideLength * 2))).occupation, 1, "Bot[1,1] Dead, Area3[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, 0, "Bot[1,1] Dead, Area1[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 2f/3f)) + 1, (int) (areaSideLength * 2))).occupation, -1, "Bot[1,1] Dead, Area2[2,2] Occupation");
            }

            if(botsAlive[5] && (updateP1B2.y * Board.SCALE >= (int) (areaSideLength * 2.5f))){
                botsAlive[5] = false;

                EqualTest(Board.Instance.GetAreaCount(), 26, "Bot[1,1] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, 0)).occupation, -1, "Bot[1,2] Dead, Area1[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 0.5f + 1))).occupation, -1, "Bot[1,2] Dead, Area2[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength/3f), 0)).occupation, -1, "Bot[1,2] Dead, Area3[0,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, 0)).occupation, 0, "Bot[1,2] Dead, Area1[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[1,2] Dead, Area2[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (1f + 2f/3f)) + 1, 0)).occupation, -1, "Bot[1,2] Dead, Area3[1,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[1,2] Dead, Area1[2,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, 2, "Bot[1,2] Dead, Area2[2,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) areaSideLength)).occupation, -1, "Bot[1,2] Dead, Area[0,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[1,2] Dead, Area[0,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) areaSideLength)).occupation, 0, "Bot[1,2] Dead, Area1[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 1.5f) + 1)).occupation, 0, "Bot[1,2] Dead, Area2[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (1f + 2f/3f)) + 1, (int) areaSideLength)).occupation, 0, "Bot[1,2] Dead, Area3[1,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, 1, "Bot[1,2] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 1f/3f) + 1), (int) areaSideLength)).occupation, 1, "Bot[1,2] Dead, Area2[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 2f/3f) + 1), (int) areaSideLength)).occupation, 1, "Bot[1,2] Dead, Area3[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[1,2] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[1,2] Dead, Area2[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[1,2] Dead, Area3[0,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, 1, "Bot[1,2] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[1,2] Dead, Area2[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 2.5f) + 1)).occupation, 0, "Bot[1,2] Dead, Area3[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (1f + 2f/3f) + 1), (int) (areaSideLength * 2))).occupation, 1, "Bot[1,2] Dead, Area3[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, 0, "Bot[1,2] Dead, Area1[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 2f/3f)) + 1, (int) (areaSideLength * 2))).occupation, -1, "Bot[1,2] Dead, Area2[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) (areaSideLength * 2.5f) + 1)).occupation, 0, "Bot[1,2] Dead, Area3[2,2] Occupation");
            }

            if(botsAlive[2] && (updateP0B2.x * Board.SCALE >= (int) (areaSideLength * (2f + 2f/3f)))){
                botsAlive[2] = false;

                EqualTest(Board.Instance.GetAreaCount(), 27, "Bot[1,1] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, 0)).occupation, -1, "Bot[0,2] Dead, Area1[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 0.5f + 1))).occupation, -1, "Bot[0,2] Dead, Area2[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength/3f), 0)).occupation, -1, "Bot[0,2] Dead, Area3[0,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, 0)).occupation, 0, "Bot[0,2] Dead, Area1[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[0,2] Dead, Area2[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (1f + 2f/3f)) + 1, 0)).occupation, -1, "Bot[0,2] Dead, Area3[1,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[0,2] Dead, Area1[2,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, 2, "Bot[0,2] Dead, Area2[2,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) areaSideLength)).occupation, -1, "Bot[0,2] Dead, Area[0,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[0,2] Dead, Area[0,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) areaSideLength)).occupation, 0, "Bot[0,2] Dead, Area1[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 1.5f) + 1)).occupation, 0, "Bot[0,2] Dead, Area2[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (1f + 2f/3f)) + 1, (int) areaSideLength)).occupation, 0, "Bot[0,2] Dead, Area3[1,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, 1, "Bot[0,2] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 1f/3f) + 1), (int) areaSideLength)).occupation, 1, "Bot[0,2] Dead, Area2[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 2f/3f) + 1), (int) areaSideLength)).occupation, 1, "Bot[0,2] Dead, Area3[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[0,2] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[0,2] Dead, Area2[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[0,2] Dead, Area3[0,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, 1, "Bot[0,2] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[0,2] Dead, Area2[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 2.5f) + 1)).occupation, 0, "Bot[0,2] Dead, Area3[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (1f + 2f/3f) + 1), (int) (areaSideLength * 2))).occupation, 1, "Bot[0,2] Dead, Area3[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, 0, "Bot[0,2] Dead, Area1[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 2f/3f)) + 1, (int) (areaSideLength * 2))).occupation, -1, "Bot[0,2] Dead, Area2[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) (areaSideLength * 2.5f) + 1)).occupation, 0, "Bot[0,2] Dead, Area3[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 1f/3f) + 1), (int) (areaSideLength * 2))).occupation, 0, "Bot[0,2] Dead, Area4[2,2] Occupation");
            }

            if(botsAlive[8] && (updateP2B2.y * Board.SCALE <= 0)){
                botsAlive[8] = false;

                EqualTest(Board.Instance.GetAreaCount(), 28, "Bot[1,1] AreaCountCheck");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, 0)).occupation, -1, "Bot[2,2] Dead, Area1[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 0.5f + 1))).occupation, -1, "Bot[2,2] Dead, Area2[0,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength/3f), 0)).occupation, -1, "Bot[2,2] Dead, Area3[0,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, 0)).occupation, 0, "Bot[2,2] Dead, Area1[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 0.5f) + 1)).occupation, 0, "Bot[2,2] Dead, Area2[1,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (1f + 2f/3f)) + 1, 0)).occupation, -1, "Bot[2,2] Dead, Area3[1,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), 0)).occupation, -1, "Bot[2,2] Dead, Area1[2,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 1f/3f)) + 1,  0)).occupation, 2, "Bot[2,2] Dead, Area2[2,0] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 2f/3f)) + 1,  0)).occupation, 2, "Bot[2,2] Dead, Area3[2,0] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) areaSideLength)).occupation, -1, "Bot[2,2] Dead, Area[0,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 1.5f) + 1)).occupation, -1, "Bot[2,2] Dead, Area[0,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) areaSideLength)).occupation, 0, "Bot[2,2] Dead, Area1[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 1.5f) + 1)).occupation, 0, "Bot[2,2] Dead, Area2[1,1] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (1f + 2f/3f)) + 1, (int) areaSideLength)).occupation, 0, "Bot[2,2] Dead, Area3[1,1] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) areaSideLength)).occupation, 1, "Bot[2,2] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 1f/3f) + 1), (int) areaSideLength)).occupation, 1, "Bot[2,2] Dead, Area2[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 2f/3f) + 1), (int) areaSideLength)).occupation, 1, "Bot[2,2] Dead, Area3[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 2))).occupation, -1, "Bot[2,2] Dead, Area1[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2f / 3f) + 1, (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[2,2] Dead, Area2[0,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2(0, (int) (areaSideLength * 2.5f) + 1)).occupation, -1, "Bot[2,2] Dead, Area3[0,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 2))).occupation, 1, "Bot[2,2] Dead, Area1[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength + (int) (areaSideLength / 3f)), (int) (areaSideLength * (2f+2f/3f)) + 1)).occupation, -1, "Bot[2,2] Dead, Area2[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) areaSideLength, (int) (areaSideLength * 2.5f) + 1)).occupation, 0, "Bot[2,2] Dead, Area3[1,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (1f + 2f/3f) + 1), (int) (areaSideLength * 2))).occupation, 1, "Bot[2,2] Dead, Area3[1,2] Occupation");

                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) (areaSideLength * 2))).occupation, 0, "Bot[2,2] Dead, Area1[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 2f/3f)) + 1, (int) (areaSideLength * 2))).occupation, -1, "Bot[2,2] Dead, Area2[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * 2), (int) (areaSideLength * 2.5f) + 1)).occupation, 0, "Bot[2,2] Dead, Area3[2,2] Occupation");
                EqualTest(Board.Instance.cellAreaMap.get(new int2((int) (areaSideLength * (2f + 1f/3f) + 1), (int) (areaSideLength * 2))).occupation, 0, "Bot[2,2] Dead, Area4[2,2] Occupation");
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

    private static String FormatBool(boolean value, String postfix){
        return (value? ConsoleColors.GREEN_BOLD + "TRUE" : ConsoleColors.RED_BOLD + "FALSE") + ConsoleColors.RESET + postfix;
    }

    private static <T> void EqualTest(T value, T expectedValue, String testName){
        var bool = value.equals(expectedValue);
        var result = FormatBool(bool, ": " + testName + " [" + value + "/" + expectedValue + "]");
        if(!bool)
            Debug.Log(result);
    }

    private static <T> void SmallerEqualTest(Comparable<T> value, T max, String testName) {
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
}
