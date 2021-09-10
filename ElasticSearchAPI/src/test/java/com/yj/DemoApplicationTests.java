package com.yj;

import com.alibaba.fastjson.JSON;
import com.yj.pojo.User;
import net.minidev.json.JSONArray;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;
    //测试索引的创建
    @Test
    void createIndex() throws IOException {
        //创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("yj_index");
        //执行请求 indicesClient,
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    //测试获取索引
    @Test
    void getIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("yj_index2");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }
    //删除索引
    @Test
    void deleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("yj_index");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());

    }
    //添加文档
    @Test
    void addDoc() throws IOException {
        User user = new User("施英杰", 5);
        IndexRequest request = new IndexRequest("yj_index");
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(2));
        //将数据放入请求
        request.source(JSON.toJSONString(user), XContentType.JSON);
        //客户端发送请求，获取相应结果
        IndexResponse index = client.index(request, RequestOptions.DEFAULT);
        System.out.println(index);

    }

    //获取文档
    @Test
    void testExist() throws IOException {
        GetRequest request = new GetRequest("yj_index", "1");
        //不返回获取_source的上下文了
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("age");
        boolean exists = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //获取文档信息
    @Test
    void getDoc() throws IOException {
        GetRequest request = new GetRequest("yj_index", "1");
        GetResponse getResponse = client.get(request, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString());//打印文档内容
        System.out.println(getResponse);//返回的全部内容和命令式一样的
    }
    //更新文档信息
    @Test
    void updateDoc() throws IOException {
        UpdateRequest request = new UpdateRequest("yj_index", "1");
        User user = new User("施英杰杰杰",8);
        request.doc(JSON.toJSONString(user),XContentType.JSON);
        UpdateResponse update = client.update(request, RequestOptions.DEFAULT);

        System.out.println(update.status());
    }
    //删除文档记录
    @Test
    void deleteDoc() throws IOException {
        DeleteRequest request = new DeleteRequest("yj_index","1");
        request.timeout("3s");
        DeleteResponse delete = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.status());

    }
    //批量插入
    @Test
    void bulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        List<User> users = new ArrayList<>();
        users.add(new User("haha",3));
        users.add(new User("heihei",4));
        users.add(new User("xixi",5));
        users.add(new User("xixi5",5));
        users.add(new User("xixi6",6));
        users.add(new User("xixi7",7));

        //批处理请求
        for (int i = 0; i < users.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("yj_index")
                    .id(""+(i+1))
                    .source(JSON.toJSONString(users.get(i)),XContentType.JSON)
            );
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.status());
        System.out.println(bulk.hasFailures());
    }
    //搜索
    @Test
    void search() throws IOException {
        SearchRequest request = new SearchRequest("jd_goods");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        TermQueryBuilder query = QueryBuilders.termQuery("title", "java");
        sourceBuilder.query(query);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        SearchRequest source = request.source(sourceBuilder);
        SearchResponse search = client.search(source, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println(JSON.toJSONString(hits));
        System.out.println("===============================");
        for (SearchHit hit:search.getHits().getHits()){
            hit.getSourceAsMap();
        }

    }
}
