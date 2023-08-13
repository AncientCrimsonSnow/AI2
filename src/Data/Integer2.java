package Data;

public class Integer2 {
    public int x;
    public int y;

    public Integer2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Integer2 value = (Integer2) o;
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

    public Integer2 Multiply(float other) {
        return new Integer2(Math.round(x * other), Math.round(y * other));
    }

    public Integer2 Divide(float other) {
        return new Integer2(Math.round(x / other), Math.round(y / other));
    }
}
