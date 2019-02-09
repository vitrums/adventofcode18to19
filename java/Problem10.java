package adventofcode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class Problem10 {
  static class Solver {
    static class Pair<A, B> {
      A fst;
      B snd;

      Pair(A fst, B snd) {
        this.fst = fst;
        this.snd = snd;
      }
    }

    static class Pos {
      int x, y;

      Pos(int x, int y) {
        this.x = x;
        this.y = y;
      }
    }

    class AugPos {
      Pos pos;
      long power;
      Optional<Long> augPower;

      AugPos(Pos pos, long power) {
        this.pos = pos;
        this.power = power;
        this.augPower = Optional.empty();
      }

      long calcPower() {
        // Cache
        if (!augPower.isPresent()) {
          long sum = 0;
          if (n - pos.x < 3 || n - pos.y < 3) {
            sum = Long.MIN_VALUE;
          } else {
            for (int x = pos.x; x < pos.x + 3; ++x) {
              for (int y = pos.y; y < pos.y + 3; ++y) {
                sum += grid[x][y].power;
              }
            }
          }

          augPower = Optional.of(sum);
        }

        return augPower.get();
      }
    }

    int n;
    int serial;
    AugPos[][] grid;

    Solver() {
      this.n = 300;
      this.grid = new AugPos[n][n];
    }

    void solve() throws IOException {
      // Scanner sc = new Scanner(Files.find(Paths.get("./src/main/resources/adventofcode/input"),
      // 1,
      // (a, b) -> a.getFileName().toString().equals("10.txt")).findFirst().get());
      //
      // System.out.println(sc.nextLine());
      serial = 5153;

      for (int x = 0; x < n; ++x) {
        for (int y = 0; y < n; ++y) {
          int rackId = (x + 1) + 10;
          long power = ((rackId * (y + 1) + serial) * rackId) % 1000 / 100 - 5;
          grid[x][y] = new AugPos(new Pos(x, y), power);
        }
      }

      AugPos res = Arrays.stream(grid).flatMap(Arrays::stream)
          .max((a, b) -> Long.compare(a.calcPower(), b.calcPower())).get();
      System.out.printf("(%d, %d) %d%n", res.pos.x + 1, res.pos.y + 1, res.calcPower());
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
