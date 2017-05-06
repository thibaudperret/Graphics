package se.graphics.cornell;

import static se.graphics.cornell.Vector3.ones;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public final class Lab2 {
    
    private static Vector3 C;
    private static Matrix3 R;
    private static float f;
    private static int ratio;
    
    private static float[][] depthBuffer;
    
    private final static Vector3 LIGHT_POSITION = new Vector3(0f, -0.5f, -0.7f);
    private final static Vector3 LIGHT_POWER = ones().times(1.1f);
    private final static Vector3 INDIRECT_LIGHT = ones().times(0.5f); 
    private final static float PI = 3.141592653589793238f;
    
    private Lab2() {
        
    }

    public static void draw(PApplet app, int size, float f, Vector3 C, Matrix3 R) {
        app.fill(255);
        
        Lab2.C = C;
        Lab2.R = R;
        Lab2.f = f;
        Lab2.ratio = 1;

        depthBuffer = new float[size][size];
        
        for (int i = 0; i < Loader.cornellBox().size(); ++i) {
            Triangle t = Loader.cornellBox().get(i);
            drawPolygon(app, t.vertices(), i, t.color());
        }
    }
    
    public static void drawLowRes(PApplet app, int size, int resolution, float f, Vector3 C, Matrix3 R) {
        app.fill(255);
        
        Lab2.C = C;
        Lab2.R = R;
        Lab2.f = f;
        Lab2.ratio = size / resolution;
        
        depthBuffer = new float[resolution][resolution];
        
        for (int i = 0; i < Loader.cornellBox().size(); ++i) {
            Triangle t = Loader.cornellBox().get(i);
            drawPolygon(app, t.vertices(), i, t.color());
        }
    }
    
    private static Vector2 vertexShader(Vertex vertex, Vector3 color) {
        Vector3 ptilde = R.times(vertex.position().minus(C));
        
//        Vector3 r = LIGHT_POSITION.minus(vertex.position());
        Vector3 r = vertex.position().minus(LIGHT_POSITION);
        Vector3 rhat = r.normalise();
        
        float rsz = r.size();
        float f = 1 / (4 * PI * rsz * rsz);
        
        Vector3 ill = LIGHT_POWER.times(max(rhat.dot(vertex.normal()), 0f)).times(f).plus(INDIRECT_LIGHT).entrywiseDot(vertex.reflectance());
        
        return new Vector2((int) (Lab2.f * ptilde.x() / ptilde.z() + Lab2.f / 2), (int) (Lab2.f * ptilde.y() / ptilde.z() + Lab2.f / 2), 1f / (C.z() - vertex.position().z()), ill.entrywiseDot(color));
    }
    
    private static void pixelShader(PApplet p, Vector2 v) {
        if (depthBuffer[v.x()][v.y()] > v.zinv()) {
            p.fill(255 * v.illumination().x(), 255 * v.illumination().y(), 255 * v.illumination().z());
            p.rect(v.x() * ratio, v.y() * ratio, ratio, ratio);
            depthBuffer[v.x()][v.y()] = v.zinv();
        }
    }
    
    private static List<Vector2> interpolate(Vector2 a, Vector2 b) {
        List<Vector2> result = new ArrayList<>();
        int size = max(abs(a.x() - b.x()), abs(a.y() - b.y()));

        float dx = ((float) (b.x() - a.x())) / size;
        float dy = ((float) (b.y() - a.y())) / size;
        float dzinv = (b.zinv() - a.zinv()) / size;
        
        float dix = (b.illumination().x() - a.illumination().x()) / size;
        float diy = (b.illumination().y() - a.illumination().y()) / size;
        float diz = (b.illumination().z() - a.illumination().z()) / size;
        
        for (int i = 0; i < size; ++i) {
            result.add(new Vector2(a.x() + (int) Math.floor(i * dx), a.y() + (int) Math.floor(i * dy), a.zinv() + i * dzinv, a.illumination().plus(new Vector3(dix, diy, diz).times(i))));
        }
        
        result.add(b);
        
        return result;
    }
    
    private static void drawLine(PApplet p, Vector2 a, Vector2 b) {
        List<Vector2> result = interpolate(a, b);

        for (Vector2 v : result) {
            pixelShader(p, v);
        }
    }
    
    /*
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
    */
    
    private static void drawPolygon(PApplet p, List<Vector3> vertices, int triangleIndex, Vector3 color) {
        int v = vertices.size();
        List<Vector2> projectedVertices = new ArrayList<>();

        for (int i = 0; i < v; ++i) {
            Vertex vertex = new Vertex(vertices.get(i), Loader.cornellBox().get(triangleIndex).normal(), ones());
            projectedVertices.add(vertexShader(vertex, color));
        }
        
        Tuple<List<Vector2>, List<Vector2>> leftRight = computePolygonRows(projectedVertices);
        drawPolygonRow(p, leftRight.x(), leftRight.y());
    }
    
    private static void drawPolygonRow(PApplet p, List<Vector2> leftPixels, List<Vector2> rightPixels) {
        for (int i = 0; i < leftPixels.size(); ++i) {
            drawLine(p, leftPixels.get(i), rightPixels.get(i));
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
            leftPixels.add(new Vector2(Integer.MAX_VALUE, 0, 0f, null));
            rightPixels.add(new Vector2(Integer.MIN_VALUE, 0, 0f, null));
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

    private static float max(float a, float b) {
        return a > b ? a : b;
    }

    private static int max(int a, int b) {
        return a > b ? a : b;
    }

    private static int abs(int i) {
        return i < 0 ? -i : i;
    }

}
