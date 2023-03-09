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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
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

    private static Post getPost(Element row) {
        Post post = new Post();
        Element titleElement = row.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        Element dateElement  = row.select(".vacancy-card__date").first().child(0);
        String vacancyName = titleElement.text();
        String sourceLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        String date = dateElement.attr("datetime");
        DateTimeParser hcdtp = new HabrCareerDateTimeParser();
        LocalDateTime dateFormat = hcdtp.parse(date);
        String description = new HabrCareerParse(hcdtp).retrieveDescription(sourceLink);
        post.setTitle(vacancyName);
        post.setLink(sourceLink);
        post.setDescription(description);
        post.setCreated(dateFormat);
        return post;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> list = new ArrayList<>();
        for (int i = 1; i < 2; i++) {
            Connection connection = Jsoup.connect(link + i);
            Elements rows = null;
            try {
                rows = connection.get().select(".vacancy-card__inner");
            } catch (IOException e) {
                e.printStackTrace();
            }
            rows.forEach(row -> {
                list.add(getPost(row));
            });
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        HabrCareerParse habrCareerParse = new HabrCareerParse(new HabrCareerDateTimeParser());
        List<Post> list = habrCareerParse.list(PAGE_LINK);
        list.forEach(System.out::println);
    }
}
