package com.kctv.api.entity.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@Table(value = "tags")
public class Tag {

    @PrimaryKeyColumn(value = "tag_type",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
    private final String tagType;
    @PrimaryKeyColumn(value = "tag_name",ordinal = 1,type = PrimaryKeyType.CLUSTERED)
    private final String tagName;



    public Tag(final String tagType,final String tagName) {
        this.tagType = tagType;
        this.tagName = tagName;
    }



    public class TagList{

        private List<Map<String, List<String>>> tagList = new ArrayList<>();

        public void TagToList(List<Tag> tags){




            List<Map<String, List<String>>> we = new ArrayList<>();


            this.tagList = we;

        }
    }
}
