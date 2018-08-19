package testing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

class Test {

  public static void main(String[] args) {

    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    Path path = new Path(new MoveTo(0.0f, 50.0f), new LineTo(100.0f, 100.0f));

    try {
      String completePathObject = gson.toJson(path);
      System.out.println(completePathObject);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      // java.lang.IllegalArgumentException: class com.sun.javafx.util.WeakReferenceQueue$ListEntry declares multiple JSON fields named next
    }

    try {
      String pathObjectElements = gson.toJson(path.getElements());
      System.out.println(pathObjectElements);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      // java.lang.IllegalArgumentException: class com.sun.javafx.util.WeakReferenceQueue$ListEntry declares multiple JSON fields named next
    }

    try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("test.set"))) {
      objectOutputStream.writeObject(path);
    } catch (IOException e) {
      e.printStackTrace();
//      java.io.NotSerializableException: javafx.scene.shape.Path
    }

    try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("test.set"))) {
      objectOutputStream.writeObject(path.getElements());
    } catch (IOException e) {
      e.printStackTrace();
//      java.io.NotSerializableException: javafx.scene.shape.Path$2
    }
  }
}
