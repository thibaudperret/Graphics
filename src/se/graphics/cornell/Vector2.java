package se.graphics.cornell;

public final class Vector2 {

    private final int x;
    private final int y;
    private final float zinv;
    private final Vector3 illumination;
    
    public Vector2(int x, int y, float zinv, Vector3 illumination) {
        this.x = x;
        this.y = y;
        this.zinv = zinv;
        this.illumination = illumination;
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
    
    public Vector3 illumination() {
        return illumination;
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    
}
