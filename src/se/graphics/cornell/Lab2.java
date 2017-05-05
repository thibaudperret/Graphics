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
                Vector2 p = vertexShader(vertex, C, R, f);
                app.rect(p.x(), p.y(), 1, 1);
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
            for (Vector3 vertex : t.vertices()) {
                Vector2 p = vertexShader(vertex, C, R, f);
                app.rect(p.x() * ratio, p.y() * ratio, ratio, ratio);
            }

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
        int size = max(abs(a.x() - b.x()), abs(a.y() - b.y()));

        float dx = ((float) (b.x() - a.x())) / size;
        float dy = ((float) (b.y() - a.y())) / size;
        
        for (int i = 0; i < size; ++i) {
            result.add(new Vector2(a.x() + (int) (i * dx), a.y() + (int) (i * dy)));
        }
        
        result.add(b);
        
        return result;
    }
    
    private static void drawLine(PApplet p, int x1, int y1, int x2, int y2) {
//        List<Vector2> line = interpolate(new Vector2(x1, y1), new Vector2(x2, y2));
        int size = max(abs(x1 - x2), abs(y1 - y2));

        float dx = ((float) (x2 - x1)) / size;
        float dy = ((float) (y2 - y1)) / size;
        
        for (int i = 0; i <= size; ++i) {
            p.point(x1 + (int) (i * dx), y1 + (int) (i * dy));
        }
    }

    private static int max(int a, int b) {
        return a > b ? a : b;
    }

    private static int abs(int i) {
        return i < 0 ? -i : i;
    }

}
