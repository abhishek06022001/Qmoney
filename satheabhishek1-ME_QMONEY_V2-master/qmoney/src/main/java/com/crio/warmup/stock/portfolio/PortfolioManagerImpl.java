
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  private RestTemplate restTemplate;
  private StockQuotesService stockQuotesService;
 
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  public PortfolioManagerImpl(StockQuotesService stockQuotesService) {
 
   
    this.stockQuotesService= stockQuotesService;
  }
  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }
  

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
  throws JsonProcessingException,StockQuoteServiceException,RuntimeException{
      
        return stockQuotesService.getStockQuote( symbol,  from,  to);
  }
  public static String getToken() {
    return "c50b9b8de393aab72180d0f2d8a2354d4f6a5458";
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
     

      String url = String.format("https://api.tiingo.com/tiingo/daily/%s/prices?startDate=%s&endDate=%s&token=%s", symbol, startDate, endDate, getToken());
     // String url = "https://api.tiingo.com/tiingo/daily/%s/prices?startDate="+startDate+"&endDate="+endDate+"&token="+getToken();
     // String url = "https://api.tiingo.com/tiingo/daily/%s/prices?startDate=+startDate+&endDate=+&token=%s", symbol, startDate, endDate, getToken();

      return url;
  
  }


  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) throws StockQuoteServiceException{
    // TODO Auto-generated method stub
    
      List<AnnualizedReturn> annualised = new ArrayList<AnnualizedReturn>();
    try{
      
      for(PortfolioTrade t1 : portfolioTrades){

        double total_num_years = t1.getPurchaseDate().until(endDate, ChronoUnit.DAYS)/365.24;
        List<Candle> ans = getStockQuote(t1.getSymbol(),t1.getPurchaseDate(),endDate);

      
        double totalReturn =   ( ans.get(ans.size()-1).getClose()-ans.get(0).getOpen()) /(ans.get(0).getOpen() );
        //System.out.println(totalReturn);
    
        double annualized_returns = Math.pow((1 + totalReturn), (1 / total_num_years)) -1;
        //System.out.println(annualized_returns);
        annualised.add(new AnnualizedReturn(t1.getSymbol(), annualized_returns, totalReturn));
      }
    }catch(JsonProcessingException e){
      e.printStackTrace();;
    }
    

    
     annualised.sort(getComparator());
     return annualised;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturnParallel(
      List<PortfolioTrade> portfolioTrades, LocalDate endDate, int numThreads)
      throws InterruptedException, StockQuoteServiceException {
        List<AnnualizedReturn> annualised = new ArrayList<AnnualizedReturn>();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Future<AnnualizedReturn>> ans = new ArrayList<>();
        for(PortfolioTrade t1 : portfolioTrades){
         
          Callable<AnnualizedReturn> task = () -> {
          
            return  getAnnualizedReturns(t1, endDate);
        };

        Future<AnnualizedReturn> future = executorService.submit(task);
        ans.add(future);
        }
       
        for(int i = 0 ; i < ans.size(); i++){
          
            AnnualizedReturn a;
            
              try {
                a = ans.get(i).get();
                annualised.add(a);
              } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                throw new StockQuoteServiceException("error while calling api");
              }
             
            
           
         
          
        }
        annualised.sort(getComparator());
       
        return annualised;
       
        
    

    
  }
  private AnnualizedReturn getAnnualizedReturns(PortfolioTrade trade , LocalDate endDate) throws StockQuoteServiceException{
    PortfolioTrade t1 = trade;
    double totalReturn= 0.0;
    double annualized_returns= 0.0;

    try{
      double total_num_years = t1.getPurchaseDate().until(endDate, ChronoUnit.DAYS)/365.24;
      List<Candle> ans = getStockQuote(t1.getSymbol(),t1.getPurchaseDate(),endDate);

    
       totalReturn =   ( ans.get(ans.size()-1).getClose()-ans.get(0).getOpen()) /(ans.get(0).getOpen() );
    
  
       annualized_returns = Math.pow((1 + totalReturn), (1 / total_num_years)) -1;


    }catch(JsonProcessingException e){
      e.printStackTrace();
    }


    return new AnnualizedReturn(trade.getSymbol(), annualized_returns, totalReturn);

  }
}


     







      
   
 


  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.


