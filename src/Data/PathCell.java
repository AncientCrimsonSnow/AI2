package Data;

public class PathCell implements Cloneable, Comparable<PathCell> {
    public Integer2 pos;
    public int value;
    public PathCell cameFrom;
    public PathCell(Integer2 pos, int value) {
        this.pos = pos;
        this.value = value;
    }

    @Override
    public PathCell clone() {
        var clone = new PathCell(pos, value);
        clone.cameFrom = cameFrom;
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathCell value = (PathCell) o;
        return pos.x == value.pos.x && pos.y == value.pos.y;
    }

    @Override
    public int hashCode(){
        int sum = pos.x + pos.y;
        int uniqueInteger = (sum * (sum + 1)) / 2 + pos.y;
        return uniqueInteger;
    }

    @Override
    public int compareTo(PathCell o) {
        if(value == o.value)
            return 0;
        if(value < o.value)
            return -1;
        return 1;
    }
}
