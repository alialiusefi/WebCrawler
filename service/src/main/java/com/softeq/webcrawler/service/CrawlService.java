package com.softeq.webcrawler.service;

import com.softeq.webcrawler.entity.Crawl;
import com.softeq.webcrawler.view.CrawlView;
import java.util.List;

public interface CrawlService {

  Crawl saveCrawl(Crawl crawl);

  List<CrawlView> getTopCrawlsByStatisticIdSortByTotalHitsDesc(Long statisticId, Integer limitParam);

  List<CrawlView> getCrawlsByStatisticId(Long statisticId);

}
