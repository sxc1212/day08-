package com.atguigu.yygh.hosp.testmongo;

import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/mongo1")
public class TestMongo1 {
    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("creat")
    public void creatUser() {
        User user = new User();
        user.setAge(20);
        user.setName("test");
        user.setEmail("11223344@qq.com");
        User insert = mongoTemplate.insert(user);
        System.out.println("insert = " + insert);
    }

    @GetMapping("findAll")
    public void findAll() {
        List<User> users = mongoTemplate.findAll(User.class);
        users.forEach(System.out::println);
    }

    @GetMapping("findById")
    public void findById() {
        User user = mongoTemplate.findById("62d7eacde523b40043a4d2db", User.class);
        System.out.println("user = " + user);
    }

    @GetMapping("findUser")
    public void findUserList() {
        Query query = new Query(Criteria.where("name").is("test").and("age").is(20));
        List<User> users = mongoTemplate.find(query, User.class);
        users.forEach(System.out::println);
    }

    @GetMapping("findLike")
    public void findUsersLikeName() {
        String name = "est";
        String regex = String.format("%s%s%s", "^.*", name, ".*$");
        Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Query query = new Query(
                Criteria.where("name").regex(pattern));
        List<User> users = mongoTemplate.find(query,User.class);
        users.forEach(System.out::println);
    }

    @GetMapping("delete")
    public void delete() {
        Query query =new Query(Criteria.where("_id").is("62d7eacde523b40043a4d2db"));
        DeleteResult remove = mongoTemplate.remove(query, User.class);
        long deletedCount = remove.getDeletedCount();
        System.out.println("deletedCount = " + deletedCount);
    }
}
