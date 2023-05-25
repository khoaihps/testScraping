import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import java.text.Normalizer;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.select.*;
import org.jsoup.nodes.*;

import java.io.IOException;
import org.jsoup.HttpStatusException;

public class Scraper {
    private static String sourceUrl;
    private static Queue<String> queue;
    private static Set<String> addedUrls;

    static {
        queue = new LinkedList<>();
        addedUrls = new HashSet<>();
    }

    public static String removeAccentsAndToLowercase(String text) {
        String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String withoutAccents = pattern.matcher(normalizedText).replaceAll("");
        String replacedText = withoutAccents.replace("đ", "d").replace("Đ", "D");
        return replacedText.toLowerCase();
    }

    public static void addUrlToQueue(String url) {
        if (addedUrls.contains(url)) {
            return;
        }
        addedUrls.add(url);
        queue.add(url);
    }

    public static void scrapePerson(String url) throws Exception  {
        Document doc = Jsoup.connect(url).get(); //Establising connection

        String title = doc.title();
        System.out.println("Title: "+title);  // Console log

        Person p = new Person();
        String label = "Name";
        ArrayList<String> info = new ArrayList<String>();

        String name = doc.select("h2[itemprop=headline]").text();
        p.setName(name);

        Element table = doc.selectFirst("table.infobox"); // select the table element with class "infobox"
        if (table != null) {
            // Map<String, ArrayList<String>> infoMap = new HashMap<String, ArrayList<String>>();
            Elements rows = table.select("tr");
            for (Element row : rows) {
                label = row.select("th[scope=row]").text();

                if (label.isEmpty()) {
                    continue; 
                } 

                info = new ArrayList<>();
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
                        } 
                    } else info.add(row.select("td").text());
                }
                if (label.equals("Sinh")) {
                    p.setBirth(info);
                } else
                if (label.equals("Mất")) {
                    p.setDeath(info);
                } else
                p.infoList.add(new Pair(label,info));
            }
        }

        //  Scrape description
        info = new ArrayList<>();
        Element firstPTag = doc.selectFirst("p");
        if (firstPTag != null)  {
            String text = firstPTag.ownText();
            info.add(text);
            Element nextElement = firstPTag.nextElementSibling();
            while (nextElement != null && !nextElement.tagName().equals("h2")) {
                text = nextElement.ownText();
                if (!text.isEmpty()) info.add(text);
                nextElement = nextElement.nextElementSibling();
            }
            p.setDescription(info);
        }

        // p.writePersonToJson(p);
        String generatedPath = url.substring(url.lastIndexOf("/") + 1);
        p.writePersonToJsonFile(p,"./data/nhan-vat/"+generatedPath+".json");

        // Push link to queue
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            if (href.length() == 1 || href.charAt(0) != '/') continue;
            addUrlToQueue(sourceUrl+href);
        }
    }

    public static void scrapeOthers(String url) throws Exception  {
        Document doc = Jsoup.connect(url).get();
        // ...
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            if (href.length() == 1 || href.charAt(0) != '/') continue;
            addUrlToQueue(sourceUrl+href);
        }
    }

    public void scrapeData() throws Exception  {
        // Init the first url
        sourceUrl = "https://nguoikesu.com";
        addUrlToQueue(sourceUrl + "/nhan-vat/nguyen-du");

        while (!queue.isEmpty()) {
            String currentUrl = queue.poll();
            // Perform scraping and log to console
            System.out.println("Processing URL: " + currentUrl);  // Log to console

            String targetUrl = "https://nguoikesu.com/nhan-vat/";
            if (currentUrl.startsWith(targetUrl)) {
                try {
                    scrapePerson(currentUrl);
                } catch (HttpStatusException e) {
                    System.out.println("Error fetching URL: " + currentUrl + ", Status: " + e.getStatusCode());
                    // ...
                } catch (IOException e) {
                    System.out.println("IOException occurred: " + e.getMessage());
                    // ...
                }
            } else {
                try {
                    scrapeOthers(targetUrl);
                } catch (HttpStatusException e) {
                    System.out.println("Error fetching URL: " + currentUrl + ", Status: " + e.getStatusCode());
                    // ...
                } catch (IOException e) {
                    System.out.println("IOException occurred: " + e.getMessage());
                    // ...
                }
            }
        }
    }

}
