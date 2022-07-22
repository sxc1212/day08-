package com.atguigu.yygh.hosp.testmongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mongo2")
public class TestMongo2 {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("create")
    public void create() {
        User user = new User();
        user.setAge(20);
        user.setName("张三");
        user.setEmail("3332200@qq.com");
        User user1 = userRepository.save(user);
        System.out.println("user1 = " + user1);
    }

    @GetMapping("findAll")
    public void findAll() {
        List<User> users = userRepository.findAll();
        users.forEach(System.out::println);
    }

    @GetMapping("findById")
    public void findById() {
        User user = userRepository.findById("62d7ee82e92c3c5433a330ae").get();
        System.out.println("user = " + user);
    }

    @GetMapping("findUser")
    public void findUserList() {
        User user = new User();
        user.setName("张三");
        user.setAge(20);
        Example<User> example = Example.of(user);
        List<User> users = userRepository.findAll(example);
        users.forEach(System.out::println);
    }

    @GetMapping("update")
    public void updateUser() {
        User user = userRepository.findById("62d7ee82e92c3c5433a330ae").get();
        user.setName("张三_1");
        user.setAge(25);
        user.setEmail("883220990@qq.com");
        User save = userRepository.save(user);
        System.out.println(save);
    }
    @GetMapping("findLike")
    public void findUsersLikeName() {
        User user = new User();
        user.setName("三");
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING).withIgnoreCase(true);
        Example<User> example = Example.of(user,exampleMatcher);
        List<User> users = userRepository.findAll(example);
        users.forEach(System.out::println);
    }

    @GetMapping("delete")
    public void delete() {
        userRepository.deleteById("62d7ee82e92c3c5433a330ae");
    }


    @GetMapping("testMethod1")
    public void testMethod1() {
        List users = userRepository.getByNameAndAge("张三",20);
        users.forEach(System.out::println);
    }

    @GetMapping("testMethod2")
    public void testMethod2() {
        List users = userRepository.getByNameLike("三");
        users.forEach(System.out::println);
    }



}

