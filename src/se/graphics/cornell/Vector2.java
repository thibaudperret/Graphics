package se.graphics.cornell;

public final class Vector2 {

    private final int x;
    private final int y;
    
    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int x() {
        return x;
    }
    
    public int y() {
        return y;
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    
}
