package com.kodomo.stockhelper.utility;

import com.kodomo.stockhelper.dao.StockInfoDao;
import com.kodomo.stockhelper.entity.StockInfo;
import com.kodomo.stockhelper.utility.jsouphelper.JsoupHelper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-12 21:11
 **/
@Slf4j
@Component
public class FetchAllStockIdHelper {

    private final StockInfoDao stockInfoDao;
    private final JsoupHelper jsoupHelper;

    public FetchAllStockIdHelper(StockInfoDao stockInfoDao, JsoupHelper jsoupHelper) {
        this.stockInfoDao = stockInfoDao;
        this.jsoupHelper = jsoupHelper;
    }

    public void fetchAll() throws FetchAllStockIdException {
        try {
            List<String> paths = getAllLinksOnBanbanGupiao();
            log.info("共" + paths.size() + "个子页面");
            int totalCount = 0;
            for (String path : paths) {
                int count = fetchAllStockInfoOnSecondaryPage(path);
                log.info(path + "页面共添加" + count + "个数据");
                totalCount += count;
            }
            log.info("爬取结束, 共爬取到" + totalCount + "个数据");
        } catch (IOException e) {
            throw new FetchAllStockIdException();
        }
    }

    /**
     * 获取所有二级页面的path
     *
     * @return
     * @throws IOException
     */
    private List<String> getAllLinksOnBanbanGupiao() throws IOException {
        String url = "https://www.banban.cn/gupiao/";
        Document document = jsoupHelper.fetchDocument(url);

        Element div = document.getElementById("ctrlfscont");
        Elements elements = div.getElementsByTag("a");
        List<String> result = new ArrayList<>();
        for (Element e : elements) {
            String path = e.attr("href");
            if (path != null && path.endsWith("/")) {
                result.add(path);
            }
        }
        return result;
    }

    /***
     * 在二级页面爬到代码和名字， 保存
     * 中国国贸(600007)
     * @param path
     *
     * @return 数量
     */
    private int fetchAllStockInfoOnSecondaryPage(String path) {
        String baseUrl = "https://www.banban.cn";
        try {
            Document document = jsoupHelper.fetchDocument(baseUrl + path);
            Element div = document.getElementById("ctrlfscont");
            Elements elements = div.getElementsByTag("a");

            int totalCount = 0;
            List<StockInfo> stockInfoList = new ArrayList<>();
            for (Element e : elements) {
                String elementText = e.text();
                if (elementText != null && !elementText.equals("")) {
                    int leftIndex = elementText.indexOf("(");
                    int rightIndex = elementText.indexOf(")");

                    if (leftIndex != -1 && rightIndex != -1) {
                        String name = elementText.substring(0, leftIndex);
                        String id = elementText.substring(leftIndex + 1, rightIndex);
                        StockInfo stockInfo = getStockId(name, id);
                        stockInfoList.add(stockInfo);
                        totalCount++;
                    }
                }
            }
            stockInfoDao.saveAll(stockInfoList);
            return totalCount;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 返回股票代码和名字
     *
     * @param name
     * @param id
     */
    private StockInfo getStockId(String name, String id) {
        StockInfo stockInfo = new StockInfo();
        stockInfo.setStockId(id);
        stockInfo.setName(name);
        return stockInfo;
    }
}
