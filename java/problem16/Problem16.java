package adventofcode.problem16;

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

public class Problem16 {
  static PrintWriter writer;

  static void print(CharSequence s) {
    writer.print(s);
  }

  static void println(CharSequence s) {
    print(s + "\n");
  }

  static enum InputType {
    BEFORE, INSTRUCTION, AFTER, OTHER
  }

  static class InputLineResult {
    InputType inputType;
    int[] input;
  }

  static class Solver {
    List<Unit> units;
    List<int[]> testProgram;

    Solver() {

    }

    void init() {
      units = new ArrayList<>();
      testProgram = new ArrayList<int[]>();
    }

    void readInput(String fileName) throws IOException {
      Scanner sc =
          new Scanner(Files
              .find(Paths.get("./src/main/resources/adventofcode/input"), 1,
                  (a, b) -> a.getFileName().toString().equals(fileName + ".txt"))
              .findFirst().get());

      init();

      InputType lastInputType = InputType.OTHER;
      Unit unit = null;
      while (sc.hasNextLine()) {
        InputLineResult inputLineResult = parseLine(sc.nextLine());

        switch (inputLineResult.inputType) {
          case BEFORE:
            unit = new Unit();
            unit.setBefore(inputLineResult.input);
            break;
          case INSTRUCTION:
            if (lastInputType != InputType.BEFORE) {
              testProgram.add(inputLineResult.input);
            } else {
              unit.setInstruction(inputLineResult.input);
            }
            break;
          case AFTER:
            unit.setAfter(inputLineResult.input);
            units.add(unit);
            break;
          case OTHER:
            break;
          default:
            throw new AssertionError();
        }

        lastInputType = inputLineResult.inputType;
      }
      sc.close();
    }

    private final Pattern patternBefore =
        Pattern.compile("Before:\\s+\\[(\\d+),\\s(\\d+),\\s(\\d+),\\s(\\d+)\\]");
    private final Pattern patternAfter =
        Pattern.compile("After:\\s+\\[(\\d+),\\s(\\d+),\\s(\\d+),\\s(\\d+)\\]");
    private final Pattern patternInstruction =
        Pattern.compile("(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");

    InputLineResult parseLine(String s) {
      InputLineResult inputLineResult = new InputLineResult();

      if ("".equals(s)) {
        inputLineResult.inputType = InputType.OTHER;
      } else {
        Pattern p = null;

        if (patternBefore.matcher(s).matches()) {
          p = patternBefore;
          inputLineResult.inputType = InputType.BEFORE;
        } else if (patternAfter.matcher(s).matches()) {
          p = patternAfter;
          inputLineResult.inputType = InputType.AFTER;
        } else if (patternInstruction.matcher(s).matches()) {
          p = patternInstruction;
          inputLineResult.inputType = InputType.INSTRUCTION;
        } else {
          System.err.format("s=[%s] s.length()=%d%n", s, s.length());
          throw new AssertionError("no match");
        }

        Matcher m = p.matcher(s);
        m.find();
        inputLineResult.input = new int[] {Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)),
            Integer.valueOf(m.group(3)), Integer.valueOf(m.group(4))};
      }

      return inputLineResult;
    }

    /**
     * @throws IOException
     */
    void solve1() throws IOException {
      int sum = 0;
      for (Unit u : units) {
        int count = 0;
        for (Opcode opcode : Opcode.values()) {
          int r[] = u.before.clone();
          int a = u.instruction[1], b = u.instruction[2], c = u.instruction[3];
          opcode.apply(a, b, c, r);

          if (Arrays.equals(u.after, r)) {
            count++;
          }
        }

        if (count >= 3) {
          sum++;
        }
      }

      System.out.format("Part I result = %d%n", sum);
    }

    /**
     * @author vitru
     *
     */
    static class CodeToOps { // 2019 we still can't work with arrays of generics........
      final int idx; // from 0 to 15 including
      Set<Opcode> potentialOps = new HashSet<>();

      CodeToOps(int idx) {
        this.idx = idx;
      }
    }

    CodeToOps codeToOps[];
    {
      codeToOps = new CodeToOps[16];
      IntStream.range(0, codeToOps.length).forEach(it -> codeToOps[it] = new CodeToOps(it));
    }

    /**
     * @throws IOException
     */
    void solve2() throws IOException {
      for (Unit u : units) {
        for (Opcode opcode : Opcode.values()) {
          int r[] = u.before.clone();
          int a = u.instruction[1], b = u.instruction[2], c = u.instruction[3];
          opcode.apply(a, b, c, r);

          Set<Opcode> potentialOps = codeToOps[u.instruction[0]].potentialOps;
          if (Arrays.equals(u.after, r)) {
            potentialOps.add(opcode);
          } else {
            potentialOps.remove(opcode);
          }
        }
      }

      // Following the simple logic that there's only one solution (otherwise the terms would be
      // ambiguous) we exclude on each pass impossible scenarios
      for (int pass = 0; pass < 16; ++pass) {
        Set<Opcode> exclusionSet =
            Arrays.stream(codeToOps).filter(it -> it.potentialOps.size() == 1)
                .map(it -> it.potentialOps.iterator().next()).collect(Collectors.toSet());
        Arrays.stream(codeToOps).filter(it -> it.potentialOps.size() > 1)
            .forEach(it -> it.potentialOps.removeAll(exclusionSet));
      }

      Opcode codes[] = new Opcode[16];
      Arrays.stream(codeToOps).forEach(it -> codes[it.idx] = it.potentialOps.iterator().next());
      // Our registers hold zero values at the beginning
      int r[] = new int[4];
      for (int[] instruction : testProgram) {
        int a = instruction[1], b = instruction[2], c = instruction[3];
        codes[instruction[0]].apply(a, b, c, r);
      }

      System.out.format("Part II result = %d%n", r[0]);
    }

    // ############## Tests ##############
    @interface Test {
    }

    @Test
    void testRegEx() {
      String s = "Before: [1, 1, 2, 0]";
      System.out.println(patternBefore.matcher(s).matches());
      Matcher m = patternBefore.matcher(s);
      while (m.find()) {
        System.out.format("%s %s %s %s%n", m.group(1), m.group(2), m.group(3), m.group(4));
      }
    }

    @Test
    void testInputParsing() {
      units.forEach(it -> System.out.format("%s%n%n", it.toString()));
    }
  }

  public static void main(String[] args) {
    Solver solver = new Solver();
    // solver.testRegEx();
    try {
      solver.readInput("16");
      // solver.solve1(); // Right answer for part one is 614
      solver.solve2(); // Right answer for part two is 656
      // solver.testInputParsing();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
