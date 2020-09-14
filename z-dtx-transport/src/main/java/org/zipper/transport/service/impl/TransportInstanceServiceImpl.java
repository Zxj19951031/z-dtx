package org.zipper.transport.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
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
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(pageNum * pageSize).size(pageSize)
                .sort("@timestamp", SortOrder.ASC)
                .query(QueryBuilders
                        .boolQuery()
                        .must(new MatchPhraseQueryBuilder("thread", String.format("*%s#%s*", instance.getTid(), instanceId)))
                        .must(QueryBuilders
                                .boolQuery().minimumShouldMatch(1)
                                .should(new MatchPhraseQueryBuilder("level", "INFO"))
                                .should(new MatchPhraseQueryBuilder("level", "WARN"))
                                .should(new MatchPhraseQueryBuilder("level", "ERROR"))));

        SearchRequest request = new SearchRequest("fluentd-*");
        request.source(searchSourceBuilder);


        List<String> msg = new ArrayList<>();
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            if (response.getHits().getHits() != null) {
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (SearchHit hit : response.getHits().getHits()) {
                    String timestamp = sf.format(new Date((Long) hit.getSortValues()[0]));
                    String msgStr = String.format("%s %s",
                            timestamp,
                            hit.getSourceAsMap().get("msg"));
                    if ("ERROR".equals(hit.getSourceAsMap().get("level"))) {
                        msgStr = String.format("%s\n%s", msgStr, hit.getSourceAsMap().get("throwable"));
                    }
                    msg.add(msgStr);
                }
            }
        } catch (IOException e) {
            log.error("检索实例日志IO异常", e);
        }

        return msg;
    }
}
