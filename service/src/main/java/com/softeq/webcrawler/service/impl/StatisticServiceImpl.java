package com.softeq.webcrawler.service.impl;

import com.softeq.webcrawler.entity.Statistic;
import com.softeq.webcrawler.exception.ResourceNotFoundException;
import com.softeq.webcrawler.repository.StatisticRepository;
import com.softeq.webcrawler.service.CrawlService;
import com.softeq.webcrawler.service.StatisticService;
import com.softeq.webcrawler.view.CrawlView;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

  private static final String URL_HEADER = "URL";
  private static final String TOTALHITS_HEADER = "TOTAL HITS";
  private static final String COLUMN_DELIMITER = ",";
  private static final String ROW_DELIMITER = "\n";
  private static final Charset CHARSET = StandardCharsets.UTF_8;
  private static final String RESOURCE_NOTFOUND = "Cannot find resource with the id: %d";
  private final StatisticRepository statisticRepository;
  private final CrawlService crawlService;

  public Statistic saveStatistic(Statistic statistic) {
    return statisticRepository.save(statistic);
  }

  @Override
  public InputStream getCSVStatistics(Long statisticId) {
    isStatisticExists(statisticId);

    List<CrawlView> crawls = crawlService.getCrawlsByStatisticId(statisticId);

    return createCSVFile(statisticId, crawls);
  }

  @Override
  public InputStream getCSVTopHitsStatistics(Long statisticId, Integer recordCount) {
    isStatisticExists(statisticId);

    List<CrawlView> crawls = crawlService.getTopCrawlsByStatisticIdSortByTotalHitsDesc(statisticId, recordCount);

    return createCSVFile(statisticId, crawls);
  }

  private InputStream createCSVFile(Long statisticId, List<CrawlView> crawls) {

    String header = createHeader(crawls.get(0));
    String records = createRecords(crawls);

    String csvFile = header + records;

    return new ByteArrayInputStream(csvFile.getBytes(CHARSET));
  }

  private String createHeader(CrawlView crawlView) {
    StringJoiner joiner = new StringJoiner(COLUMN_DELIMITER);

    joiner.add(URL_HEADER);

    List<String> keywords = Arrays.asList(crawlView.getKeywords().split(COLUMN_DELIMITER));
    keywords.forEach(joiner::add);

    joiner.add(TOTALHITS_HEADER);

    joiner.add(ROW_DELIMITER);

    return joiner.toString();
  }

  private String createRecords(List<CrawlView> crawls) {
    StringJoiner joiner = new StringJoiner(ROW_DELIMITER);

    crawls.forEach(o -> {
      String record = createRecord(o);
      joiner.add(record);
    });

    return joiner.toString();
  }

  private String createRecord(CrawlView view) {
    StringJoiner joiner = new StringJoiner(COLUMN_DELIMITER);

    joiner.add(view.getUrls());

    List<String> hits = Arrays.asList(view.getHitsPerKeyword().split(COLUMN_DELIMITER));
    hits.forEach(joiner::add);

    joiner.add(view.getTotalHits().toString());

    return joiner.toString();
  }

  private void isStatisticExists(Long statisticId) {
    Optional<Statistic> statistic = statisticRepository.findById(statisticId);
    if (statistic.isEmpty()) {
      throw new ResourceNotFoundException(String.format(RESOURCE_NOTFOUND, statisticId));
    }
  }

}
