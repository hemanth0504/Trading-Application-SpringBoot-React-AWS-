package com.Hemanth.trading.service;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.Hemanth.trading.model.CoinDTO;
import com.Hemanth.trading.response.ApiResponse;
import com.Hemanth.trading.response.FunctionResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ChatBotServiceImpl implements ChatBotService{

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    private static final String OPENAI_MODEL = "gpt-4-0613";


    private double convertToDouble(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        } else {
            throw new IllegalArgumentException("Unsupported data type: " + value.getClass().getName());
        }
    }

    public CoinDTO makeApiRequest(String currencyName) {
        System.out.println("coin name "+currencyName);
        String url = "https://api.coingecko.com/api/v3/coins/"+currencyName.toLowerCase();

        RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();


            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody != null) {
                Map<String, Object> image = (Map<String, Object>) responseBody.get("image");

                Map<String, Object> marketData = (Map<String, Object>) responseBody.get("market_data");

                CoinDTO coinInfo = new CoinDTO();
                coinInfo.setId((String) responseBody.get("id"));
                coinInfo.setSymbol((String) responseBody.get("symbol"));
                coinInfo.setName((String) responseBody.get("name"));
                coinInfo.setImage((String) image.get("large"));

                coinInfo.setCurrentPrice(convertToDouble(((Map<String, Object>) marketData.get("current_price")).get("usd")));
                coinInfo.setMarketCap(convertToDouble(((Map<String, Object>) marketData.get("market_cap")).get("usd")));
                coinInfo.setMarketCapRank((int) responseBody.get("market_cap_rank"));
                coinInfo.setTotalVolume(convertToDouble(((Map<String, Object>) marketData.get("total_volume")).get("usd")));
                coinInfo.setHigh24h(convertToDouble(((Map<String, Object>) marketData.get("high_24h")).get("usd")));
                coinInfo.setLow24h(convertToDouble(((Map<String, Object>) marketData.get("low_24h")).get("usd")));
                coinInfo.setPriceChange24h(convertToDouble(marketData.get("price_change_24h")) );
                coinInfo.setPriceChangePercentage24h(convertToDouble(marketData.get("price_change_percentage_24h")));
                coinInfo.setMarketCapChange24h(convertToDouble(marketData.get("market_cap_change_24h")));
                coinInfo.setMarketCapChangePercentage24h(convertToDouble( marketData.get("market_cap_change_percentage_24h")));
                coinInfo.setCirculatingSupply(convertToDouble(marketData.get("circulating_supply")));
                coinInfo.setTotalSupply(convertToDouble(marketData.get("total_supply")));

                return coinInfo;

             }
       return null;
    }


    public FunctionResponse getFunctionResponse(String prompt) {
        String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);

        JSONObject function = new JSONObject();
        function.put("name", "getCoinDetails");
        function.put("description", "Get the coin details from given currency object");
        JSONObject parameters = new JSONObject();
        JSONObject properties = new JSONObject();
        properties.put("currencyName", new JSONObject()
                .put("type", "string")
                .put("description", "The currency name, id, or symbol"));
        parameters.put("type", "object");
        parameters.put("properties", properties);
        parameters.put("required", new JSONArray().put("currencyName"));
        function.put("parameters", parameters);

        JSONArray functionsArray = new JSONArray().put(function);

        JSONArray messages = new JSONArray().put(
                new JSONObject().put("role", "user").put("content", prompt)
        );

        JSONObject requestBody = new JSONObject()
                .put("model", OPENAI_MODEL)
                .put("messages", messages)
                .put("functions", functionsArray)
                .put("function_call", "auto");

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, entity, String.class);

        String responseBody = response.getBody();
        System.out.println("OpenAI Raw Response:\n" + responseBody);
        ReadContext ctx = JsonPath.parse(responseBody);

        String functionName = ctx.read("$.choices[0].message.function_call.name");
        String argumentsStr = ctx.read("$.choices[0].message.function_call.arguments");

        // 🔧 FIX: parse arguments properly
        JSONObject argsJson = new JSONObject(argumentsStr);
        String currencyName = argsJson.getString("currencyName");

        FunctionResponse res = new FunctionResponse();
        res.setFunctionName(functionName);
        res.setCurrencyName(currencyName);

        return res;
    }




    @Override
    public ApiResponse getCoinDetails(String prompt) {
        try {
            FunctionResponse res = getFunctionResponse(prompt);

            if (res.getCurrencyName() == null || res.getCurrencyName().isEmpty()) {
                ApiResponse errorRes = new ApiResponse();
                errorRes.setMessage("Could not determine a valid coin name from the prompt.");
                return errorRes;
            }

            CoinDTO coinDto = makeApiRequest(res.getCurrencyName());
            if (coinDto == null) {
                ApiResponse errorRes = new ApiResponse();
                errorRes.setMessage("Coin not found: " + res.getCurrencyName());
                return errorRes;
            }

            String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(OPENAI_API_KEY);

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "user").put("content", prompt));
            messages.put(new JSONObject().put("role", "function")
                    .put("name", res.getFunctionName())
                    .put("content", coinDto.toString()));

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-4-0613");
            requestBody.put("messages", messages);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, entity, String.class);

            String responseBody = response.getBody();
            ReadContext ctx = JsonPath.parse(responseBody);
            String finalMessage = ctx.read("$.choices[0].message.content");

            ApiResponse ans = new ApiResponse();
            ans.setMessage(finalMessage);

            return ans;

        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse errorRes = new ApiResponse();
            errorRes.setMessage("Error processing request: " + e.getMessage());
            return errorRes;
        }
    }



    @Override
    public CoinDTO getCoinByName(String coinName) {
        return this.makeApiRequest(coinName);
//        return null;
    }

    @Override
    public String simpleChat(String prompt) {
        String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY); // Use your OpenAI API key securely

        // Create request JSON
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", OPENAI_MODEL);

        JSONArray messages = new JSONArray();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.put(userMessage);

        requestBody.put("messages", messages);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, requestEntity, String.class);

        // Extract the message content
        String responseBody = response.getBody();
        ReadContext ctx = JsonPath.parse(responseBody);
        String reply = ctx.read("$.choices[0].message.content");

        System.out.println("Response: " + reply);
        return reply;
    }



}
