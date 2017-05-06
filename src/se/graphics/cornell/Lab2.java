package se.graphics.cornell;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public final class Lab2 {
    
    private static Vector3 C;
    private static Matrix3 R;
    private static float f;
    private static int ratio;
    
    private Lab2() {
        
    }

    public static void draw(PApplet app, int size, float f, Vector3 C, Matrix3 R) {
        app.fill(255);
        
        Lab2.C = C;
        Lab2.R = R;
        Lab2.f = f;
        Lab2.ratio = 1;
        
        for (Triangle t : Loader.cornellBox()) {
            drawPolygonEdges(app, t.vertices());
        }
    }
    
    public static void drawLowRes(PApplet app, int size, int resolution, float f, Vector3 C, Matrix3 R) {
        app.fill(255);
        
        Lab2.C = C;
        Lab2.R = R;
        Lab2.f = f;
        Lab2.ratio = size / resolution;
        
        for (Triangle t : Loader.cornellBox()) {
            drawPolygon(app, t.vertices(), t.color());
        }
    }
    
    private static Vector2 vertexShader(Vector3 p) {
        Vector3 ptilde = R.times(p.minus(C));
        return new Vector2((int) (f * ptilde.x() / ptilde.z() + f / 2), (int) (f * ptilde.y() / ptilde.z() + f / 2));
    }
    
    private static List<Vector2> interpolate(Vector2 a, Vector2 b) {
        List<Vector2> result = new ArrayList<>();
        int size = max(abs(a.x() - b.x()), abs(a.y() - b.y()));

        float dx = ((float) (b.x() - a.x())) / size;
        float dy = ((float) (b.y() - a.y())) / size;
        
        for (int i = 0; i < size; ++i) {
            result.add(new Vector2(a.x() + (int) Math.floor(i * dx), a.y() + (int) Math.floor(i * dy)));
        }
        
        result.add(b);
        
        return result;
    }
    
    private static void drawLine(PApplet p, Vector2 a, Vector2 b, Vector3 color) {
        List<Vector2> result = interpolate(a, b);
        p.fill(255 * color.x(), 255 * color.y(), 255 * color.z());

        for (Vector2 v : result) {
            p.rect(v.x() * ratio, v.y() * ratio, ratio, ratio);
        }
    }
    
    private static void drawPolygonEdges(PApplet p, List<Vector3> vertices) {
        int v = vertices.size();
        List<Vector2> projectedVertices = new ArrayList<>();
        
        for (int i = 0; i < v; ++i) {
            projectedVertices.add(vertexShader(vertices.get(i)));
        }
        
        for (int i = 0; i < v; ++i) {
            int j = (i + 1) % v;
            drawLine(p, projectedVertices.get(i), projectedVertices.get(j), new Vector3(1f, 1f, 1f));            
        }
    }
    
    private static void drawPolygon(PApplet p, List<Vector3> vertices, Vector3 color) {
        int v = vertices.size();
        List<Vector2> projectedVertices = new ArrayList<>();

        for (int i = 0; i < v; ++i) {
            projectedVertices.add(vertexShader(vertices.get(i)));
        }
        
        Tuple<List<Vector2>, List<Vector2>> leftRight = computePolygonRows(projectedVertices);
        drawPolygonRow(p, leftRight.x(), leftRight.y(), color);
    }
    
    private static void drawPolygonRow(PApplet p, List<Vector2> leftPixels, List<Vector2> rightPixels, Vector3 color) {
        for (int i = 0; i < leftPixels.size(); ++i) {
            drawLine(p, leftPixels.get(i), rightPixels.get(i), color);
        }
    }
    
    private static Tuple<List<Vector2>, List<Vector2>> computePolygonRows(List<Vector2> vertexPixels) {
        int ymin = Integer.MAX_VALUE, ymax = Integer.MIN_VALUE;
        
        for (Vector2 v : vertexPixels) {
            if (v.y() < ymin) {
                ymin = v.y();
            }
            if (v.y() > ymax) {
                ymax = v.y();
            }
        }
        
        int size = ymax - ymin + 1;
        
        List<Vector2> leftPixels = new ArrayList<>();
        List<Vector2> rightPixels = new ArrayList<>();
        
        for (int i = 0; i < size; ++i) {
            leftPixels.add(new Vector2(Integer.MAX_VALUE, 0));
            rightPixels.add(new Vector2(Integer.MIN_VALUE, 0));
        }
        
        List<Vector2> line1 = interpolate(vertexPixels.get(0), vertexPixels.get(1));
        List<Vector2> line2 = interpolate(vertexPixels.get(1), vertexPixels.get(2));
        List<Vector2> line3 = interpolate(vertexPixels.get(2), vertexPixels.get(0));
        
        
        for (Vector2 v : line1) {
            if (v.x() < leftPixels.get(v.y() - ymin).x()) {
                leftPixels.set(v.y() - ymin, v);
            }
            if (v.x() > rightPixels.get(v.y() - ymin).x()) {
                rightPixels.set(v.y() - ymin, v);
            }
        }
        
        for (Vector2 v : line2) {
            if (v.x() < leftPixels.get(v.y() - ymin).x()) {
                leftPixels.set(v.y() - ymin, v);
            }
            if (v.x() > rightPixels.get(v.y() - ymin).x()) {
                rightPixels.set(v.y() - ymin, v);
            }
        }
        
        for (Vector2 v : line3) {
            if (v.x() < leftPixels.get(v.y() - ymin).x()) {
                leftPixels.set(v.y() - ymin, v);
            }
            if (v.x() > rightPixels.get(v.y() - ymin).x()) {
                rightPixels.set(v.y() - ymin, v);
            }
        }
        
        return new Tuple<>(leftPixels, rightPixels);
    }

    private static int max(int a, int b) {
        return a > b ? a : b;
    }

    private static int abs(int i) {
        return i < 0 ? -i : i;
    }

}
