package testing;

import calibration.Helper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

class Test {

  public static void main(String[] args) {

    GsonBuilder builder = new GsonBuilder();
    Path path;
    Gson gson = builder.create();

    {
      Rectangle rectangle = new Rectangle(100, 100);
      Polygon polygon = new Polygon(0, 0, 50, 50, 0, 50);
      path = (Path) Shape.subtract(rectangle, polygon);

      System.out.println(path.getFillRule());
      System.out.println(path);
      String s = Helper.convertPathToString(path);
      System.out.println(s);
      System.out.println(Helper.convertStringToPath(s));


    }

//    try {
//      String completePathObject = gson.toJson(path);
//      System.out.println(completePathObject);
//    } catch (IllegalArgumentException e) {
//      e.printStackTrace();
//      // java.lang.IllegalArgumentException: class com.sun.javafx.util.WeakReferenceQueue$ListEntry declares multiple JSON fields named next
//    }
//
//    try {
//      String pathObjectElements = gson.toJson(path.getElements());
//      System.out.println(pathObjectElements);
//    } catch (IllegalArgumentException e) {
//      e.printStackTrace();
//      // java.lang.IllegalArgumentException: class com.sun.javafx.util.WeakReferenceQueue$ListEntry declares multiple JSON fields named next
//    }
//
//    try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("test.set"))) {
//      objectOutputStream.writeObject(path);
//    } catch (IOException e) {
//      e.printStackTrace();
////      java.io.NotSerializableException: javafx.scene.shape.Path
//    }
//
//    try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("test.set"))) {
//      objectOutputStream.writeObject(path.getElements());
//    } catch (IOException e) {
//      e.printStackTrace();
////      java.io.NotSerializableException: javafx.scene.shape.Path$2
//    }
  }
}
