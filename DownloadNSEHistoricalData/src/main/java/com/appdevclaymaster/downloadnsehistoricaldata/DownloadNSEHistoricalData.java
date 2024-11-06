package com.appdevclaymaster.downloadnsehistoricaldata;

import com.angelbroking.smartapi.Routes;
import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.SmartAPIRequestHandler;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.User;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Suraj Prajapati (Claymaster)
 */
public class DownloadNSEHistoricalData {
    
    static final Logger logger = LogManager.getLogger(DownloadNSEHistoricalData.class);

    //Full Token list is available on https://margincalculator.angelbroking.com/OpenAPI_File/files/OpenAPIScripMaster.json
    //Below is the token list for FnO Stocks listed as on current date
    static final String[] tokensList = {"7", "13", "17903", "21614", "30108", "22", "25", "15083", "11703", "1270", "157", "163", "212", "236", "14418", "263", "21238", "275", "5900", "16669", "16675", "317", "335", "341", "2263", "4668", "371", "383", "404", "422", "10604", "438", "11373", "2181", "526", "547", "6994", "10794", "583", "637", "685", "694", "20374", "11543", "15141", "4749", "739", "17094", "5701", "1901", "772", "8075", "19943", "10940", "21690", "14732", "881", "910", "958", "676", "1023", "4717", "7406", "13528", "1174", "10099", "17875", "11872", "1232", "10599", "2303", "9819", "7229", "4244", "1333", "467", "1348", "1363", "17939", "1406", "1394", "4963", "21770", "18652", "14366", "11957", "11184", "220", "11262", "1512", "10726", "11195", "5258", "29135", "1594", "1624", "1633", "13611", "1660", "6733", "13270", "11723", "18096", "1922", "11654", "19234", "1997", "11483", "24948", "17818", "18564", "10440", "2031", "13285", "19061", "4067", "10999", "31181", "9581", "2142", "17534", "4204", "4503", "2277", "23650", "6364", "13751", "14672", "17963", "15332", "11630", "20242", "10738", "2475", "14413", "2412", "18365", "11351", "14299", "2664", "24184", "10666", "9590", "14977", "13147", "2043", "18391", "15355", "2885", "2963", "17971", "21808", "3045", "3103", "4306", "3150", "3273", "3351", "13404", "10243", "3405", "3721", "3432", "3456", "3426", "3499", "11536", "13538", "3506", "3518", "1964", "8479", "16713", "11532", "10447", "11287", "3063", "3718", "3787", "7929"};
    static final String[] tokensName = {"AARTIIND", "ABB", "ABBOTINDIA", "ABCAPITAL", "ABFRL", "ACC", "ADANIENT", "ADANIPORTS", "ALKEM", "AMBUJACEM", "APOLLOHOSP", "APOLLOTYRE", "ASHOKLEY", "ASIANPAINT", "ASTRAL", "ATUL", "AUBANK", "AUROPHARMA", "AXISBANK", "BAJAJ-AUTO", "BAJAJFINSV", "BAJFINANCE", "BALKRISIND", "BALRAMCHIN", "BANDHANBNK", "BANKBARODA", "BATAINDIA", "BEL", "BERGEPAINT", "BHARATFORG", "BHARTIARTL", "BHEL", "BIOCON", "BOSCHLTD", "BPCL", "BRITANNIA", "BSOFT", "CANBK", "CANFINHOME", "CHAMBLFERT", "CHOLAFIN", "CIPLA", "COALINDIA", "COFORGE", "COLPAL", "CONCOR", "COROMANDEL", "CROMPTON", "CUB", "CUMMINSIND", "DABUR", "DALBHARAT", "DEEPAKNTR", "DIVISLAB", "DIXON", "DLF", "DRREDDY", "EICHERMOT", "ESCORTS", "EXIDEIND", "FEDERALBNK", "GAIL", "GLENMARK", "GMRINFRA", "GNFC", "GODREJCP", "GODREJPROP", "GRANULES", "GRASIM", "GUJGASLTD", "HAL", "HAVELLS", "HCLTECH", "HDFCAMC", "HDFCBANK", "HDFCLIFE", "HEROMOTOCO", "HINDALCO", "HINDCOPPER", "HINDPETRO", "HINDUNILVR", "ICICIBANK", "ICICIGI", "ICICIPRULI", "IDEA", "IDFC", "IDFCFIRSTB", "IEX", "IGL", "INDHOTEL", "INDIAMART", "INDIGO", "INDUSINDBK", "INDUSTOWER", "INFY", "IOC", "IPCALAB", "IRCTC", "ITC", "JINDALSTEL", "JKCEMENT", "JSWSTEEL", "JUBLFOOD", "KOTAKBANK", "LALPATHLAB", "LAURUSLABS", "LICHSGFIN", "LT", "LTF", "LTIM", "LTTS", "LUPIN", "M&M", "M&MFIN", "MANAPPURAM", "MARICO", "MARUTI", "MCX", "METROPOLIS", "MFSL", "MGL", "MOTHERSON", "MPHASIS", "MRF", "MUTHOOTFIN", "NATIONALUM", "NAUKRI", "NAVINFLUOR", "NESTLEIND", "NMDC", "NTPC", "OBEROIRLTY", "OFSS", "ONGC", "PAGEIND", "PEL", "PERSISTENT", "PETRONET", "PFC", "PIDILITIND", "PIIND", "PNB", "POLYCAB", "POWERGRID", "PVRINOX", "RAMCOCEM", "RBLBANK", "RECLTD", "RELIANCE", "SAIL", "SBICARD", "SBILIFE", "SBIN", "SHREECEM", "SHRIRAMFIN", "SIEMENS", "SRF", "SUNPHARMA", "SUNTV", "SYNGENE", "TATACHEM", "TATACOMM", "TATACONSUM", "TATAMOTORS", "TATAPOWER", "TATASTEEL", "TCS", "TECHM", "TITAN", "TORNTPHARM", "TRENT", "TVSMOTOR", "UBL", "ULTRACEMCO", "UNITDSPR", "UPL", "VEDL", "VOLTAS", "WIPRO", "ZYDUSLIFE"};
    
