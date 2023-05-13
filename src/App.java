
// import java.text.AttributedCharacterIterator.Attribute;
// import java.util.jar.Attributes;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.select.*;
import org.jsoup.nodes.*;


public class App {
    public static void main(String[] args) throws Exception {
        String url = "https://nguoikesu.com";
        Document doc = Jsoup.connect(url+"/nhan-vat/le-dai-hanh").get();

        String title = doc.title();
        System.out.println("Title: "+title);

        Element type = doc.select("h1").first();
        System.out.println("Type: " + type.text());

        Person p = new Person(new ArrayList<Pair>());
        String label = "Name";
        ArrayList<String> info = new ArrayList<String>();


        String name = doc.select("h2[itemprop=headline]").text();
        info.add(name);
        p.infoList.add(new Pair(label,info));

        Element table = doc.selectFirst("table.infobox"); // select the table element with class "infobox"
        if (table != null) {

            // Map<String, ArrayList<String>> infoMap = new HashMap<String, ArrayList<String>>();
            Elements rows = table.select("tr");

            for (Element row : rows) {

                label = row.select("th[scope=row]").text();
                if (label.isEmpty()) {
                    continue; // skip rows without a label
                } 

                info = new ArrayList<String>();
                Elements liElements = row.select("td li");
                if (liElements.size() > 0) {
                    // if there are li elements, get their text and concatenate with line breaks
                    for (Element li : liElements) {
                        info.add(li.text());
                    }
                } else {
                    // otherwise, get the text of the td element
                    Elements brElements = row.select("td br");
                    if (brElements.size() > 0) {
                        row = row.select("td").get(row.select("td").size()-1);;
                        String tdContent = row.select("td").html();
                        String lines[] = tdContent.split("<br>");
                        for (String line : lines) {
                            info.add(Jsoup.parse(line).text());
                            // System.out.println(Jsoup.parse(line).text());
                        } 
                    } else info.add(row.select("td").text());
                }
                p.infoList.add(new Pair(label,info));
            }
        }
        // p.writePersonToJson(p);
        p.writePersonToJsonFile(p,"./data/"+name+".json");

        // Push link to queue

        Queue<String> queue = new LinkedList<String>();
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            if (href.length() == 1 || href.charAt(0) != '/') continue;
            queue.add(url+href);
            System.out.println(url+href);
        }
    }
}


