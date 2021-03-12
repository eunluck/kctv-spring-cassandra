package com.kctv.api.repository.user;

import com.google.common.base.Strings;
import com.kctv.api.model.user.UserInfoByEmailEntity;
import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.util.AES256Util;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.data.cassandra.core.cql.RowMapper;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class UserByEmailCustomRepositoryImpl implements UserByEmailCustomRepository{


    @Autowired
    private CqlTemplate cqlTemplate;

    @Autowired
    private AES256Util aes256Util;


    @SneakyThrows
    @Override
    public Optional<UserInfoByEmailEntity> findByUserEmailAndUserEmailType(String email, String emailType) {
        String enEmail = aes256Util.encrypt(email);

        List<UserInfoByEmailEntity> user = cqlTemplate.query("SELECT * FROM userinfo_by_email where email = ? AND email_type =?",userByEmailEntityRowMapper,new Object[] {enEmail,emailType});

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
        return Optional.of(user.get(0));
        }
    }
    @SneakyThrows
    @Override
    public Optional<UserInfoByEmailEntity> findByUserSnsKey(String snsKey) {
        String enEmail = aes256Util.encrypt(snsKey);
        List<UserInfoByEmailEntity> user = cqlTemplate.query("SELECT * FROM userinfo_by_email where user_sns_key = ?",userByEmailEntityRowMapper,enEmail);

        if (user.isEmpty()){
            return Optional.empty();
        }else {
            try {
                aes(user.get(0), aes256Util);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            return Optional.of(user.get(0));
        }
    }

    static RowMapper<UserInfoByEmailEntity> userByEmailEntityRowMapper = (rs, rowNum) ->
            new UserInfoByEmailEntity(rs.getString("email"),
                    rs.getString("email_type"),
                    rs.getUuid("user_id"),
                    rs.getString("user_password"),
                    rs.getString("user_sns_key")
                   );


    public static void aes(UserInfoByEmailEntity user, AES256Util aes256Util) throws GeneralSecurityException, UnsupportedEncodingException {


        if (!Strings.isNullOrEmpty(user.getUserEmail())&&aes256Util.isBase64(user.getUserEmail())){
            user.setUserEmail(aes256Util.decrypt(user.getUserEmail()));
        }
        if (!Strings.isNullOrEmpty(user.getUserSnsKey())&&aes256Util.isBase64(user.getUserSnsKey())){
            user.setUserSnsKey(aes256Util.decrypt(user.getUserSnsKey()));
        }
    }

}
