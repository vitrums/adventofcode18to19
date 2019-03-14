package adventofcode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Problem15 {
  static class Solver {
    static class PointXY {
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
      Set<Player> targetsForE, targetsForG;
      Set<PointXY> from;

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

    int yn, xn;
    int a[][];

    Set<Player> pSorted = new TreeSet<>();
    Cell cells[][];
    int gn, en;


    // HashMap<?> inRange;
    // sort all players to select order
    // Player's turn: if we belong to inRange map => attack
    // else search: waves of length 1, 2... until wave N, which meats ? in inRange

    Solver() {}

    void readInput() throws IOException {
      Scanner sc = new Scanner(Files.find(Paths.get("./src/main/resources/adventofcode/input"), 1,
          (a, b) -> a.getFileName().toString().equals("15.txt")).findFirst().get());

      List<int[]> rows = new ArrayList<>();
      while (sc.hasNextLine()) {
        rows.add(parseLine(sc.nextLine()));
      }
      yn = rows.size();
      xn = rows.get(0).length;
      a = new int[yn][];
      rows.toArray(a);
    }

    int turn = 0;
    void solve() throws IOException {
      readInput();

      cells = new Cell[yn + 2][xn + 2];
      for (int y = 0; y < yn + 2; ++y) {
        for (int x = 0; x < xn + 2; ++x) {
          cells[y][x] = new Cell();
        }
      }

      // for players update all players-related collections
      IntStream.range(0, yn).forEach(
          y -> IntStream.range(0, xn).filter(x -> a[y][x] == G || a[y][x] == E).forEach(x -> {
            Player p = Player.of(a[y][x], x, y);
            updateCellForIncomingPlayer(p);

            if (a[y][x] == G)
              gn++;
            else
              en++;
          }));
      // pSorted.forEach(System.out::println);

      // TODO: turn < xxx for debug only
      while (en > 0 && gn > 0 && turn < 10000) {
        turn++;
        List<Player> players = new ArrayList<>(pSorted); // the order we iterate players by
        // Make a move
        for (Player p : players) {
          if (en == 0 || gn == 0) {
            turn--;
            break;
          }
          
          class AI {
            int opponentType;
            int width;
            List<Set<PointXY>> BFS = new ArrayList<>();
            Set<PointXY> current, next, targets;

            AI() {
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
                  for (PointXY dxdy : readingOrder) {
                    int nextX = cp.x + dxdy.x;
                    int nextY = cp.y + dxdy.y;
                    if (a[nextY][nextX] == SPACE) {
                      a[nextY][nextX] = width + 1;
                      next.add(new PointXY(nextX, nextY));
                    }

                    Cell cell = cells[nextY + 1][nextX + 1];
                    if (a[nextY][nextX] - a[cp.y][cp.x] == 1) {
                      cell.from.add(PointXY.of(cp.x, cp.y));
                    } else {
                      continue;
                    }

                    Set<Player> targetsForP =
                        opponentType == E ? cell.targetsForG : cell.targetsForE;
                    if (!targetsForP.isEmpty()) {
                      targets.add(PointXY.of(nextX, nextY));
                    }
                  }
                }

                width++;
              } while (targets.size() == 0 && next.size() > 0);
              // move to target
              if (targets.size() > 0) {
                HashSet<PointXY> behind = new HashSet<>(targets);
                HashSet<PointXY> ahead = new HashSet<>();
                do {
                  ahead.clear();
                  ahead.addAll(behind);
                  behind.clear();
                  ahead.forEach(pt -> behind.addAll(cells[pt.y + 1][pt.x + 1].from));
                } while (!behind.contains(startingPoint));

                PointXY direction = readingOrder.stream()
                    .filter(dxdy -> ahead.contains(PointXY.of(p.x + dxdy.x, p.y + dxdy.y)))
                    .findFirst().get();
                a[p.y + direction.y][p.x + direction.x] = p.type;
                a[p.y][p.x] = SPACE;
                updateCellForLeavingPlayer(p);
                p.x = p.x + direction.x;
                p.y = p.y + direction.y;
                updateCellForIncomingPlayer(p);
              }
              // cleanup
              BFS.stream().flatMap(Set::stream).forEach(pt -> {
                a[pt.y][pt.x] = SPACE;
                cells[pt.y + 1][pt.x + 1].from.clear();
              });

              a[p.y][p.x] = p.type;
            }

            boolean tryAttack(Player p) {
              Optional<Player> mayBeOpponent = getBestAdjacentOpponent(p);
              if (mayBeOpponent.isPresent()) {
                Player opp = mayBeOpponent.get();
                opp.hp -= 3;
                if (opp.hp <= 0) {
                  if (opp.type == E)
                    en--;
                  else
                    gn--;
                  updateCellForLeavingPlayer(opp);
                  a[opp.y][opp.x] = SPACE;
                }
                return true;
              }

              return false;
            }
          }

          if (p.hp <= 0)
            continue;

          AI ai = new AI();
          if (!ai.tryAttack(p)) {
            ai.tryMove();
            ai.tryAttack(p);
          }
        }

        System.out.printf("Turn %d%n", turn);
        for (int y = 0; y < a.length; ++y) {
          StringBuilder sb = new StringBuilder();
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
            sb.append(c);
          }
          System.out.println(sb);
        }
//        System.out.println(cells[5][6].p.isPresent() ? cells[5][6].p.get().hp + "" : "dead");
        System.out.println();
        System.out.println();
      }
      
      int sumHpOfAlive = pSorted.stream().collect(Collectors.summingInt(p -> p.hp));
      System.out.printf("Outcome: %d * %d = %d", turn, sumHpOfAlive, turn * sumHpOfAlive);
    }

    static List<PointXY> readingOrder;
    static {
      readingOrder = Arrays.asList(
          new PointXY[] {PointXY.of(0, -1), PointXY.of(-1, 0), PointXY.of(1, 0), PointXY.of(0, 1)});
    }

    void updateCellForIncomingPlayer(Player p) {
      cells[p.y + 1][p.x + 1].p = Optional.of(p);
      readingOrder.forEach(dxdy -> cells[p.y + dxdy.y + 1][p.x + dxdy.x + 1].updateTarget(p));
      pSorted.add(p);
    }

    void updateCellForLeavingPlayer(Player p) {
      cells[p.y + 1][p.x + 1].p = Optional.empty();
      readingOrder.forEach(dxdy -> cells[p.y + dxdy.y + 1][p.x + dxdy.x + 1].removeTarget(p));
      pSorted.remove(p);
    }

    // TODO: test that reading order preserves for many equal HP adjacent opponents
    Optional<Player> getBestAdjacentOpponent(Player p) {
      int opponentType = p.type == E ? G : E;
      Optional<Player> opp = readingOrder.stream().filter(dxdy -> {
        Optional<Player> pp = cells[p.y + dxdy.y + 1][p.x + dxdy.x + 1].p;
        return pp.isPresent() && pp.get().type == opponentType;
      }).map(pt -> cells[p.y + pt.y + 1][p.x + pt.x + 1].p.get())
          .sorted((p1, p2) -> Integer.compare(p1.hp, p2.hp)).findFirst();
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
    try {
      new Solver().solve();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
