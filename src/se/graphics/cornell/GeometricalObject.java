package se.graphics.cornell;

public abstract class GeometricalObject {
    
    private final Vector3 color;

    public abstract boolean isTriangle();
    public abstract boolean isSphere();
    
    public GeometricalObject(Vector3 color) {
        this.color = color;
    }
    
    public Vector3 color() {
        return color;
    }
    
    public Triangle asTriangle() {
        if (isTriangle()) {
            return (Triangle) this;
        } else {
            throw new IllegalStateException("cannot interpret sphere as triangle");
        }
    }
    
    public Sphere asSphere() {
        if (isSphere()) {
            return (Sphere) this;
        } else {
            throw new IllegalStateException("cannot interpret triangle as sphere");
        }
    }
    
}
