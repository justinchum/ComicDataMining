import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
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
  private static String downloadTemplate = "http://readcomics.website/uploads/manga/%s/chapters/%s/%s";
  private static String urlAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";
  private static String imageRegex = "(?is).*var\\s+pages\\s+=\\s+\\[([^\\]]*).*";
  private static Pattern imagePattern = Pattern.compile(imageRegex);
  private static final String imgPaths = "//script";
  private static final String outputPath = "C:\\Users\\%USERNAME%\\Downloads\\";

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

  private static void downloadImage(final String image) {
    System.out.println(image);
    if (image == null || image.isEmpty()) {
      return;
    }
    URLConnection openConnection = null;
    try {

      final String temp = pageURL.substring(0, pageURL.lastIndexOf("/"));
      final String chapter =  pageURL.substring(pageURL.lastIndexOf("/") + 1);
      final String comic = temp.substring(temp.lastIndexOf("/")+1);
      final String outputDirPath = String.format("%s%s_%s", outputPath, comic, chapter);

      final File outputDir = new File(outputDirPath);
      if (!outputDir.exists()) {
        outputDir.mkdirs();
      }
      else if (!outputDir.isDirectory()) {
        System.out.println("The path is not a directory. " + outputDirPath);
      }
      openConnection = new URL(String.format(downloadTemplate, comic, chapter, image)).openConnection();
      openConnection.addRequestProperty("User-Agent", urlAgent);
      final InputStream in = openConnection.getInputStream();
      Files.copy(in, Paths.get(outputDirPath+"\\"+image));

    }
    catch (final Exception e1) {
      System.out.println("Can not open URL " + image);
    }
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
            downloadImage(jsonArray.getJSONObject(i).getString("page_image"));
          }
        }
      }
    }
    else {
      System.out.println("Unable to parse script nodes from " + pageURL);
    }
  }
}
