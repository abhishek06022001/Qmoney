
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.management.RuntimeErrorException;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  private RestTemplate restTemplate;
  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException,StockQuoteServiceException {
    // TODO Auto-generated method stub
    String url =buildUri(symbol, from, to);
    System.out.println(url);
    TiingoCandle[] ans = null;
    List<Candle> stock = new ArrayList<Candle>();
    if(from.compareTo(to)>= 0){
      throw new RuntimeException();
    }
        
   
    try{
      String stocksStartToEndDate = restTemplate.getForObject(url, String.class);
      ObjectMapper  objectMapper = PortfolioManagerApplication.getObjectMapper();
       ans = objectMapper.readValue(stocksStartToEndDate,TiingoCandle[].class);
       stock = Arrays.asList(ans);
    }catch(NullPointerException e){
      throw new StockQuoteServiceException("error in requesting response from tiingo api ",e.getCause());
      
    }
    //List<Candle> fetch = new ArrayList<>(Arrays.asList(results));
       
     return stock;
    }

 
    
  

  private String buildUri(String symbol, LocalDate from, LocalDate to) {
    String url = String.format("https://api.tiingo.com/tiingo/daily/%s/prices?startDate=%s&endDate=%s&token=%s", symbol, from, to, getToken());
   
      return url;
  }

  public static String getToken() {
    return "c50b9b8de393aab72180d0f2d8a2354d4f6a5458";
}
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
