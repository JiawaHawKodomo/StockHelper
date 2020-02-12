package com.kodomo.stockhelper.utility.jsouphelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-12 21:18
 **/

@Component
public class JsoupHelper {
    public Document fetchDocument(String url) throws IOException {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.90 Safari/537.36").get();

    }
}
