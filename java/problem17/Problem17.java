package adventofcode.problem17;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Problem17 {
  static PrintWriter writer;

  static void print(CharSequence s) {
    writer.print(s);
  }

  static void println(CharSequence s) {
    print(s + "\n");
  }

  static enum Type {
    HORIZONTAL, VERTICAL
  }

  static class Unit {
    Type type;
    int at;
    int from, to;

    @Override
    public String toString() {
      return String.format("%s=%d, %s=%d..%d", type == Type.HORIZONTAL ? "y" : "x", at,
          type == Type.HORIZONTAL ? "x" : "y", from, to);
    }
  }

  static final int CLAY = -1000000;
  static final int SAND = 0;
  static final int FALL = -800000;
  static final int REST = -700000;

  static class Solver {
    List<Unit> units;
    int minX, maxX, minY, maxY;
    int a[][];
    int nx, ny;

    Solver() {

    }

    void init() {
      units = new ArrayList<>();
      minX = Integer.MAX_VALUE;
      maxX = Integer.MIN_VALUE;
      minY = Integer.MAX_VALUE;
      maxY = Integer.MIN_VALUE;
    }

    void readInput(String fileName) throws IOException {
      Scanner sc =
          new Scanner(Files
              .find(Paths.get("./src/main/resources/adventofcode/input"), 1,
                  (a, b) -> a.getFileName().toString().equals(fileName + ".txt"))
              .findFirst().get());

      init();

      while (sc.hasNextLine()) {
        Unit unit = parseLine(sc.nextLine());
        units.add(unit);
      }
      sc.close();
    }

    private final Pattern pattern = Pattern.compile("([xy])=(\\d+),\\s([xy])=(\\d+)\\.\\.(\\d+)");

    Unit parseLine(String s) {
      Unit unit = new Unit();
      Matcher m = pattern.matcher(s);
      if (!pattern.matcher(s).matches()) {
        System.err.format("s=[%s] s.length()=%d%n", s, s.length());
        throw new AssertionError("no match");
      }

      m.find();
      unit.type = "x".equals(m.group(1)) ? Type.VERTICAL : Type.HORIZONTAL;
      unit.at = Integer.valueOf(m.group(2));
      unit.from = Integer.valueOf(m.group(4));
      unit.to = Integer.valueOf(m.group(5));

      adjustBounds(unit);

      return unit;
    }

    /**
     * For each input it adjusts min and max values for x and y.
     * 
     * @param unit
     */
    private void adjustBounds(Unit unit) {
      switch (unit.type) {
        case VERTICAL: {
          int x = unit.at;
          int yFrom = unit.from;
          int yTo = unit.to;
          if (x < minX)
            minX = x;
          if (x > maxX)
            maxX = x;
          if (yFrom < minY)
            minY = yFrom;
          if (yTo > maxY)
            maxY = yTo;
          break;
        }
        case HORIZONTAL: {
          int y = unit.at;
          int xFrom = unit.from;
          int xTo = unit.to;
          if (y < minY)
            minY = y;
          if (y > maxY)
            maxY = y;
          if (xFrom < minX)
            minX = xFrom;
          if (xTo > maxX)
            maxX = xTo;
          break;
        }
        default:
          throw new AssertionError();
      }
    }

    private void populateGround() {
      nx = maxX - minX + 1;
      ny = maxY - minY + 1;
      a = new int[ny + 2][nx + 2 * X_EXTRA];
      for (Unit unit : units) {
        for (int i = 0; i <= unit.to - unit.from; ++i) {
          switch (unit.type) {
            case HORIZONTAL:
              setA(unit.from - minX + i, unit.at - minY, CLAY);
              break;
            case VERTICAL:
              setA(unit.at - minX, unit.from - minY + i, CLAY);
              break;
          }
        }
      }
    }

    private void printGround() {
      StringBuilder sb = new StringBuilder();
      for (int y = 0; y < ny + 2; ++y) {
        for (int x = 0; x < nx + 2 * X_EXTRA; ++x) {
          String s = "";
          switch (a[y][x]) {
            case CLAY:
              s = "#";
              break;
            case REST:
              s = "~";
              break;
            case FALL:
              s = "|";
              break;
            case SAND:
              s = ".";
              break;
            default:
              throw new AssertionError("Unknown value: " + a[y][x]);
          }
          sb.append(s);
        }
        sb.append("\n");
      }

      print(sb.toString());
    }

    static final int X_EXTRA = 10;

    int a(int x, int y) {
      return a[y + 1][x + X_EXTRA];
    }

    int a(PointXY p) {
      return a[p.y + 1][p.x + X_EXTRA];
    }

    void setA(int x, int y, int value) {
      a[y + 1][x + X_EXTRA] = value;
    }

    void setA(PointXY p, int value) {
      a[p.y + 1][p.x + X_EXTRA] = value;
    }

    int nCalls = 0;
    final int N_CALLS_MAX = 100000;
    boolean wasNCallsThresholdExceeded = false;

    void flow(PointXY pHere) {
      nCalls++;
      if (nCalls > N_CALLS_MAX) {
        wasNCallsThresholdExceeded = true;
        return;
      }
      //////////////////////
      setA(pHere, FALL);
      
      int x = pHere.x;
      int y = pHere.y;
      if (y >= ny || x < -X_EXTRA + 1 || x >= nx + X_EXTRA - 1) {
        return;
      }
      
      PointXY pDown = PointXY.of(x, y + 1);
      if (a(pDown) == SAND) {
        flow(pDown);
      }

      if (a(pDown) == FALL) {
        return;
      }

      PointXY pLeft = PointXY.of(x - 1, y);
      if (a(pLeft) == SAND) {
        flow(pLeft);
      }

      PointXY pRight = PointXY.of(x + 1, y);
      if (a(pRight) == SAND) {
        flow(pRight);
      } else if (a(pRight) == CLAY) {
        int i = 1;
        while (a(x - i, y) == FALL) {
          i++;
        }

        if (a(x - i, y) == CLAY) {
          for (int j = x - i + 1; j <= x; ++j) {
            setA(j, y, REST);
          }
        }
      }
    }

    /**
     * @throws IOException
     */
    void solve1() throws IOException {
      populateGround();
      // printGround();
      flow(PointXY.of(500 - minX, 0));
      printGround();
      System.out.format("wasNCallsThresholdExceeded: %s%n", wasNCallsThresholdExceeded);
      System.out.format("minX=%d maxX=%d minY=%d maxY=%d ny=%d nx=%d%n", minX, maxX, minY, maxY, ny,
          nx);

      int sum = 0;
      for (int y = 0; y < ny; ++y) {
        for (int x = -X_EXTRA + 1; x < nx + X_EXTRA - 1; ++x) {
          int val = a(x, y);
          if (val == FALL || val == REST) {
            sum++;
          }
        }
      }
      System.out.format("Part I result = %d%n", sum);
    }
    
    void solve2() throws IOException {
      int sum = 0;
      for (int y = 0; y < ny; ++y) {
        for (int x = -X_EXTRA + 1; x < nx + X_EXTRA - 1; ++x) {
          int val = a(x, y);
          if (val == REST) {
            sum++;
          }
        }
      }
      System.out.format("Part II result = %d%n", sum);
    }

    // ############## Tests ##############
    @interface Test {
    }

    @Test
    void testRegEx() {
      String s = "x=452, y=1077..1087";
      System.out.println(pattern.matcher(s).matches());
      Matcher m = pattern.matcher(s);
      while (m.find()) {
        System.out.format("%s %s %s %s %s%n", m.group(1), m.group(2), m.group(3), m.group(4),
            m.group(5));
      }
    }

    @Test
    void testInputParsing() {
      units.forEach(it -> System.out.format("%s%n", it.toString()));
    }
  }

  public static void main(String[] args) {
    Solver solver = new Solver();
    // solver.testRegEx();
    try (
        PrintWriter r = new PrintWriter(Files
            .find(Paths.get("./src/main/resources/adventofcode/output"), 1,
                (a, b) -> a.getFileName().toString().equals("17out.txt"))
            .findFirst().get().toFile())) {
      writer = r;
      // try {
      solver.readInput("17");
      solver.solve1(); // Right answer for part one is 32552
      solver.solve2(); // Right answer for part two is 26405
      // solver.testInputParsing();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
