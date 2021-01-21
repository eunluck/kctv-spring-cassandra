package com.kctv.api.util.test.movie.repository;


import com.kctv.api.util.test.movie.entity.MovieByYear;
import com.kctv.api.util.test.movie.entity.MovieByYearKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MovieByYearRepository extends CassandraRepository<MovieByYear, MovieByYearKey> {

  List<MovieByYear> findByKeyYearAndKeyReleaseDateAndKeyMovieId(int year, LocalDateTime releaseDate, UUID movieId);
}