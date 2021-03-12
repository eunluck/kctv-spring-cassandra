package com.kctv.api.config.database;


import com.kctv.api.model.user.UserInfoEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.event.BeforeConvertCallback;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.data.convert.CustomConversions;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CassandraConfiguration extends AbstractCassandraConfiguration {

    @Value("${spring.data.cassandra.keyspace-name}")
    private String keySpace;
    @Value("${spring.data.cassandra.port}")
    private int port;
    @Value("${spring.data.cassandra.local-datacenter}")
    private String localDatacenter;
    @Value("${spring.data.cassandra.contact-points}")
    private String  contactPoints;
    @Value("${spring.data.cassandra.username}")
    private String  username;



    @Override
    protected String getKeyspaceName() {

        return keySpace;
    }

    @Override
    protected String getLocalDataCenter() {
        return localDatacenter;
    }


    @Override
    protected String getContactPoints() {
        return contactPoints;
    }

    @Override
    protected int getPort() {
        return port;
    }


/*

    @Override
    public CassandraCustomConversions customConversions() {

        List<Converter<?,?>> converters = new ArrayList<>();
        converters.add(new Aes256WritingConverter());
        converters.add(new Aes256ReadingConverter());

        System.out.println("converter등록");

        return new CassandraCustomConversions(converters);
    }
*/


}
