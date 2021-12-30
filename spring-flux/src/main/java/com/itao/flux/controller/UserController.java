package com.itao.flux.controller;

import java.time.LocalDateTime;

import com.itao.flux.bean.User;
import com.itao.flux.repository.UserRepository;
import com.itao.flux.util.SnowflakeIdHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.sql.In;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;
import static org.springframework.data.relational.core.query.Update.update;

@RestController
@AllArgsConstructor
@Slf4j
public class UserController {


    private R2dbcEntityTemplate template;

    @GetMapping("/user/all")
    public Flux<User> getUsers() {
        return template
                .select(User.class)
                .all()
                .log()
                .doOnNext(user -> log.info("{}", user));
    }

    @PostMapping("/user")
    public Mono<User> saveUser(@RequestBody User user) {
        //user.setId(SnowflakeIdHelper.getId());
        return template.insert(user);
        //return template.insert(User.class).using(user);
    }

    @GetMapping("/user/{name}")
    public Mono<User> getUserByName(@PathVariable String name) {
        return template.selectOne(query(where("name").is(name)), User.class);
    }

    @PostMapping("/user/update")
    public Mono<User> updateUser(@RequestBody User user) {
        return template.update(user);
    }

    @PostMapping("/user/update/{newName}/{oldName}")
    public Mono<Integer> updateUserByName(@PathVariable String newName, @PathVariable String oldName) {
        return template
                .update(User.class)
                .matching(query(where("name").is(oldName)))
                .apply(update("name", newName));
    }

    @GetMapping("/user/database")
    public Flux<User> database() {
        return template.getDatabaseClient().sql("select * from t_user where name = :name")
                //bind(0, "sjt") //"select * from t_user where name = ?"
                .bind("name", "sjt")
                .map(row -> {
                    User user = new User();
                    user.setId(row.get("id", String.class));
                    user.setName(row.get("name", String.class));
                    user.setAge(row.get("age", Integer.class));
                    user.setDeleted(Objects.requireNonNullElse(row.get("deleted", Integer.class), 0) != 0);
                    user.setCreateTime(row.get("create_time", LocalDateTime.class));
                    user.setModifyTime(row.get("modify_time", LocalDateTime.class));
                    return user;
                })
                .all();

    }
}
