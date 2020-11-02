package com.kctv.api.config.database;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories
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
        System.out.println("키스페이스이름 :"+ keySpace);
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
}
