package se.graphics.cornell;

import static se.graphics.cornell.Vector3.*;
import static se.graphics.cornell.Intersection.*;

import processing.core.PApplet;

public final class Lab1 {

    private final static float LIGHT_INTENSITY = 14f;
    private final static Vector3 LIGHT_POSITION = new Vector3(0f, -0.5f, -0.7f);
    private final static Vector3 LIGHT_COLOR = ones().times(LIGHT_INTENSITY);
    private final static Vector3 INDIRECT_LIGHT = ones().times(0);
    private final static float PI = 3.141592653589793238f;
    
    private Lab1() {
        
    }

    public static void draw(PApplet p, int size, float f, Vector3 C, Matrix3 R) {
        for (int y = 0; y < size; ++y) {
            for (int x = 0; x < size; ++x) {
                Vector3 direction = R.times(vec3(x - size / 2, y - size / 2, f));
                Intersection intersection = getIntersection(C, direction);
                
                if (intersection.valid()) {
                    Vector3 color = Loader.cornellBox().get(intersection.index()).color().entrywiseDot(getDirectLight(intersection));
//                    Vector3 color = intersection.position().plus(ones());
                    p.stroke(color.x() * 255, color.y() * 255, color.z() * 255);
                    p.point(x, y);
                }
            }
        }
    }
    
    public static void drawLowRes(PApplet p, int size, int resolution, float f, Vector3 C, Matrix3 R) {
        int ratio = size / resolution;
        
        for (int y = 0; y < resolution; ++y) {
            for (int x = 0; x < resolution; ++x) {
                Vector3 direction = R.times(vec3(x - resolution / 2, y - resolution / 2, f));
                Intersection intersection = getIntersection(C, direction);
                
                if (intersection.valid()) {
                    Vector3 color = Loader.cornellBox().get(intersection.index()).color().entrywiseDot(getDirectLight(intersection));

                    p.fill(color.x() * 255, color.y() * 255, color.z() * 255);                    
                    p.rect(x * ratio, y * ratio, ratio, ratio);
                }
            }
        }
    }
    
    /*
    private static Intersection getIntersection(Vector3 start, Vector3 direction) {
        Intersection intersection = invalidIntersection();
        
        for (int i = 0; i < Loader.cornellBox().size(); ++i) {
            Triangle triangle = Loader.cornellBox().get(i);
            
            Vector3 v1 = triangle.v1();
            Vector3 v2 = triangle.v2();
            Vector3 v3 = triangle.v3();
            
            Vector3 e1 = v2.minus(v1);
            Vector3 e2 = v3.minus(v1);
            Vector3 b = start.minus(v1);
            Matrix3 A = mat3(Vector3.zeros().minus(direction), e1, e2);
            
            Vector3 x = A.inversed().times(b);
            
            float t = x.x();
            float u = x.y();
            float v = x.z();
            
            if (t < intersection.distance() && t > 0 && u >= 0 && v >= 0 && u + v <= 1) {
                Vector3 p = v1.plus(e1.times(u)).plus(e2.times(v));
                intersection = new Intersection(true, p, t, i);
            }
        }
        
        return intersection;
    }
    */
    
    private static Intersection getIntersection(Vector3 start, Vector3 direction) {
        Intersection intersection = invalidIntersection();
        
        direction = direction.normalise();
        
        for (int i = 0; i < Loader.cornellBox().size(); ++i) {
            Triangle triangle = Loader.cornellBox().get(i);
            
            Vector3 v1 = triangle.v1();
            Vector3 v2 = triangle.v2();
            Vector3 v3 = triangle.v3();
                        
            Vector3 n = triangle.normal();
            float a = (v1.minus(start)).dot(n);
            float b = direction.dot(n);
            
            if (b != 0) {
                float d = a / b;
                Vector3 position = start.plus(direction.times(d));
                float t = v2.minus(v1).cross(position.minus(v1)).dot(n);
                float u = v3.minus(v2).cross(position.minus(v2)).dot(n);
                float v = v1.minus(v3).cross(position.minus(v3)).dot(n);
                
                if (d < intersection.distance() && d > 0.00001 && t >= 0 && u >= 0 && v >= 0) {
                    intersection = new Intersection(true, position, d, i);
                }
            }
        }
        
        return intersection;
    }
    
    private static Vector3 getDirectLight(Intersection i) {
        Vector3 p = i.position();
        Vector3 r = p.minus(LIGHT_POSITION);
//        Vector3 r = LIGHT_POSITION.minus(p);        
        Vector3 rhat = r.normalise();
        Triangle t = Loader.cornellBox().get(i.index());
        Vector3 n = t.normal();
        
        float rsz = r.size();
        float f = 1 / (4 * PI * rsz * rsz);
        
        Intersection blocking = getIntersection(p, LIGHT_POSITION.minus(p));
        
        Vector3 returnLight;
        
        if (!blocking.valid() || blocking.distance() > r.size() || blocking.index() == i.index()) {
            returnLight = LIGHT_COLOR.times(max(rhat.dot(n), 0f)).times(f);
        } else {
            returnLight = new Vector3(0f, 0f, 0f);
        }
        
        return returnLight.plus(INDIRECT_LIGHT);
    }

    private static float max(float a, float b) {
        return a > b ? a : b;
    }

}
