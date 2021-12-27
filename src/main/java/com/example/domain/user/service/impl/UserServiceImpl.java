package com.example.domain.user.service.impl;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.security.cripto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.user.model.MUser;
import com.example.domain.user.service.UserService;
import com.example.repository.UserRepository;


@Service
@Primary
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder encoder;


    @Transactional
    @Override
    public void signup(MUser user) {

        boolean exists = repository.existsByld(user.getUserId());
        if(exists) {
            throw new DataAccessException("ユーザーが既に存在") {};
        }

        // パスワードの暗号化
        String rawPassword = user.getPassword();
        user.setPassword(encoder.encode(rawPassword));

        // insert
        repository.save(user);
    }


    /** ユーザー取得 */
    @Override
    public List<MUser> grtUsers(MUser user){
        //検索条件
        ExampleMacther matcher = ExampleMatcher
                .matching()
                .withStringMatcher(StringMatcher.CONTAINING)
                .withIgnoreCase();

        return repository.findAll(Example.of(user, matcher));

    }

    /** ユーザー取得(1件) */
    @Override
    public MUser getUserOne(String userId) {
        Optional<MUser> option = repository.findById(userId);
        MUser user = option.orElse(null);
        return user;
    }

    /** ユーザー更新(1件) */
    @Transactional
    @Override
    public void updateUserOne(String userId, String password, String userName) {

        // パスワード暗号化
        String encryptPassword = encoder.encode(password);

        // ユーザー更新
        repository.updateUsere(userId, encryptPassword, userName);
    }

    /** ユーザー削除(1件) */
    @Transactional
    @Override
    public void deleteUserOne(String userId) {
        repository.deleteById(userId);
    }

    /** ログインユーザー取得 */
    @Override
    public MUser getLoginUser(String userId) {
        return repository.findLoginUser(userId);
    }
}