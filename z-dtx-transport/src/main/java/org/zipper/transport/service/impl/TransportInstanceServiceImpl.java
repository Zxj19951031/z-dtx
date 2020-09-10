package org.zipper.transport.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.zipper.transport.mapper.TransportInstanceMapper;
import org.zipper.transport.pojo.entity.TransportInstance;
import org.zipper.transport.pojo.vo.TransportInstanceVO;
import org.zipper.transport.service.TransportInstanceService;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhuxj
 * @since 2020/09/08
 */
@Slf4j
@Service
public class TransportInstanceServiceImpl implements TransportInstanceService {

    @Resource
    private TransportInstanceMapper transportInstanceMapper;

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public long addOne(TransportInstance instance) {
        int record = transportInstanceMapper.insert(instance);
        return instance.getId();
    }

    @Override
    public void updateOne(TransportInstance instance) {
        transportInstanceMapper.updateOne(instance);
    }

    @Override
    public PageInfo<TransportInstanceVO> queryByTransportId(Integer transportId, Integer pageNum, Integer pageSize) {
        if (pageNum > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<TransportInstanceVO> list = transportInstanceMapper.selectByTransportId(transportId);
        return PageInfo.of(list);
    }

    @Override
    public TransportInstance queryByInstanceId(Integer instanceId) {

        return transportInstanceMapper.selectByInstanceId(instanceId);
    }

    @Override
    public List<String> queryLog(Integer instanceId, Integer pageNum, Integer pageSize) {
        TransportInstance instance = queryByInstanceId(instanceId);
        String jobKey = String.format("job-%s#%s", instance.getTid(), instanceId);
        String readerKey = String.format("reader-%s#%s-*", instance.getTid(), instanceId);
        String writerKey = String.format("writer-%s#%s-*", instance.getTid(), instanceId);


        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(pageNum * pageSize).size(pageSize)
                .sort("@timestamp", SortOrder.ASC)
                .query(QueryBuilders
                        .boolQuery()
                        .must(QueryBuilders
                                .boolQuery()
                                .minimumShouldMatch(1)
                                .should(new MatchPhraseQueryBuilder("thread", jobKey))
                                .should(new MatchPhraseQueryBuilder("thread", readerKey))
                                .should(new MatchPhraseQueryBuilder("thread", writerKey)))
                        .must(QueryBuilders
                                .boolQuery().minimumShouldMatch(1)
                                .should(new MatchPhraseQueryBuilder("level", "INFO"))
                                .should(new MatchPhraseQueryBuilder("level", "WARN"))
                                .should(new MatchPhraseQueryBuilder("level", "ERROR"))));

        SearchRequest request = new SearchRequest("fluentd-20200909");
        request.source(searchSourceBuilder);


        List<String> msg = new ArrayList<>();
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            if (response.getHits().getHits() != null) {
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (SearchHit hit : response.getHits().getHits()) {
                    String timestamp = sf.format(new Date((Long) hit.getSortValues()[0]));
                    msg.add(String.format("%s %s", timestamp, hit.getSourceAsMap().get("msg")));
                }
            }
        } catch (IOException e) {
            log.error("检索实例日志IO异常", e);
        }

        return msg;
    }
}
