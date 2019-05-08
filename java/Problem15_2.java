package adventofcode;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Problem15_2 {
  static PrintWriter writer;

  static void print(CharSequence s) {
    writer.print(s);
  }

  static void println(CharSequence s) {
    print(s + "\n");
  }



  static class Solver {
    static class PointXY implements Comparable<PointXY> {
      static class ReadingOrderComparator implements Comparator<PointXY> {
        static ReadingOrderComparator INSTANCE = new ReadingOrderComparator();

        @Override
        public int compare(PointXY o1, PointXY o2) {
          int byY = Integer.compare(o1.y, o2.y);
          return byY != 0 ? byY : Integer.compare(o1.x, o2.x);
        }
      }

      private static PointXY of(int x, int y) {
        return new PointXY(x, y);
      }

      final int x, y;
      private int hash;
      private boolean wasHashInit = false;

      private PointXY(int x, int y) {
        this.x = x;
        this.y = y;
      }

      @Override
      public boolean equals(Object obj) {
        if (!(obj instanceof PointXY))
          return false;
        PointXY p = (PointXY) obj;
        return x == p.x && y == p.y;
      }

      @Override
      public int hashCode() {
        if (!wasHashInit) {
          hash = 17;
          hash = 31 * hash + y;
          hash = 31 * hash + x;
          wasHashInit = true;
        }

        return hash;
      }

      @Override
      public int compareTo(PointXY o) {
        return ReadingOrderComparator.INSTANCE.compare(PointXY.this, o);
      }

      @Override
      public String toString() {
        return String.format("(%d, %d)", y, x);
      }
    }

    static class Player implements Comparable<Player> {
      static class ReadingOrderComparator implements Comparator<Player> {
        static ReadingOrderComparator INSTANCE = new ReadingOrderComparator();

        @Override
        public int compare(Player o1, Player o2) {
          int byY = Integer.compare(o1.y, o2.y);
          return byY != 0 ? byY : Integer.compare(o1.x, o2.x);
        }
      }

      static Player of(int type, int x, int y) {
        return new Player(type, x, y);
      }

      int type;
      int x, y;
      int hp;
      private int hash;
      private boolean wasHashInit = false;

      Player(int type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.hp = 200;
      }

      @Override
      public String toString() {
        return String.format("%c[%d][%d]", type == E ? 'E' : 'G', y, x);
      }

      @Override
      public int compareTo(Player o) {
        return ReadingOrderComparator.INSTANCE.compare(Player.this, o);
      }

      @Override
      public boolean equals(Object obj) {
        if (!(obj instanceof Player))
          return false;
        Player p = (Player) obj;
        return x == p.x && y == p.y;
      }

      @Override
      public int hashCode() {
        if (!wasHashInit) {
          hash = 17;
          hash = 31 * hash + y;
          hash = 31 * hash + x;
          wasHashInit = true;
        }

        return hash;
      }
    }

    static class Cell {
      Optional<Player> p;
      Set<Player> targetsForE, targetsForG; // Collection of adjacent opponents
      Set<PointXY> from; // Temporarily store during BFS where we might've come from to this cell

      Cell() {
        targetsForE = new HashSet<>();
        targetsForG = new HashSet<>();
        from = new HashSet<>();
        p = Optional.empty();
      }

      boolean updateTarget(Player p) {
        return (p.type == E ? targetsForG : targetsForE).add(p);
      }

      boolean removeTarget(Player p) {
        return (p.type == E ? targetsForG : targetsForE).remove(p);
      }
    }

    static final int WALL = -1000000;
    static final int SPACE = -900000;
    static final int G = -800000;
    static final int E = -700000;

    int yn, xn; // Height and width of battlefield
    int a[][]; // Battlefield, that evolves with turns, but starts as shown in input file

    TreeSet<Player> pSorted; // All Goblins and Elves in reading order
    Cell cells[][]; // Battlefield's metadata. Size is [0..yn+2,0..xn+2] to help with corner cases
    int gn, en; // Number of alive Goblins and Elves correspondingly
    int en_initial;
    int turn;

    /*
     * Algorithm.
     *
     * 1) Sort all players to select order.
     * 
     * 2) Player's turn: do we belong to inRange map ? attack : try to move.
     * 
     * 3) Trying to move - BFS. Waves of length 1, 2... until wave N, which meets '?' in inRange.
     * Can we move ? make a step : pass - let the next player move. Can we attack after step ?
     * attack : nothing.
     * 
     * 4) No targets ? end of combat (and we don't count this turn in output) : keep fighting.
     */

    Solver() {

    }

    private boolean wasInputRead = false;
    List<int[]> inputLines;

    void readInput(String fileName) throws IOException {
      Scanner sc =
          new Scanner(Files
              .find(Paths.get("./src/main/resources/adventofcode/input"), 1,
                  (a, b) -> a.getFileName().toString().equals(fileName + ".txt"))
              .findFirst().get());

      inputLines = new ArrayList<>();
      while (sc.hasNextLine()) {
        inputLines.add(parseLine(sc.nextLine()));
      }
      yn = inputLines.size();
      xn = inputLines.get(0).length;

      sc.close();
      wasInputRead = true;
    }

    void resetBattlefield(String fileName) throws IOException {
      // don't wanna re-read the input from the file
      if (!wasInputRead) {
        readInput(fileName);

        a = new int[yn][];
        cells = new Cell[yn + 2][xn + 2];
      }

      // Flush a
      {
        int y = 0;
        Iterator<int[]> it = inputLines.iterator();
        while (it.hasNext()) {
          a[y++] = it.next().clone();
        }
      }
      // Flush cells
      for (int y = 0; y < yn + 2; ++y) {
        for (int x = 0; x < xn + 2; ++x) {
          cells[y][x] = new Cell();
        }
      }
      en = gn = 0;
      // for players update all players-related collections
      pSorted = new TreeSet<>();
      IntStream.range(0, yn).forEach(
          y -> IntStream.range(0, xn).filter(x -> a[y][x] == G || a[y][x] == E).forEach(x -> {
            Player p = Player.of(a[y][x], x, y);
            updateStateWhenPlayerArrives(p);

            if (a[y][x] == G)
              gn++;
            else
              en++;
          }));
      // pSorted.forEach(System.out::println);
      en_initial = en;
      turn = 0;
    }

    public static final int MAX_TURNS = 1000;
    public static final int ELF_MIN_ATTACK_POWER = 4; // according to the condition of the problem
    final int goblinAttackPower = 3;
    // Binary search left and right borders
    int binSearchAttackPower_l = ELF_MIN_ATTACK_POWER - 1;
    int binSearchAttackPower_r = 50;
    int elfAttackPower = (binSearchAttackPower_l + binSearchAttackPower_r) / 2;

    /**
     * @author vitru
     *
     */
    class AI {
      Player p;
      int opponentType;
      int width;
      List<Set<PointXY>> BFS = new ArrayList<>();
      Set<PointXY> current, next, targets;

      AI(Player p) {
        this.p = p;
        opponentType = p.type == E ? G : E;
        width = 0;
      }

      void tryMove() {
        PointXY startingPoint = PointXY.of(p.x, p.y);
        Set<PointXY> startingSet = new HashSet<>();
        startingSet.add(startingPoint);
        BFS.add(startingSet);
        a[p.y][p.x] = 0;
        // find targets (aka '?')
        do {
          current = BFS.get(width);
          BFS.add(next = new HashSet<>());
          targets = new HashSet<>();
          for (PointXY cp : current) {
            for (PointXY dxdy : READING_ORDER) {
              int nextX = cp.x + dxdy.x;
              int nextY = cp.y + dxdy.y;
              if (a[nextY][nextX] == SPACE) {
                a[nextY][nextX] = width + 1;
                next.add(new PointXY(nextX, nextY));
              }

              Cell cell = cells[nextY + 1][nextX + 1];
              if (a[nextY][nextX] - a[cp.y][cp.x] == 1) {
                cell.from.add(PointXY.of(cp.x, cp.y)); // store every partent node (where we
                                                       // might've come from)
              } else {
                continue; // it can't be a target!
              }

              Set<Player> targetsForP = opponentType == E ? cell.targetsForG : cell.targetsForE;
              if (!targetsForP.isEmpty()) {
                targets.add(PointXY.of(nextX, nextY));
              }
            }
          }

          width++;
        } while (targets.size() == 0 && next.size() > 0);
        // move to target
        if (targets.size() > 0) {
          // only move to the target first in reading order
          PointXY bestTarget = targets.stream().sorted().findFirst().get();
          targets.clear();
          targets.add(bestTarget);

          HashSet<PointXY> behind = new HashSet<>(targets);
          HashSet<PointXY> ahead = new HashSet<>();
          do {
            ahead.clear();
            ahead.addAll(behind);
            behind.clear();
            ahead.forEach(pt -> behind.addAll(cells[pt.y + 1][pt.x + 1].from));
          } while (!behind.contains(startingPoint));

          PointXY direction = READING_ORDER.stream()
              .filter(dxdy -> ahead.contains(PointXY.of(p.x + dxdy.x, p.y + dxdy.y))).findFirst()
              .get();
          a[p.y + direction.y][p.x + direction.x] = p.type;
          a[p.y][p.x] = SPACE;
          updateStateWhenPlayerLeaves(p);
          p.x = p.x + direction.x;
          p.y = p.y + direction.y;
          updateStateWhenPlayerArrives(p);
        }
        // cleanup
        BFS.stream().flatMap(Set::stream).forEach(pt -> {
          a[pt.y][pt.x] = SPACE;
          cells[pt.y + 1][pt.x + 1].from.clear();
        });

        a[p.y][p.x] = p.type;
      }

      /**
       * @return true if attacked some opponent successfully
       */
      boolean tryAttack() {
        Optional<Player> mayBeOpponent = getBestAdjacentOpponent(p);
        if (mayBeOpponent.isPresent()) {
          Player opp = mayBeOpponent.get();
          opp.hp -= p.type == E ? elfAttackPower : goblinAttackPower;
          if (opp.hp <= 0) {
            opp.hp = 0;
            if (opp.type == E) {
              en--;
            } else {
              gn--;
            }
            updateStateWhenPlayerLeaves(opp);
            a[opp.y][opp.x] = SPACE;
          }
          return true;
        }

        return false;
      }
    }

    /**
     * @param fileName
     * @param res
     * @throws IOException
     */
    void solve(String fileName, int res) throws IOException {
      while (elfAttackPower != binSearchAttackPower_r) {
        resetBattlefield(fileName);
        solveForCurrentElfAttackPower(fileName, res);
      }
      
      resetBattlefield(fileName);
      solveForCurrentElfAttackPower(fileName, res);

//      System.out.println("turn = " + turn);
//      pSorted.forEach(p -> { System.out.format("%s (%d)%n", p, p.hp);});
      
      int sumHpOfAlive =
          pSorted.stream().filter(p -> p.hp > 0).collect(Collectors.summingInt(p -> p.hp));
      System.out.format("Outcome: %d * %d = %d. (res=%d) elfAttackPower=%d%n", turn,
          sumHpOfAlive, turn * sumHpOfAlive, res, elfAttackPower);
      if (res > 0 && turn * sumHpOfAlive != res) {
        throw new AssertionError(String.format("Test %s failed!", fileName));
      }
    }
    
    /**
     * @param fileName
     * @param res
     */
    private void solveForCurrentElfAttackPower(String fileName, int res) {
      while (en == en_initial && gn > 0 && turn < MAX_TURNS) {
//        System.out.println("turn = " + turn);
//        pSorted.forEach(p -> { System.out.format("%s (%d)%n", p, p.hp);});
        turn++;
        List<Player> players = new ArrayList<>(pSorted); // the order we iterate players by
        // Each player makes a move
        for (Player p : players) {
          if (en == 0 || gn == 0) {
            turn--; // don't count the turn on which the battle has ended
            break;
          }

          // Dead men don't fight
          if (p.hp <= 0)
            continue;

          AI ai = new AI(p);
          if (!ai.tryAttack()) {
            ai.tryMove();
            ai.tryAttack();
          }
        }
        // For our task's real input we wanna see each turn
        printTurnPrettyWhen(res == -1);
      }

      // Quick check for integrity and continue with binary search
      if (turn >= MAX_TURNS) {
        throw new AssertionError("Exceeded the maximum allowed amount of turns: " + turn);
      } else if (binSearchAttackPower_r - binSearchAttackPower_l > 1) {
          // Banary search
          if (en < en_initial) {
            binSearchAttackPower_l = elfAttackPower;
          } else {
            binSearchAttackPower_r = elfAttackPower;
          }
          elfAttackPower = (binSearchAttackPower_l + binSearchAttackPower_r + 1) / 2;
      }
    }

    /**
     * @param condition
     */
    private void printTurnPrettyWhen(boolean condition) {
      if (!condition)
        return;

      print(String.format("Turn %d%n", turn));
      for (int y = 0; y < a.length; ++y) {
        StringBuilder mapSb = new StringBuilder();
        for (int x = 0; x < a[y].length; ++x) {
          char c;
          switch (a[y][x]) {
            case E:
              c = 'E';
              break;
            case G:
              c = 'G';
              break;
            case SPACE:
              c = '.';
              break;
            case WALL:
              c = '#';
              break;
            default:
              throw new AssertionError("Unknown symbol.");
          }
          mapSb.append(c);
        }
        print(mapSb.append("   "));

        StringBuilder hpSb = new StringBuilder();
        for (int x = 1; x < cells[y + 1].length - 1; ++x) {
          Cell cell = cells[y + 1][x + 1];
          if (cell.p.isPresent()) {
            hpSb.append(cell.p.get().type == E ? 'E' : 'G')
                .append(String.format("%-6s", String.format("(%d) ", cell.p.get().hp)));
          }
        }
        println(hpSb);
      }
      // System.out.println(cells[5][6].p.isPresent() ? cells[5][6].p.get().hp + "" :
      // "dead");
      println("");
    }

    public static final List<PointXY> READING_ORDER;
    static {
      READING_ORDER = Arrays.asList(
          new PointXY[] {PointXY.of(0, -1), PointXY.of(-1, 0), PointXY.of(1, 0), PointXY.of(0, 1)});
    }

    void updateStateWhenPlayerArrives(Player p) {
      cells[p.y + 1][p.x + 1].p = Optional.of(p);
      READING_ORDER.forEach(dxdy -> cells[p.y + dxdy.y + 1][p.x + dxdy.x + 1].updateTarget(p));
      pSorted.add(p);
    }

    void updateStateWhenPlayerLeaves(Player p) {
      cells[p.y + 1][p.x + 1].p = Optional.empty();
      READING_ORDER.forEach(dxdy -> cells[p.y + dxdy.y + 1][p.x + dxdy.x + 1].removeTarget(p));
      pSorted.remove(p);
    }

    // TODO: test that reading order preserves for many equal HP adjacent opponents
    Optional<Player> getBestAdjacentOpponent(Player p) {
      int opponentType = p.type == E ? G : E;
      Optional<Player> opp = READING_ORDER.stream().filter(dxdy -> {
        Optional<Player> pp = cells[p.y + dxdy.y + 1][p.x + dxdy.x + 1].p;
        return pp.isPresent() && pp.get().type == opponentType;
      }).map(pt -> cells[p.y + pt.y + 1][p.x + pt.x + 1].p.get())
          .sorted((p1, p2) -> Integer.compare(p1.hp, p2.hp)).findFirst();

      // if (opp.isPresent()) {
      // if (!opp.get().equals(readingOrder.stream().filter(dxdy -> {
      // Optional<Player> pp = cells[p.y + dxdy.y + 1][p.x + dxdy.x + 1].p;
      // return pp.isPresent() && pp.get().type == opponentType && pp.get().hp ==
      // opp.get().hp;
      // }).map(pt -> cells[p.y + pt.y + 1][p.x + pt.x +
      // 1].p.get()).findFirst().get())) {
      // throw new AssertionError("WTF?");
      // }
      // }

      return opp;
    }

    int[] parseLine(String s) {
      int[] res;
      char[] cs = s.toCharArray();
      res = new int[cs.length];
      int i = 0;
      for (char c : cs) {
        int cToInt;
        switch (c) {
          case '#':
            cToInt = WALL;
            break;
          case '.':
            cToInt = SPACE;
            break;
          case 'G':
            cToInt = G;
            break;
          case 'E':
            cToInt = E;
            break;
          default:
            throw new AssertionError(String.format("Unknown symbol in input file + [%c]", c));
        }
        res[i++] = cToInt;
      }
      return res;
    }
  }

  public static void main(String[] args) {
    try (
        PrintWriter r = new PrintWriter(Files
            .find(Paths.get("./src/main/resources/adventofcode/output"), 1,
                (a, b) -> a.getFileName().toString().equals("15out.txt"))
            .findFirst().get().toFile())) {
      writer = r;

      new Solver().solve("15a", 4988);
      new Solver().solve("15c", 31284);
      new Solver().solve("15d", 3478);
      new Solver().solve("15e", 6474);
      new Solver().solve("15f", 1140);
      new Solver().solve("15", -1); // right answer is 95764
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
