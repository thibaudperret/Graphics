package se.graphics.cornell;

import java.util.Arrays;
import java.util.List;

public class Test {
    
    public static void main(String[] agrs) {
        List<Vector2> vertexPixels = Arrays.asList(new Vector2(10, 5), new Vector2(5, 10), new Vector2(15, 15));
        Tuple<List<Vector2>, List<Vector2>> leftRight = Lab2.computePolygonRows(vertexPixels);
        
        for (int i = 0; i < leftRight.x().size(); ++i) {
            System.out.println(leftRight.x().get(i) + " " + leftRight.y().get(i));
        }
    }

}
