package com.kctv.api.repository.user;

import com.google.common.base.Strings;
import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.util.AES256Util;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.data.cassandra.core.cql.RowMapper;
import org.springframework.stereotype.Repository;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository {

    @Autowired
    private CqlTemplate cqlTemplate;

    @Autowired
    private AES256Util aes256Util;

    @SneakyThrows
    @Override
    public List<UserInfoEntity> findAll() {
        List<UserInfoEntity> users = cqlTemplate.query("select * from user_info",userInfoEntityRowMapper);

        for (UserInfoEntity user : users){
           aes(user,aes256Util);
        }
        return users;
    }

    @Override
    public Optional<UserInfoEntity> findByInviteCode(String code) {

        List<UserInfoEntity> user = cqlTemplate.query("SELECT * FROM user_info where invite_code = ? ALLOW FILTERING",userInfoEntityRowMapper,code);

        if (user.isEmpty()){
            return Optional.empty();
        }else {

        try {
            aes(user.get(0),aes256Util);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(user.get(0));
        }
    }

    @Override
    public Optional<UserInfoEntity> findByUserId(UUID id) {

        List<UserInfoEntity> user = cqlTemplate.query("SELECT * FROM user_info where user_id = ? ", userInfoEntityRowMapper, id);

        if (user.isEmpty()){
            return Optional.empty();
        }else {

        System.out.println(user.toString());
        try {
            aes(user.get(0), aes256Util);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return Optional.of(user.get(0));
        }
    }



    public static void aes(UserInfoEntity user, AES256Util aes256Util) throws GeneralSecurityException, UnsupportedEncodingException {


        if (!Strings.isNullOrEmpty(user.getUserEmail())&&aes256Util.isBase64(user.getUserEmail())){
            user.setUserEmail(aes256Util.decrypt(user.getUserEmail()));
        }

        if (!Strings.isNullOrEmpty(user.getUserNickname())&&aes256Util.isBase64(user.getUserNickname())){
            user.setUserNickname(aes256Util.decrypt(user.getUserNickname()));
        }

        if (!Strings.isNullOrEmpty(user.getUserSnsKey())&&aes256Util.isBase64(user.getUserSnsKey())){
            user.setUserSnsKey(aes256Util.decrypt(user.getUserSnsKey()));
        }

        if (!Strings.isNullOrEmpty(user.getUserPhone())&&aes256Util.isBase64(user.getUserPhone())){
            user.setUserPhone(aes256Util.decrypt(user.getUserPhone()));
        }
    }

    static RowMapper<UserInfoEntity> userInfoEntityRowMapper = (rs,rowNum) ->
            new UserInfoEntity(rs.getUuid("user_id"),
            rs.getString("user_email_type"),
            rs.getString("user_email"),
            rs.getString("user_address"),
            rs.getString("user_birth"),
            rs.getString("user_gender"),
            rs.getList("user_mac",String.class),
            rs.getString("user_name"),
            rs.getString("invite_code"),
            rs.getString("user_nickname"),
            rs.getString("user_password"),
            rs.getString("user_phone"),
            rs.getMap("accept",String.class,Boolean.class),
            rs.getString("user_status"),
            rs.getInstant("create_date") !=null ? Date.from(rs.getInstant("create_date")) : null,
            rs.getInstant("update_date") !=null ? Date.from(rs.getInstant("update_date")) : null,
            //Date.from(rs.getInstant("create_date")),
            //Date.from(rs.getInstant("update_date")),
            rs.getString("user_sns_key"),
            rs.getList("roles",String.class));

}

