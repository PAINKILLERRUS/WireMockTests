package org.example.generator;

import com.github.javafaker.Faker;
import org.example.model.Comment;
import org.example.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ObjectsGenerator {

    public static List<Post> generatingPostsAccordingToASpecifiedNumber(int count) {

        List<Post> postList = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i <= count; i++) {
            int threeDigitNumber = random.ints(1, 100).findFirst().getAsInt();
            Post post = new Post()
                    .setId(threeDigitNumber)
                    .setBody("Body: ".concat(Integer.toString(threeDigitNumber)))
                    .setTitle("My post: ".concat(Integer.toString(threeDigitNumber)))
                    .setUserId(threeDigitNumber);

            postList.add(post);
        }

        return postList;
    }

    public static List<Comment> generatingCommentsAccordingASpecifiedNumber(int count){

        List<Comment> commentList = new ArrayList<>();
        Random random = new Random();
        Faker faker = new Faker(new Locale("en"));

        for (int i = 0; i <= count; i++) {
            int threeDigitNumber = random.ints(1, 100).findFirst().getAsInt();
            Comment comment = new Comment()
                    .setPostId(threeDigitNumber)
                    .setId(threeDigitNumber)
                    .setName("Name: ".concat(Integer.toString(threeDigitNumber)))
                    .setEmail(faker.internet().emailAddress())
                    .setBody("Body: ".concat(Integer.toString(threeDigitNumber)));

            commentList.add(comment);
        }

        return commentList;
    }
}
