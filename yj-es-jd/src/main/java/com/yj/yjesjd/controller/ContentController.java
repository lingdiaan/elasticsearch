package com.yj.yjesjd.controller;

import com.yj.yjesjd.service.ContentService;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keyword) throws IOException {
        return contentService.parseContent(keyword);

    }

    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> search(@PathVariable("keyword") String keyword,
                                           @PathVariable("pageNo") int pageNo,
                                           @PathVariable("pageSize")int pageSize) throws IOException {
        List<Map<String, Object>> maps = contentService.searchPageHighlight(keyword, pageNo, pageSize);
        for (Map map:maps) System.out.println(map);
        return maps;

    }

}
