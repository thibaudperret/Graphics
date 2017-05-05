package se.graphics.cornell;

import static se.graphics.cornell.Vector3.ones;
import static se.graphics.cornell.Vector3.vec3;
import static se.graphics.cornell.Intersection.*;

import processing.core.PApplet;

public final class Lab3 {
    
    private final static float LIGHT_INTENSITY = 500f;
    private final static Vector3 LIGHT_POSITION = new Vector3(0f, 0f, -5f);
    private final static Vector3 LIGHT_COLOR = ones().times(LIGHT_INTENSITY);
    private final static Vector3 INDIRECT_LIGHT = ones().times(0);
    private final static float PI = 3.141592653589793238f;

    private Lab3() {
        
    }
    
    public static void draw(PApplet p, int size, float f, Vector3 C, Matrix3 R) {
        for (int y = 0; y < size; ++y) {
            for (int x = 0; x < size; ++x) {
                Vector3 direction = R.times(vec3(x - size / 2, y - size / 2, f));
                Intersection intersection = getIntersection(C, direction);
                
                if (intersection.valid()) {
                    Vector3 color = Loader.thibaudBox().get(intersection.index()).color().entrywiseDot(getDirectLight(intersection));
//                    Vector3 color = getDirectLight(intersection);
//                    Vector3 color = ones().times(intersection.distance() / 2f).normalise().plus(ones());
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
                    Vector3 color = Loader.thibaudBox().get(intersection.index()).color().entrywiseDot(getDirectLight(intersection));
//                    Vector3 color = getDirectLight(intersection);
//                    Vector3 color = ones().times(intersection.distance() / 2f).normalise().plus(ones());
                    p.fill(color.x() * 255, color.y() * 255, color.z() * 255);                    
                    p.rect(x * ratio, y * ratio, ratio, ratio);
                }
            }
        }
    }

    private static Intersection getIntersection(Vector3 start, Vector3 direction) {
        Intersection intersection = invalidIntersection();
        
        direction = direction.normalise();
        
        for (int i = 0; i < Loader.thibaudBox().size(); ++i) {
            Sphere sphere = Loader.thibaudBox().get(i);
            
            Vector3 center = sphere.c();
            float r = sphere.r();
            
            Vector3 v = start.minus(center);
            
            float b = 2f * (direction.dot(v));
            float c = v.dot(v) - r * r;
            
            float delta = b * b - 4 * c;
            
            if (delta >= 0) {
                float d = (float) (- (b + Math.sqrt(delta)) / 2f);
//                if (d < 0) {
//                    d = (float) (- (b - Math.sqrt(delta)) / 2f);
//                }
                
                if (d < intersection.distance() && d > 0) {
                    Vector3 position = start.plus(direction.times(d));
                    intersection = new Intersection(true, position, d, i);
                }
            }
        }
        
        return intersection;
    }
    
    private static Vector3 getDirectLight(Intersection i) {
        Vector3 p = i.position();
        Vector3 r = p.minus(LIGHT_POSITION);
    
        Vector3 rhat = r.normalise();
        Sphere s = Loader.thibaudBox().get(i.index());
        Vector3 n = s.c().minus(p).normalise();
        
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
