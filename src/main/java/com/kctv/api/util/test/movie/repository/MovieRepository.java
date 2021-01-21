package com.kctv.api.util.test.movie.repository;

import com.kctv.api.util.test.movie.entity.Movie;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface MovieRepository extends CassandraRepository<Movie, UUID> {
  
  // Movie insert(Movie movie);
}