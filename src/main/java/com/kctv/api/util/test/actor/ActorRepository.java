package com.kctv.api.util.test.actor;


import com.kctv.api.util.test.actor.entity.Actor;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActorRepository extends CassandraRepository<Actor, UUID> {}
