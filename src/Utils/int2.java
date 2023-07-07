package Utils;

public class int2 {
    public int x;
    public int y;

    public int2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        int2 value = (int2) o;
        return x == value.x && y == value.y;
    }

    @Override
    public int hashCode(){
        int sum = x + y;
        int uniqueInteger = (sum * (sum + 1)) / 2 + y;
        return uniqueInteger;
    }

    @Override
    public String toString(){
        return "(" + x + "/" + y + ")";
    }

    public int2 Multiply(float other) {
        return new int2(Math.round(x * other), Math.round(y * other));
    }
}
