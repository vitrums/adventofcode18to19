package adventofcode.problem16;

public class Unit {
  int[] before, after, instruction;

  int[] getBefore() {
    return before;
  }

  void setBefore(int[] before) {
    this.before = before;
  }

  int[] getAfter() {
    return after;
  }

  void setAfter(int[] after) {
    this.after = after;
  }

  int[] getInstruction() {
    return instruction;
  }

  void setInstruction(int[] instruction) {
    this.instruction = instruction;
  }

  @Override
  public String toString() {
    return String.format("Before: [%d, %d, %d, %d]%n%d %d %d %d%nAfter: [%d, %d, %d, %d]",
        before[0], before[1], before[2], before[3], instruction[0], instruction[1], instruction[2],
        instruction[3], after[0], after[1], after[2], after[3]);
  }
}
