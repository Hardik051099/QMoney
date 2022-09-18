
package com.crio.warmup.stock.quotes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  private RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public static String getToken() {
    String token = "0e13a0db0ed1675aada089c4dd39990e527e5ec6";//hardik10 "4170cea0d1c7f54ba505cc34b8aec02e24536c90";//hardik05 "f4fb86085f26a9ce03740f043d2cc51a2c380304";
    return token;
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
         + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
         
   return uriTemplate.replace("$APIKEY", getToken())
   .replace("$STARTDATE",startDate.toString())
   .replace("$ENDDATE",endDate.toString()).replace("$SYMBOL", symbol);      
}

private static ObjectMapper getObjectMapper() {
  ObjectMapper objectMapper = new ObjectMapper();
  objectMapper.registerModule(new JavaTimeModule());
  return objectMapper;
}

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException,StockQuoteServiceException {
        List<Candle> candlesList = new ArrayList<>();
        try{
          String url = buildUri(symbol, from, to);
          String data = restTemplate.getForObject(url,String.class);
          Candle[] candles = getObjectMapper().readValue(data, TiingoCandle[].class);
          candlesList = Arrays.asList(candles);  
        } catch (NullPointerException e) {
          throw new StockQuoteServiceException("Api limit reached ",e);
        }
 
     return candlesList;
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.





  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  1. Update the method signature to match the signature change in the interface.
  //     Start throwing new StockQuoteServiceException when you get some invalid response from
  //     Tiingo, or if Tiingo returns empty results for whatever reason, or you encounter
  //     a runtime exception during Json parsing.
  //  2. Make sure that the exception propagates all the way from
  //     PortfolioManager#calculateAnnualisedReturns so that the external user's of our API
  //     are able to explicitly handle this exception upfront.

  //CHECKSTYLE:OFF


}
