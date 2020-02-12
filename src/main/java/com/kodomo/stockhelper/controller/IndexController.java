package com.kodomo.stockhelper.controller;

import com.kodomo.stockhelper.dao.RecommendedStockDao;
import com.kodomo.stockhelper.entity.RecommendedStock;
import com.kodomo.stockhelper.entity.StockInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-12 12:21
 **/
@Controller
public class IndexController {

    private final RecommendedStockDao recommendedStockDao;

    public IndexController(RecommendedStockDao recommendedStockDao) {
        this.recommendedStockDao = recommendedStockDao;
    }


    @GetMapping(value = "/")
    public String index() {
        return "index";
    }

    @ResponseBody
    @GetMapping(value = "/data/{date}")
    public List<RecommendedStock> getData(@PathVariable(value = "date") String dateString) {
        if (dateString == null) {
            return null;
        }

        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException e) {
            return null;
        }

        RecommendedStock example = new RecommendedStock();
        example.setDate(date);
        List<RecommendedStock> result = recommendedStockDao.findAll(Example.of(example), Sort.by(Sort.Order.desc("turnOverRate")));
        return result;
    }
}
