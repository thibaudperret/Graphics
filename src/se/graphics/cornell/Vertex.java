package se.graphics.cornell;

public class Vertex {
    
    private final Vector3 position;

    public Vertex(Vector3 position) {
        this.position = position;
    }
    
    public Vector3 position() {
        return position;
    }
}
