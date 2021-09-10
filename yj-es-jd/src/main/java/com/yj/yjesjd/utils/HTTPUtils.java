package com.yj.yjesjd.utils;

import com.yj.yjesjd.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HTTPUtils {
    public static void main(String[] args) throws IOException {
        new HTTPUtils().parseJD("咒术回战").forEach(System.out::println);
    }

    public List<Content> parseJD(String keyword) throws IOException {
        //获得请求 http://search.jd.com/Search?keyword=java
        String url = "http://search.jd.com/Search?keyword="+keyword;
        //解析网页,返回的对象就是js页面对象
        Document doc = Jsoup.parse(new URL(url),30000);
        //所有在js中能使用的方法这里都能用
        Element element = doc.getElementById("J_goodsList");
        Elements elements = element.getElementsByTag("li");
        //获取元素中的内容
        List<Content> contents = new ArrayList<>();
        for(Element el:elements){
            //关于图片特别多的网站，所有的图片都是延迟加载的
            //data-lazy-img
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            Content content = new Content();
            content.setImage(img);
            content.setPrice(price);
            content.setTitle(title);
            contents.add(content);
        }
        return contents;
    }

    }




