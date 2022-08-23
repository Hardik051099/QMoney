
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.io.IOException;
import java.net.URISyntaxException;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {


private RestTemplate restTemplate;

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF



  //For sorting AnnualizedReturns in descending order
  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.

//Fetches candles for given symbol startdate and endate
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      {
        String url = buildUri(symbol, from, to);
        Candle[] candles = restTemplate.getForObject(url,TiingoCandle[].class);
        List<Candle> candlesList = Arrays.asList(candles);   
     return candlesList;
  }
  //builds url from params
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
       String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
            + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
            
      return uriTemplate.replace("$APIKEY", getToken())
      .replace("$STARTDATE",startDate.toString())
      .replace("$ENDDATE",endDate.toString()).replace("$SYMBOL", symbol);      
  }

  //returns tiingo token
  public static String getToken() {
    String token = "f4fb86085f26a9ce03740f043d2cc51a2c380304";//hardik10 "4170cea0d1c7f54ba505cc34b8aec02e24536c90";//hardik05 "f4fb86085f26a9ce03740f043d2cc51a2c380304";
    return token;
  }

//Calculation part for annualized return
public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
PortfolioTrade trade, Double buyPrice, Double sellPrice) {
  
  LocalDate purchaseDate = trade.getPurchaseDate();
  double years = purchaseDate.until(endDate, ChronoUnit.DAYS)/365.24;
  double totalReturns = (sellPrice - buyPrice) / buyPrice;
  double annualizedReturns = Math.pow((1+totalReturns), (1/years)) - 1; 

return new AnnualizedReturn(trade.getSymbol(), annualizedReturns, totalReturns);
}

//buyprice
static Double getOpeningPriceOnStartDate(List<Candle> candles) {

  return candles.get(0).getOpen();
}

//sellprice
public static Double getClosingPriceOnEndDate(List<Candle> candles) {

  return candles.get(candles.size()-1).getClose();
}

  //Calculate annulized method
  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) {

        return portfolioTrades.stream()
            .map(trade -> {
              List<Candle> candlesList = getStockQuote(trade.getSymbol(), trade.getPurchaseDate(), endDate)
              .stream()
              .filter(candle -> candle.getDate().equals(trade.getPurchaseDate()) || candle.getDate().equals(endDate))
              .collect(Collectors.toList());         
                return calculateAnnualizedReturns(endDate, trade, getOpeningPriceOnStartDate(candlesList), getClosingPriceOnEndDate(candlesList));
            })
            .sorted(Comparator.comparingDouble(AnnualizedReturn::getAnnualizedReturn).reversed())
            .collect(Collectors.toList());
  }

}
