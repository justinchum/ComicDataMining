import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 * Sample URL - http://readcomics.website/uploads/manga/infinity-tpb-2014/chapters/1/374.jpg
 * @author Justin
 *
 */
public class ComicDataminer {

  protected static String urlTemplate = "http://readcomics.website/uploads/manga/infinity-tpb-2014/chapters/1/%s.jpg";
  protected static String urlAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";

  public static void main(final String[] args) {
    // TODO Auto-generated method stub

    final TagNode tagNode = new HtmlCleaner().clean(
        "<div><table><td id='1234 foo 5678'>Hello</td>");
    //org.w3c.dom.Document doc = new DomSerializer(
    //        new CleanerProperties()).createDOM(tagNode);

    for(int i = 1; i <= 866; i++ ) {

      final String url = String.format(urlTemplate, i < 10 ? "0" + i : i);

      URLConnection openConnection = null;
      try {
        openConnection = new URL(url).openConnection();
        openConnection.addRequestProperty("User-Agent", urlAgent);
      }
      catch (final Exception e1) {
        System.out.println("Can not open URL " + i);
      }

      if (openConnection != null) {
        try(InputStream in = openConnection.getInputStream()){
          Files.copy(in, Paths.get("C:\\Users\\Justin\\Downloads\\InfinityTest\\"+i+".jpg"));
        }
        catch (final Exception e1) {
          System.out.println("Can not get URL " + i);
        }
      }

    }

  }
}