    static SmartConnect smartConnect;
    
    public static void main(String[] args) {

        //Initialise & get the API credentials
        String API_KEY = "";
        String CLIENT_ID = "";
        String CLIENT_PIN = "";
        String TOTP = "";
        
        String accessToken = "";
        String refreshToken = "";
        String feedToken = "";
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        try {
            System.out.print("Enter API_KEY: ");
            API_KEY = reader.readLine();
            
            System.out.print("Enter CLIENT_ID: ");
            CLIENT_ID = reader.readLine();
            
            System.out.print("Enter PASSWORD: ");
            CLIENT_PIN = reader.readLine();
            
            System.out.print("Enter TOTP: ");
            TOTP = reader.readLine();
            
            logger.info("API_KEY: " + API_KEY);
            logger.info("CLIENT_ID: " + CLIENT_ID);
            logger.info("CLIENT_PIN: " + CLIENT_PIN);
            logger.info("TOTP: " + TOTP);
        } catch (IOException e) {
            logger.error("Error fetching user API Credentials. Try again !!!");
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        // Initialize Smart API using API Credentials
        logger.info("Connecting to Smart API");
        smartConnect = new SmartConnect(API_KEY);
        
        smartConnect.setSessionExpiryHook(() -> {
            logger.info("SmartApi Session Expired");
        });
        
        User user = smartConnect.generateSession(CLIENT_ID, CLIENT_PIN, TOTP);
        
        if (user != null) {
            logger.info("Connected to Smart API");
            accessToken = user.getAccessToken();
            feedToken = user.getFeedToken();
            smartConnect.setUserId(user.getUserId());
            smartConnect.setAccessToken(accessToken);
            downloadHistoricalData();
        } else {
            logger.info("Login to Smart API failed.");
        }
        
    }
    
    public static void downloadHistoricalData() {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate = LocalDate.now(); // Current date

        for (int i = 0; i < tokensList.length; i++) {
            
            try {

                // Setting start date of historical data as 2024-01-01
                LocalDate startDate = LocalDate.of(2024, 01, 01);
                
                String fileName = tokensName[i] + ".csv";
                FileWriter fileWriter = null;
                
                fileWriter = new FileWriter(fileName);
                fileWriter.append("DateTime,open,high,low,close,volume\n");
                
                while (startDate.isBefore(endDate)) {

                    // Calculate the start & end of the current month
                    LocalDate nextMonthStart = startDate.plus(1, ChronoUnit.MONTHS).withDayOfMonth(1);
                    LocalDate monthEnd = nextMonthStart.minusDays(1);

                    // Ensure we don't go past the end date
                    if (monthEnd.isAfter(endDate)) {
                        monthEnd = endDate;
                    }

                    //Check below link for parameters to get historical data 
                    //https://smartapi.angelbroking.com/docs/Historical
                    JSONObject requestObject = new JSONObject();
                    requestObject.put("exchange", "NSE");
                    requestObject.put("symboltoken", tokensList[i]);
                    requestObject.put("interval", "FIVE_MINUTE");
                    requestObject.put("fromdate", startDate.format(formatter) + " 09:15");
                    requestObject.put("todate", monthEnd.format(formatter) + " 15:30");
                    
                    logger.info("Fetching data for " + tokensName[i] + " from " + startDate.format(formatter) + " to " + monthEnd.format(formatter));
                    JSONObject response = getCandleData(requestObject);
                    Gson gson = new Gson();
                    JsonResponsePojo candleJsonResponse;
                    
                    System.out.println("response : {}" + response);
                    String responseStatus = response.get("message").toString();
                    int noOfCandles = 0;

                    // Convert json data to pojo & save it in CSV
                    if (responseStatus.equalsIgnoreCase("SUCCESS")) {
                        candleJsonResponse = gson.fromJson(response.toString(), JsonResponsePojo.class);
                        noOfCandles = candleJsonResponse.getData().size();
                        logger.info("No. of candles fetched : " + noOfCandles);
                        
                        for (int j = 0; j < noOfCandles; j++) {
                            fileWriter.append(String.join(",", candleJsonResponse.getData().get(j).toString().replaceAll("[\\[\\]]", "")));
                            fileWriter.append("\n");
                        }
                        
                        logger.info("Adding data to CSV file successfull");
                    }
                    
                    try {
                        //Only 3 requests per second is allowed. Hence, sleeping after every 1/3rd second
                        Thread.sleep(333);
                    } catch (InterruptedException ex) {
                        logger.error("Interupted sleep while fetching candle data");
                        logger.error(ex.getMessage());
                    }
                    
                    startDate = nextMonthStart;
                }
                
                fileWriter.close();
            } catch (IOException e) {
                logger.error("IO Exception for CSV File");
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    public static JSONObject getCandleData(JSONObject params) {
        JSONObject response = null;
        String apiKey = smartConnect.getApiKey();
        String accessToken = smartConnect.getAccessToken();
        
        Proxy proxy = null;
        SmartAPIRequestHandler smartAPIRequestHandler = new SmartAPIRequestHandler(proxy);
        Routes routes = new Routes();
        String url = routes.get("api.candle.data");
        
        int maxAttempts = 3;

        // Request again in case of data request failed due to some reason viz timeed out etc.
        for (int i = 0; i < maxAttempts; i++) {
            try {
                response = smartAPIRequestHandler.postRequest(apiKey, url, params, accessToken);
                if (response != null) {
                    break;
                }
                Thread.sleep(1000);
            } catch (Exception | SmartAPIException e) {
                logger.error("Failed to fetch candle data. Retrying...");
                logger.error("response is : {}", response);
                logger.error(e.getStackTrace().toString());
            }
        }
        return response;
    }
    
}
