package adventofcode.problem16;

public enum Opcode {
  addr {
    @Override
    public void apply(int a, int b, int c, int[] r) {
      r[c] = r[a] + r[b];
    }
  },
  addi {
    @Override
    public void apply(int a, int b, int c, int[] r) {
      r[c] = r[a] + b;
    }
  },
  mulr {
    @Override
    public void apply(int a, int b, int c, int[] r) {
      r[c] = r[a] * r[b];
    }
  },
  muli {
    @Override
    public void apply(int a, int b, int c, int[] r) {
      r[c] = r[a] * b;
    }
  },
  banr {
    @Override
    public void apply(int a, int b, int c, int[] r) {
      r[c] = r[a] & r[b];
    }
  },
  bani {
    @Override
    public void apply(int a, int b, int c, int[] r) {
      r[c] = r[a] & b;
    }
  },
  borr {
    @Override
    public void apply(int a, int b, int c, int[] r) {
      r[c] = r[a] | r[b];
    }
  },
  bori {
    @Override
    public void apply(int a, int b, int c, int[] r) {
      r[c] = r[a] | b;
    }
  },
  setr {
    @Override
    public void apply(int a, int b, int c, int[] r) {
      r[c] = r[a];
    }
  },
  seti {
    @Override
    public void apply(int a, int b, int c, int[] r) {
      r[c] = a;
    }
  },
  gtir {
    @Override
    public void apply(int a, int b, int c, int[] r) {
      r[c] = a > r[b] ? 1 : 0;
    }
  },
  grri {
    @Override
    public void apply(int a, int b, int c, int[] r) {
      r[c] = r[a] > b ? 1 : 0;
    }
  },
  gtrr {
    @Override
    public void apply(int a, int b, int c, int[] r) {
      r[c] = r[a] > r[b] ? 1 : 0;
    }
  },
  eqir {
    @Override
    public void apply(int a, int b, int c, int[] r) {
      r[c] = a == r[b] ? 1 : 0;
    }
  },
  eqri {
    @Override
    public void apply(int a, int b, int c, int[] r) {
      r[c] = r[a] == b ? 1 : 0;
    }
  },
  eqrr {
    @Override
    public void apply(int a, int b, int c, int[] r) {
      r[c] = r[a] == r[b] ? 1 : 0;
    }
  };
  
  public abstract void apply(int a, int b, int c, int[] r);
}
