package com.yj.yjesjd.service;

import com.alibaba.fastjson.JSON;
import com.yj.yjesjd.pojo.Content;
import com.yj.yjesjd.utils.HTTPUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.ScrollableHitSource;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 1.爬取数据放到ES中
     * @param keyword
     * @return
     * @throws IOException
     */
    public Boolean parseContent(String keyword) throws IOException {
        List<Content> contents = new HTTPUtils().parseJD(keyword);
        //把查询的数据放入ES中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for (int i = 0; i <contents.size() ; i++) {
            bulkRequest.add(new IndexRequest("jd_goods")
            .source(JSON.toJSONString(contents.get(i)), XContentType.JSON)
            );
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();

    }

    /**
     * 2.获取数据实现搜索功能
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     * @throws IOException
     */
    public List<Map<String,Object>> searchPage(String keyword,int pageNo,int pageSize) throws IOException {
//        pageNo=Math.max(1,pageNo);

        SearchRequest request = new SearchRequest("jd_goods");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(0);
        sourceBuilder.size(30);
        MatchQueryBuilder query = QueryBuilders.matchQuery("title",keyword);
        sourceBuilder.query(query);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        SearchRequest source = request.source(sourceBuilder);
        SearchResponse search = restHighLevelClient.search(source, RequestOptions.DEFAULT);
        //解析结果
        search.getHits();
        List<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit documentFields:search.getHits().getHits()){
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            list.add(sourceAsMap);
        }
        return list;

    }

    /**
     * 实现搜索高亮
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     * @throws IOException
     */
    public List<Map<String,Object>> searchPageHighlight(String keyword,int pageNo,int pageSize) throws IOException {
//        pageNo=Math.max(1,pageNo);

        SearchRequest request = new SearchRequest("jd_goods");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(0);
        sourceBuilder.size(30);


        //构建高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        //关闭同一商品多个高亮
        highlightBuilder.requireFieldMatch(true);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        sourceBuilder.highlighter(highlightBuilder);

//TermQueryBuilder query = QueryBuilders.termQuery("title",keyword);
        MatchQueryBuilder query = QueryBuilders.matchQuery("title",keyword);
        sourceBuilder.query(query);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        SearchRequest source = request.source(sourceBuilder);
        SearchResponse search = restHighLevelClient.search(source, RequestOptions.DEFAULT);
        //解析结果
        search.getHits();
        List<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit documentFields:search.getHits().getHits()){
            //解析高亮字段
            Map<String, HighlightField> map = documentFields.getHighlightFields();
            HighlightField title = map.get("title");
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();//原来的结果
            //解析高亮字段，将原来的字段替换为我们高亮的字段
            if(title!=null){
                Text[] fragments = title.fragments();
                String newTitle = "";
                for(Text text:fragments){
                    newTitle+=text;
                }
                sourceAsMap.put("title",newTitle);//替换原来的title
            }
            list.add(sourceAsMap);
        }
        return list;

    }

}
