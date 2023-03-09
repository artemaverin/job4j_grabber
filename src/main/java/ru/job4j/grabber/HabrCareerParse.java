package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import javax.print.Doc;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

public class HabrCareerParse {
    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

    public static void main(String[] args) throws IOException {
        for (int i = 1; i < 6; i++) {
            System.out.println("Page # " + i);
            Connection connection = Jsoup.connect(PAGE_LINK + i);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement  = row.select(".vacancy-card__date").first().child(0);
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String date = dateElement.attr("datetime");
                DateTimeParser hcdtp = new HabrCareerDateTimeParser();
                LocalDateTime dateFormat = hcdtp.parse(date);
                String description = new HabrCareerParse().retrieveDescription(link);
                System.out.printf("%s %s %s %s%n", vacancyName, link, dateFormat, description);
            });
        }

    }

    private String retrieveDescription(String link) {
        Connection connection = Jsoup.connect(link);
        String description = "";
        try {
            description = connection.get().select(".vacancy-description__text").text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return description;
    }
}
