package se.graphics.cornell;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public final class Lab2 {
    
    private Lab2() {
        
    }

    public static void draw(PApplet app, int size, float f, Vector3 C, Matrix3 R) {
        app.fill(255);
        
        for (Triangle t : Loader.cornellBox()) {
            for (Vector3 vertex : t.vertices()) {
//                Vector2 p = vertexShader(vertex, C, R, f);
//                app.rect(p.x(), p.y(), 1, 1);
                drawPolygonEdgesLowRes(app, t.vertices(), C, R, f, 1);
            }

//            drawLine(p, i1, j1, i2, j2);
//            drawLine(p, i2, j2, i3, j3);
//            drawLine(p, i3, j3, i1, j1);
        }
    }
    
    public static void drawLowRes(PApplet app, int size, int resolution, float f, Vector3 C, Matrix3 R) {
        int ratio = size / resolution;
        app.fill(255);
        
        for (Triangle t : Loader.cornellBox()) {
//                Vector2 p = vertexShader(vertex, C, R, f);
//                app.rect(p.x() * ratio, p.y() * ratio, ratio, ratio);
            drawPolygonEdgesLowRes(app, t.vertices(), C, R, f, ratio);
            

//            drawLine(p, i1, j1, i2, j2);
//            drawLine(p, i2, j2, i3, j3);
//            drawLine(p, i3, j3, i1, j1);
        }
    }
    
    private static Vector2 vertexShader(Vector3 p, Vector3 C, Matrix3 R, float f) {
        Vector3 ptilde = R.times(p.minus(C));
        return new Vector2((int) (f * ptilde.x() / ptilde.z() + f / 2), (int) (f * ptilde.y() / ptilde.z() + f / 2));
    }
    
    private static List<Vector2> interpolate(Vector2 a, Vector2 b) {
        List<Vector2> result = new ArrayList<>();
        int size = max(abs(a.x() - b.x()), abs(a.y() - b.y())) / 2;

        float dx = ((float) (b.x() - a.x())) / size;
        float dy = ((float) (b.y() - a.y())) / size;
        
        for (int i = 0; i < size; ++i) {
            result.add(new Vector2(a.x() + (int) (i * dx), a.y() + (int) (i * dy)));
        }
        
        return result;
    }
    
    private static void drawLineLowRes(PApplet p, Vector2 a, Vector2 b, Vector3 color, int ratio) {
        List<Vector2> result = interpolate(a, b);
        p.fill(255 * color.x(), 255 * color.y(), 255 * color.z());

        for (Vector2 v : result) {
            p.rect(v.x() * ratio, v.y() * ratio, ratio, ratio);
        }
    }
    
    private static void drawPolygonEdgesLowRes(PApplet p, List<Vector3> vertices, Vector3 C, Matrix3 R, float f, int ratio) {
        int v = vertices.size();
        List<Vector2> projectedVertices = new ArrayList<>();
        
        for (int i = 0; i < v; ++i) {
            projectedVertices.add(vertexShader(vertices.get(i), C, R, f));
        }
        
        for (int i = 0; i < v; ++i) {
            int j = (i + 1) % v;
            drawLineLowRes(p, projectedVertices.get(i), projectedVertices.get(j), new Vector3(1f, 1f, 1f), ratio);            
        }
    }

    private static int max(int a, int b) {
        return a > b ? a : b;
    }

    private static int abs(int i) {
        return i < 0 ? -i : i;
    }

}
