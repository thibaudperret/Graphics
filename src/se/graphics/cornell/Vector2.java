package se.graphics.cornell;

public final class Vector2 {

    private final int x;
    private final int y;
    private final float zinv;
    private final Vector3 pos3d;
    
    public Vector2(int x, int y, float zinv, Vector3 pos3d) {
        this.x = x;
        this.y = y;
        this.zinv = zinv;
        this.pos3d = pos3d;
    }
    
    public int x() {
        return x;
    }
    
    public int y() {
        return y;
    }
    
    public float zinv() {
        return zinv;
    }
    
    public Vector3 pos3d() {
        return pos3d;
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    
}
