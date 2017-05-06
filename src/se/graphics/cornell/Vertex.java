package se.graphics.cornell;

public class Vertex {
    
    private final Vector3 position;
    private final Vector3 normal;
    private final Vector3 reflectance;

    public Vertex(Vector3 position, Vector3 normal, Vector3 reflectance) {
        this.position = position;
        this.normal = normal;
        this.reflectance = reflectance;
    }
    
    public Vector3 position() {
        return position;
    }
    
    public Vector3 normal() {
        return normal;
    }
    
    public Vector3 reflectance() {
        return reflectance;
    }
    
}
