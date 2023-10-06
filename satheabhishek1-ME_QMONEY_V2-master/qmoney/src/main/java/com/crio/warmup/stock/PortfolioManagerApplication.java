
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.management.RuntimeErrorException;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.cglib.core.Local;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {










  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException,RuntimeException {
    //read file 
    //args[1] is taking end dates 
    
    // RestTemplate restTemplate = new RestTemplate();
    // List<PortfolioTrade> mainsymbols =readTradesFromJson(args[0]);
    // List<String> extract = new ArrayList<>();
    // LocalDate endDate = LocalDate.parse(args[1]);
    // List<TotalReturnsDto> totalReturnsList = new ArrayList<>();
    // for(PortfolioTrade trade : mainsymbols){
    //   String symbol = trade.getSymbol();
    //   LocalDate startDate = trade.getPurchaseDate();
     
  
    //   String url= prepareUrl( trade,endDate,"c50b9b8de393aab72180d0f2d8a2354d4f6a5458"); 
    //   /*TiingoCandle[] candles = restTemplate.getForObject(
    //       "https://api.tiingo.com/tiingo/daily/{symbol}/prices?startDate={startDate}&endDate={endDate}&token=c50b9b8de393aab72180d0f2d8a2354d4f6a5458",
    //       TiingoCandle[].class, symbol, startDate, endDate);*/
    //       TiingoCandle[] candles = restTemplate.getForObject(url,TiingoCandle[].class);

    //       if (candles == null || candles.length == 0) {
    //         continue;
    //     }
    //     Double closingPrice = candles[candles.length-1].getClose();
    //     totalReturnsList.add(new TotalReturnsDto(symbol, closingPrice));
    //     // leliya closing price aur symbol 

     
    //  // TiingoCandle foo = restTemplate.getForObject("https://api.tiingo.com/documentation/end-of-day" + "/1", TiingoCandle.class);
    //   //each symbol tingo api from startdate ?=from transaction ;endate?= from input parameter ;
    //   //closing price on end date is we need to extract 
    //   //restemplate library to invoke tingo api 
      
    // }
    // // ab sort karna hai 
    // //stream api 
    // Collections.sort(totalReturnsList, (o1, o2) -> Double.compare(o1.getClosingPrice(), o2.getClosingPrice()));
    // //sort kar liya 
    // List<String> ans = new ArrayList<>();
    // for(TotalReturnsDto qa:totalReturnsList ){
    //   ans.add(qa.getSymbol());
    // }
    // return ans;



    RestTemplate restTemplate = new RestTemplate();
        List<TotalReturnsDto> totalReturnsDto = new ArrayList<TotalReturnsDto>();
        List<PortfolioTrade> portfolioTrade = readTradesFromJson(args[0]);
        // LocalDate endDate = LocalDate.parse(args[1]);
        //     String url = prepareUrl(t, endDate, "289464e8faf5cf34aba42001442fb59b3c854b6c");
        //     System.out.println(url);
        for(PortfolioTrade t : portfolioTrade){
            LocalDate endDate = LocalDate.parse(args[1]);
            String url = prepareUrl(t, endDate, "c50b9b8de393aab72180d0f2d8a2354d4f6a5458");
           // System.out.println(url);
            TiingoCandle[] results = restTemplate.getForObject(url, TiingoCandle[].class);
            if(results != null){
                totalReturnsDto.add(new TotalReturnsDto(t.getSymbol(), results[results.length - 1].getClose()));
            }
        }

        Collections.sort(totalReturnsDto, new Comparator<TotalReturnsDto>(){

            @Override
            public int compare(TotalReturnsDto o1, TotalReturnsDto o2){
                return  (o1.getClosingPrice().compareTo(o2.getClosingPrice()));
            }

        });

        List<String> listAnswer = new ArrayList<>();
        for(int i = 0; i < totalReturnsDto.size(); i++){
            listAnswer.add(totalReturnsDto.get(i).getSymbol());
        }
        return listAnswer;



     //return Collections.emptyList();
  }
  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
   List<String> imp = new ArrayList<>();
   // File filepath = resolveFileFromResources("trades.json");
   File filepath = resolveFileFromResources(args[0]);
   ObjectMapper objectMapper = new ObjectMapper();
   objectMapper.registerModule(new JavaTimeModule());
   PortfolioTrade[] trades = objectMapper.readValue(filepath, PortfolioTrade[].class);
   for (PortfolioTrade trade : trades) {
      
     imp.add(trade.getSymbol());
   }
   System.out.println(imp);
  return imp;
  
}
 


  // TODO:
  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    // resolve
    File file = resolveFileFromResources(filename);
    //read the contents in a list 
    //From the resolved file path, read the contents of the file into a collection..
    List <PortfolioTrade> contents = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();
   objectMapper.registerModule(new JavaTimeModule());
   PortfolioTrade[] trades = objectMapper.readValue(file, PortfolioTrade[].class);
   for (PortfolioTrade trade : trades) {
      
    contents.add(trade);
  }
  //extract the symbol 
