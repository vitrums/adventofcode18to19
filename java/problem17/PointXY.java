package adventofcode.problem17;

import java.util.Comparator;

public class PointXY implements Comparable<PointXY> {
  static class ReadingOrderComparator implements Comparator<PointXY> {
    static ReadingOrderComparator INSTANCE = new ReadingOrderComparator();

    @Override
    public int compare(PointXY o1, PointXY o2) {
      int byY = Integer.compare(o1.y, o2.y);
      return byY != 0 ? byY : Integer.compare(o1.x, o2.x);
    }
  }

  static PointXY of(int x, int y) {
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