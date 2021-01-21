package com.kctv.api.repository.faq;

import com.kctv.api.entity.admin.FaqTable;
import com.kctv.api.entity.place.WifiInfo;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FaqRepository extends CassandraRepository<FaqTable, UUID> {



    Optional<FaqTable> findByFaqId(UUID id);

}