//   for (PortfolioTrade trade : contents) {
//     String symbol = trade.getSymbol();
//     // 
//     System.out.println(symbol);
// }

return contents;
 
 






     //return Collections.emptyList
    }


  // TODO:
  //  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token){
    List<Object> answer = new ArrayList<>();
    String symbol = trade.getSymbol();
  LocalDate startDate = trade.getPurchaseDate();
  
 
 

   String url = String.format("https://api.tiingo.com/tiingo/daily/%s/prices?startDate=%s&endDate=%s&token=%s", symbol, startDate, endDate, token);
  //  answer.add( url);
  //  System.out.print(url);
//   String uri = "https://api.tiingo.com/tiingo/daily/AAPL/prices?startDate=2010-01-01&endDate=2010-01-10&token=abcd";
  //  return  answer;

  return url;

  }
  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>



  private static void printJsonObject(Object object) throws IOException {
   Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
   ObjectMapper mapper = new ObjectMapper();
   logger.info(mapper.writeValueAsString(object));
 }

 private static File resolveFileFromResources(String filename) throws URISyntaxException {
   return Paths.get(
       Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
 }

 public static ObjectMapper getObjectMapper() {
   ObjectMapper objectMapper = new ObjectMapper();
   objectMapper.registerModule(new JavaTimeModule());
   return  objectMapper;
 }


 // TODO: CRIO_TASK_MODULE_JSON_PARSING == DONE
 //  Follow the instructions provided in the task documentation and fill up the correct values for
 //  the variables provided. First value is provided for your reference.
 //  A. Put a breakpoint on the first line inside mainReadFile() which says
 //    return Collections.emptyList();
 //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
 //  following the instructions to run the test.
 //  Once you are able to run the test, perform following tasks and record the output as a
 //  String in the function below.
 //  Use this link to see how to evaluate expressions -
 //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
 //  1. evaluate the value of "args[0]" and set the value
 //     to the variable named valueOfArgument0 (This is implemented for your reference.)
 //  2. In the same window, evaluate the value of expression below and set it
 //  to resultOfResolveFilePathArgs0
 //     expression ==> resolveFileFromResources(args[0])
 //  3. In the same window, evaluate the value of expression below and set it
 //  to toStringOfObjectMapper.
 //  You might see some garbage numbers in the output. Dont worry, its expected.
 //    expression ==> getObjectMapper().toString()
 //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
 //  second place from top to variable functionNameFromTestFileInStackTrace
 //  5. In the same window, you will see the line number of the function in the stack trace window.
 //  assign the same to lineNumberFromTestFileInStackTrace
 //  Once you are done with above, just run the corresponding test and
 //  make sure its working as expected. use below command to do the same.
 //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

 public static List<String> debugOutputs() {

    String valueOfArgument0 ="trades.json";
    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/satheabhishek1-ME_QMONEY_V2/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@212b5695";
    String functionNameFromTestFileInStackTrace = "PortfolioManagerApplicationTest.mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "30";


   return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
       toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
       lineNumberFromTestFileInStackTrace});
 }


 // Note:
 // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.




  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {


     return candles.get(0).getOpen() ;
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size()-1).getClose();
    
  }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    RestTemplate restTemplate = new RestTemplate();
    
    
   
        String url = prepareUrl(trade, endDate, token);
       // System.out.println(url);
        TiingoCandle[] results = restTemplate.getForObject(url, TiingoCandle[].class);
        List<Candle> fetch = new ArrayList<>(Arrays.asList(results));
        
    
    
     return fetch;
  }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
        List<AnnualizedReturn> ans = new ArrayList<AnnualizedReturn>();
        //get trades
      
        List<PortfolioTrade> portfolioTrade = readTradesFromJson(args[0]);
        LocalDate endDate = LocalDate.parse(args[1]);
        //connect tingo and get buy sell then call calculateAnnualizedReturns and keep making AnnualizedReturnlist 
        for( PortfolioTrade t : portfolioTrade){
         List<Candle> fetch = fetchCandles(t,endDate,"c50b9b8de393aab72180d0f2d8a2354d4f6a5458");
         
         ans.add(calculateAnnualizedReturns(endDate,t,getOpeningPriceOnStartDate(fetch),getClosingPriceOnEndDate(fetch)));


        }
      
      
        
        // call calcuannualretur in for loop
        // then put them in a list 
        //now sort them in descending order
        Collections.sort(ans, new Comparator<AnnualizedReturn>(){

          @Override
          public int compare(AnnualizedReturn o1, AnnualizedReturn o2){
              return  (o2.getAnnualizedReturn().compareTo(o1.getAnnualizedReturn()));
          }

      });





     return ans;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
      double   totalReturn = (sellPrice - buyPrice) / (buyPrice);
     // double annualized_returns = (1 + totalReturn) ^ ((1 / totalReturn) - 1);
     // double annualizedreturns = Math.pow(1 + totalReturn,( 1.0 /totalReturn )) - 1;
     LocalDate start = trade.getPurchaseDate();
     //LocalDate endDate = LocalDate.parse("2021-12-19");

   // LocalDate currentDate = LocalDate.parse("2020-12-18");


    double total_num_years = start.until(endDate, ChronoUnit.DAYS)/365.24;
    // double  total_num_years=ChronoUnit.YEARS.between(start, endDate); 
     System.out.println(total_num_years);
    // double  annualized_returns = (1 + totalReturn) ^ (1 / total_num_years) - 1;
     //double  annualized_returns = Math.pow ((1 + totalReturn),((1 / total_num_years) - 1));
     double annualized_returns = Math.pow((1 + totalReturn), (1 / total_num_years)) -1;
     //System.out.println(annualized_returns);



     







      return new AnnualizedReturn(trade.getSymbol(),annualized_returns , totalReturn);
  }
 















  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
       String file = args[0];
       LocalDate endDate = LocalDate.parse(args[1]);
       
       List<PortfolioTrade> refac = readTradesFromJson(file);

//        then call the porfolio factory and get portfolioManagerImpl object
// and then call the method calculateAnnualizedReturn
// and return annualreturn list
     RestTemplate restTemplate= new RestTemplate();
     PortfolioManager re = PortfolioManagerFactory.getPortfolioManager(restTemplate);

      
      
       //ObjectMapper objectMapper = getObjectMapper();
       
       return re.calculateAnnualizedReturn(refac, endDate);
  }


  
  
  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());


    //printJsonObject(mainReadQuotes(args));previous commit change 
    
   

  


//IMADE CHANGE HERE 
    printJsonObject(mainCalculateSingleReturn(args));

  }
//


    //printJsonObject(mainCalculateReturnsAfterRefactor(args));
  
  public static String getToken() {
         return "c50b9b8de393aab72180d0f2d8a2354d4f6a5458";
     }
}

