import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ComicDataminer {
  public static void main(final String[] args) {
    // TODO Auto-generated method stub
    for(int i = 1; i <= 866; i++ ) {

      final String url = "http://readcomics.website/uploads/manga/infinity-tpb-2014/chapters/1/"+
          (i < 10 ? "0" + i : i) +".jpg";
      //      url = new URL("http://vancouver.gasbuddy.com/index.aspx?fuel=A&area=Richmond");
      //      is = url.openStream();

      URLConnection openConnection = null;
      try {
        openConnection = new URL(url).openConnection();
        openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
      }
      catch (final Exception e1) {
        System.out.println("Can not open URL " + i);
      }

      if (openConnection != null) {
        try(InputStream in = openConnection.getInputStream()){
          Files.copy(in, Paths.get("C:\\Users\\Justin\\Downloads\\Infinity\\"+i+".jpg"));
        }
        catch (final Exception e1) {
          System.out.println("Can not get URL " + i);
        }
      }


    }
    //    http://readcomics.website/uploads/manga/infinity-tpb-2014/chapters/1/374.jpg
  }
}
