import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.json.JSONArray;

/**
 * Test code to download entire comics from readcomics
 * Sample URL: http://readcomics.website/uploads/manga/infinity-tpb-2014/chapters/1/01.jpg
 * @author Justin
 *
 */
public class ComicDataMiningPage {

  private static String pageURL = "http://readcomics.website/comic/infinity-tpb-2014/1";
  private static String urlAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";
  private static String imageRegex = "(?is).*var\\s+pages\\s+=\\s+\\[([^\\]]*).*";
  private static Pattern imagePattern = Pattern.compile(imageRegex);
  private static final String imgPaths = "//script";

  private static TagNode node;

  private static HtmlCleaner cleaner;

  protected static HtmlCleaner getHtmlCleaner() {
    if (cleaner == null) {
      cleaner = new HtmlCleaner();
      final CleanerProperties props = cleaner.getProperties();
      props.setAllowHtmlInsideAttributes(true);
      props.setAllowMultiWordAttributes(true);
      props.setRecognizeUnicodeChars(true);
      props.setOmitComments(true);
    }
    return cleaner;
  }

  public static void main(final String[] args) {
    URL url;
    URLConnection conn;

    try {
      url = new URL(pageURL);
      conn = url.openConnection();
      conn.addRequestProperty("User-Agent", urlAgent);
      node = getHtmlCleaner().clean(new InputStreamReader(conn.getInputStream()));
    }
    catch (final MalformedURLException e) {
      System.out.println("Corrupt URL detected");
    }
    catch (final IOException e) {
      System.out.println("Unable to connection to the URL: " + pageURL);
    }

    Object[] scriptNodes = null;
    try {
      scriptNodes = node.evaluateXPath(imgPaths);
    }
    catch (final XPatherException e) {
      System.out.println("Unable to parse script nodes");
    }

    if (scriptNodes != null && scriptNodes.length > 0) {
      Matcher matcher;
      for(final Object script : scriptNodes) {
        final TagNode scriptNode = (TagNode) script;
        if (scriptNode.getAttributeByName("src") == null &&
            scriptNode.getText() != null &&
            (matcher = imagePattern.matcher(scriptNode.getText().toString())).matches()
            ) {

          final JSONArray jsonArray = new JSONArray(String.format("[%s]", matcher.group(1)));
          for (int i = 0; i < jsonArray.length(); i++) {
            System.out.println(String.format("%s/%s", pageURL, jsonArray.getJSONObject(i).getString("page_image")));
          }
        }
      }
    }
    else {
      System.out.println("Unable to parse script nodes from " + pageURL);
    }
  }
}
