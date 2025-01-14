package com.poloniex.api.client.spot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.poloniex.api.client.spot.common.CandlestickChannels;
import com.poloniex.api.client.spot.model.event.spot.OrderEvent;
import com.poloniex.api.client.spot.model.event.spot.PoloEvent;
import com.poloniex.api.client.spot.model.event.spot.TickerEvent;
import com.poloniex.api.client.spot.model.request.spot.*;
import com.poloniex.api.client.spot.model.response.spot.Account;
import com.poloniex.api.client.spot.model.response.spot.Order;
import com.poloniex.api.client.spot.model.response.spot.SmartOrder;
import com.poloniex.api.client.spot.rest.spot.SpotPoloRestClient;
import com.poloniex.api.client.spot.ws.PoloApiCallback;
import com.poloniex.api.client.spot.ws.PoloLoggingCallback;
import com.poloniex.api.client.spot.ws.spot.SpotPoloPrivateWebsocketClient;
import com.poloniex.api.client.spot.ws.spot.SpotPoloPublicWebsocketClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;

import java.util.List;

import static com.poloniex.api.client.spot.common.PoloApiConstants.CHANNEL_TICKER;

@Slf4j
public class SpotPoloClientSample {

    private static final String HOST = "https://api.poloniex.com/ ";
    private static final String PUBLIC_WS_URL = "wss://ws.poloniex.com/ws/public";
    private static final String PRIVATE_WS_URL = "wss://ws.poloniex.com/ws/private";

    private static final String API_KEY = "";
    private static final String SECRET = "";

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

    public static void main(String[] args) throws JsonProcessingException {

        testPublicClient();
        testPrivateClient();
        websocketPublicTest();
        websocketPrivateTest();

    }

    private static void testPublicClient() throws JsonProcessingException {
        SpotPoloRestClient poloniexApiClient = new SpotPoloRestClient(HOST);
        testPublicEndpoints(poloniexApiClient);
    }

    private static void testPrivateClient() throws JsonProcessingException {
        SpotPoloRestClient poloniexApiClient = new SpotPoloRestClient(HOST, API_KEY, SECRET);
        testAllEndpoints(poloniexApiClient);
    }

    private static void testPublicEndpoints(SpotPoloRestClient poloniexApiClient) throws JsonProcessingException {

        log.info("getTimestamp: {}", writer.writeValueAsString(poloniexApiClient.getTimestamp()));
        log.info("getPrice: {}", writer.writeValueAsString(poloniexApiClient.getPrice("BTC_USDT")));

        log.info("getPrices: {}", writer.writeValueAsString(poloniexApiClient.getPrices()));

        log.info("getMarkPrice: {}", writer.writeValueAsString(poloniexApiClient.getMarkPrice("BTC_USDT")));
        log.info("getMarketPriceComponents: {}", writer.writeValueAsString(poloniexApiClient.getMarketPriceComponents("BTC_USDT")));

        log.info("getMarkPrices: {}", writer.writeValueAsString(poloniexApiClient.getMarkPrices()));


        log.info("getOrderBook: {}", writer.writeValueAsString(poloniexApiClient.getOrderBook("BTC_USDT", "0.0001", null)));

        log.info("getCandles: {}", writer.writeValueAsString(poloniexApiClient.getCandles("BTC_USDT", "MINUTE_5", 5, 0L, System.currentTimeMillis())));
        log.info("getMarketTrades: {}", writer.writeValueAsString(poloniexApiClient.getMarketTrades("BTC_USDT", 2)));
        log.info("getTicker24h: {}", writer.writeValueAsString(poloniexApiClient.getTicker24h("BTC_USDT")));
        log.info("getTicker24hAll: {}", writer.writeValueAsString(poloniexApiClient.getTicker24hAll()));
        log.info("getMarkets: {}", writer.writeValueAsString(poloniexApiClient.getMarkets()));
        log.info("getMarket: {}", writer.writeValueAsString(poloniexApiClient.getMarket("BTC_USDT")));
//        log.info("getCurrencies: {}", writer.writeValueAsString(poloniexApiClient.getCurrencies()));
        log.info("getCurrencies: {}", writer.writeValueAsString(poloniexApiClient.getCurrencies(null)));
        log.info("getCurrencies: {}", writer.writeValueAsString(poloniexApiClient.getCurrencies(true)));
        log.info("getCurrency: {}", writer.writeValueAsString(poloniexApiClient.getCurrency("BTC", null)));
        log.info("getCurrenciesV2: {}", writer.writeValueAsString(poloniexApiClient.getCurrenciesV2()));
        log.info("getCurrencyV2: {}", writer.writeValueAsString(poloniexApiClient.getCurrencyV2("BTC")));
        log.info("getCollateralInfo: {}", writer.writeValueAsString(poloniexApiClient.getCollateralInfo()));
        log.info("getCollateralInfo: {}", writer.writeValueAsString(poloniexApiClient.getCollateralInfo("BTC")));
        log.info("getBorrowRatesInfo: {}", writer.writeValueAsString(poloniexApiClient.getBorrowRatesInfo()));
        log.info("完成公共api");

    }

    private static void testAllEndpoints(SpotPoloRestClient poloniexApiClient) throws JsonProcessingException {
        // public
        testPublicEndpoints(poloniexApiClient);

        //private
        // accounts
        List<Account> accounts = poloniexApiClient.getAccounts();
        log.info("getAccounts: {}", writer.writeValueAsString(accounts));
        log.info("getAccountBalancesByType: {}", writer.writeValueAsString(poloniexApiClient.getAccountBalancesByType("SPOT")));
        log.info("getAccountBalancesById: {}", writer.writeValueAsString(poloniexApiClient.getAccountBalancesById(Long.parseLong(accounts.get(0).getAccountId()))));
        log.info("accountsTransfer: {}", writer.writeValueAsString(poloniexApiClient.accountsTransfer(AccountsTransferRequest.builder().currency("USDT").amount("10.5").fromAccount("SPOT").toAccount("FUTURES").build())));
        log.info("getAccountsTransfers: {}", writer.writeValueAsString(poloniexApiClient.getAccountsTransfers(null, null, null, null)));
        log.info("getAccountsTransfers: {}", writer.writeValueAsString(poloniexApiClient.getAccountsTransfers(null, null, null, null, null, null)));
        log.info("getAccountsTransfers: {}", writer.writeValueAsString(poloniexApiClient.getAccountsTransferById(123L)));
        log.info("getFeeInfo: {}", writer.writeValueAsString(poloniexApiClient.getFeeInfo()));


        log.info("getAccountsActivity: {}", writer.writeValueAsString(poloniexApiClient.getAccountsActivity(System.currentTimeMillis() - 1000, System.currentTimeMillis(), 200, 100, 0L, "NEXT", "")));
        log.info("getAccountsInterestHistory: {}", writer.writeValueAsString(poloniexApiClient.getAccountsInterestHistory(null, null, null, null, null)));

        // subaccounts
        log.info("getSubaccounts: {}", writer.writeValueAsString(poloniexApiClient.getSubaccounts()));
        log.info("getSubaccountBalances: {}", writer.writeValueAsString(poloniexApiClient.getSubaccountBalances()));
        log.info("getSubaccountBalancesById: {}", writer.writeValueAsString(poloniexApiClient.getSubaccountBalancesById("abc123")));
//        log.info("transferForSubaccount: {}", writer.writeValueAsString(poloniexApiClient.transferForSubaccount(SubaccountTransferRequest.builder().currency("USD").amount("1").fromAccountId("1000-aaa").fromAccountType("SPOT").toAccountId("2000-bbb").toAccountType("SPOT").build())));
//        log.info("transferForSubaccount: {}", writer.writeValueAsString(poloniexApiClient.getSubaccountTransferRecords(null, null, null, null, null, null, null, null, null, null)));
//        log.info("transferForSubaccount: {}", writer.writeValueAsString(poloniexApiClient.getSubaccountTransferRecords(null, null, null, "USD", null, null, null, null, null, null)));
        log.info("getSubaccountTransferRecordsById: {}", writer.writeValueAsString(poloniexApiClient.getSubaccountTransferRecordsById(123456789L)));

        // wallets
        log.info("getDepositAddresses: {}", writer.writeValueAsString(poloniexApiClient.getDepositAddresses()));
        log.info("getDepositAddressesByCurrency: {}", writer.writeValueAsString(poloniexApiClient.getDepositAddressesByCurrency("USDT")));
        log.info("addNewCurrencyAddress: {}", writer.writeValueAsString(poloniexApiClient.addNewCurrencyAddress(NewCurrencyAddressRequest.builder().currency("USDT").build())));
        log.info("getWalletsActivities: {}", writer.writeValueAsString(poloniexApiClient.getWalletsActivities(System.currentTimeMillis() - 10000000L, System.currentTimeMillis(), null)));
//        log.info("withdrawCurrency: {}", writer.writeValueAsString(poloniexApiClient.withdrawCurrency(WithdrawCurrencyRequest.builder().currency("USDTTRON").amount("4").address("TQgw1uXU6GNprsU1bVcuceQu2B8axRVPL7").paymentID("123").allowBorrow(false).build())));

        // margin
        log.info("getAccountMargin: {}", writer.writeValueAsString(poloniexApiClient.getAccountMargin("SPOT")));
        log.info("getBorrowStatus: {}", writer.writeValueAsString(poloniexApiClient.getBorrowStatus(null)));
        log.info("getMaxSize: {}", writer.writeValueAsString(poloniexApiClient.getMaxSize("BTC_USDT")));

        // orders
        Order order1 = poloniexApiClient.placeOrder("BTC_USDT", "BUY", "GTC", "LIMIT", "SPOT", "20640", "1", "", "T_D_UP_" + System.currentTimeMillis());
        log.info("placeOrder: {}", writer.writeValueAsString(order1));
        Order order2 = poloniexApiClient.placeOrder("BTC_USDT", "BUY", "GTC", "LIMIT", "SPOT", "1121", "1", "", "T_D_UP_" + System.currentTimeMillis());
        Order order3 = poloniexApiClient.placeOrder("BTC_USDT", "BUY", "GTC", "LIMIT", "SPOT", "1121", "1", "", "T_D_UP_" + System.currentTimeMillis());
        Order order4 = poloniexApiClient.placeOrder("BTC_USDT", "BUY", "GTC", "LIMIT", "SPOT", "56", "1", "", "T_D_UP_" + System.currentTimeMillis());
        Order order5 = poloniexApiClient.placeOrder("BTC_USDT", "BUY", "GTC", "LIMIT", "SPOT", "0.01", "1000", "", "T_D_UP_" + System.currentTimeMillis());
        Order order6 = poloniexApiClient.placeOrder("BTC_USDT", "BUY", "GTC", "LIMIT", "SPOT", "56", "1", "", "T_D_UP_" + System.currentTimeMillis());
        log.info("placeOrders: {}", writer.writeValueAsString(poloniexApiClient.placeOrders(List.of(
                new OrderRequest("BTC_USDT", "BUY", "GTC", "LIMIT", "SPOT", "1000", "0.001", "", "T_D_UP_" + System.currentTimeMillis()),
                new OrderRequest("BTC_USDT", "BUY", "GTC", "LIMIT", "SPOT", "100", "0.01", "", "T_D_UP_" + System.currentTimeMillis())))));
        log.info("getOrderByOrderId: {}", writer.writeValueAsString(poloniexApiClient.getOrderByOrderId(order1.getId())));
        log.info("getOrderByClientOrderId: {}", writer.writeValueAsString(poloniexApiClient.getOrderByClientOrderId(order2.getClientOrderId())));
        log.info("getOrders: {}", writer.writeValueAsString(poloniexApiClient.getOrders(null, null, null, null, null)));
        log.info("getOrders: {}", writer.writeValueAsString(poloniexApiClient.getOrders("BTC_USDT", "BUY", null, "NEXT", 10)));
        log.info("cancelOrderByOrderId: {}", writer.writeValueAsString(poloniexApiClient.cancelOrderByOrderId(order1.getId())));
        log.info("cancelOrderByClientOrderId: {}", writer.writeValueAsString(poloniexApiClient.cancelOrderByClientOrderId(order2.getClientOrderId())));
        log.info("cancelOrderByIds: {}", writer.writeValueAsString(poloniexApiClient.cancelOrderByIds(new String[]{order3.getId()}, new String[]{order4.getClientOrderId()})));
        log.info("cancelAllOrders: {}", writer.writeValueAsString(poloniexApiClient.cancelAllOrders(new String[]{"BTC_USDT"}, new String[]{"SPOT"}))); // cancel order5
        log.info("cancelAllOrders: {}", writer.writeValueAsString(poloniexApiClient.cancelAllOrders(null, null))); // cancel order6

        Order order7 = poloniexApiClient.placeOrder("BTC_USDT", "BUY", "GTC", "LIMIT", "SPOT", "20640", "1", "", "T_D_UP_" + System.currentTimeMillis());
        log.info("placeOrder: {}", writer.writeValueAsString(order7));
        Order order8 = poloniexApiClient.cancelReplaceOrderById(order7.getId(), "T_D_UP_" + System.currentTimeMillis(), "21000", null, null, null, null, null, "false");
        log.info("cancelReplaceOrderById: {}", writer.writeValueAsString(order8));
        Order order9 = poloniexApiClient.cancelReplaceOrderByClientOrderId(order8.getClientOrderId(), null, "0.5", null, null, null, false, "false");
        log.info("cancelReplaceOrderById: {}", writer.writeValueAsString(order9));

        SmartOrder smartOrder1 = poloniexApiClient.placeSmartOrder("BTC_USDT", "BUY", "FOK", "STOP_LIMIT", "SPOT", "601.00", "13900", "100", "T_D_UP_" + System.currentTimeMillis(), "4000");
        log.info("placeSmartOrder: {}", writer.writeValueAsString(smartOrder1));
        SmartOrder smartOrder2 = poloniexApiClient.placeSmartOrder("BTC_USDT", "BUY", "FOK", "STOP_LIMIT", "SPOT", "601.00", "13900", "100", "T_D_UP_" + System.currentTimeMillis(), "4000");
        SmartOrder smartOrder3 = poloniexApiClient.placeSmartOrder("BTC_USDT", "BUY", "FOK", "STOP_LIMIT", "SPOT", "601.00", "13900", "100", "T_D_UP_" + System.currentTimeMillis(), "4000");
        SmartOrder smartOrder4 = poloniexApiClient.placeSmartOrder("BTC_USDT", "BUY", "FOK", "STOP_LIMIT", "SPOT", "601.00", "13900", "100", "T_D_UP_" + System.currentTimeMillis(), "4000");
        SmartOrder smartOrder5 = poloniexApiClient.placeSmartOrder("BTC_USDT", "BUY", "FOK", "STOP_LIMIT", "SPOT", "601.00", "13900", "100", "T_D_UP_" + System.currentTimeMillis(), "4000");
        SmartOrder smartOrder6 = poloniexApiClient.placeSmartOrder("BTC_USDT", "BUY", "FOK", "STOP_LIMIT", "SPOT", "601.00", "13900", "100", "T_D_UP_" + System.currentTimeMillis(), "4000");
        log.info("getSmartOrderByOrderId: {}", writer.writeValueAsString(poloniexApiClient.getSmartOrderByOrderId(String.valueOf(smartOrder1.getId()))));
        log.info("getSmartOrderByClientOrderId: {}", writer.writeValueAsString(poloniexApiClient.getSmartOrderByClientOrderId(smartOrder2.getClientOrderId())));
        log.info("getSmartOrders: {}", writer.writeValueAsString(poloniexApiClient.getSmartOrders(10)));
        log.info("cancelSmartOrderByClientOrderId: {}", writer.writeValueAsString(poloniexApiClient.cancelSmartOrderByOrderId(String.valueOf(smartOrder1.getId()))));
        log.info("cancelSmartOrderByClientOrderId: {}", writer.writeValueAsString(poloniexApiClient.cancelSmartOrderByClientOrderId(smartOrder2.getClientOrderId())));
        log.info("cancelSmartOrderByIds: {}", writer.writeValueAsString(poloniexApiClient.cancelSmartOrderByIds(new String[]{String.valueOf(smartOrder3.getId())}, new String[]{smartOrder4.getClientOrderId()})));
//        log.info("cancelAllSmartOrders: {}", writer.writeValueAsString(poloniexApiClient.cancelAllSmartOrders(new String[]{"BTC_USDT"}, new String[]{"SPOT"})));
//        log.info("cancelAllSmartOrders: {}", writer.writeValueAsString(poloniexApiClient.cancelAllSmartOrders(null, null)));
//        log.info("cancelAllSmartOrders: {}", writer.writeValueAsString(poloniexApiClient.cancelAllSmartOrders()));
        log.info("getOrderHistory: {}", writer.writeValueAsString(poloniexApiClient.getOrderHistory(null, null, null, null, null, null, null, null, null, null, null)));
        log.info("getOrderHistory: {}", writer.writeValueAsString(poloniexApiClient.getOrderHistory("SPOT", "LIMIT", "BUY", "BTC_USDT", 58067963198046208L, "PRE", "CANCELED", 100, false, null, null)));
        log.info("getSmartOrderHistory: {}", writer.writeValueAsString(poloniexApiClient.getSmartOrderHistory(null, null, null, null, null, null, null, null, null, null, null)));
        log.info("getSmartOrderHistory: {}", writer.writeValueAsString(poloniexApiClient.getSmartOrderHistory("SPOT", "STOP_LIMIT", "BUY", "BTC_USDT", null, null, "CANCELED", 1, false, null, null)));

        // trades
        log.info("getTrades: {}", writer.writeValueAsString(poloniexApiClient.getTrades(null, null, null, null, null)));
        log.info("getTrades: {}", writer.writeValueAsString(poloniexApiClient.getTrades(10, 1655016096000L, 1655929390000L, 1000L, "NEXT")));
        log.info("getTrades: {}", writer.writeValueAsString(poloniexApiClient.getTrades(null, null, null, null, null, null)));
        log.info("getTrades: {}", writer.writeValueAsString(poloniexApiClient.getTrades(10, 1655016096000L, 1655929390000L, 1000L, "NEXT", List.of("BTC_USDT"))));
        log.info("getUserTradesByOrderId: {}", writer.writeValueAsString(poloniexApiClient.getUserTradesByOrderId(62759197997072384L)));

        //kill switch
        log.info("setKillSwitch: {}", writer.writeValueAsString(poloniexApiClient.setKillSwitch("15")));
        log.info("getKillSwitch: {}", writer.writeValueAsString(poloniexApiClient.getKillSwitch()));
    }

    private static void websocketPublicTest() {
        OkHttpClient httpClient = new OkHttpClient.Builder().build();

        SpotPoloPublicWebsocketClient publicWebsocket = new SpotPoloPublicWebsocketClient(httpClient, PUBLIC_WS_URL);
        publicWebsocket.onCandlestickEvent(List.of(CandlestickChannels.CANDLES_MINUTE_1), List.of("BTC_USDT"), new PoloLoggingCallback<>());
        publicWebsocket.onTradeEvent(List.of("ETH_USDT"), new PoloLoggingCallback<>());
        publicWebsocket.onTickerEvent(List.of("ADA_USDT", "BTC_USDT"), new PoloLoggingCallback<>());
        publicWebsocket.onTickerAllEvent(new PoloLoggingCallback<>());
        publicWebsocket.onBookEvent(List.of("ETH_USDT"), 5, new PoloLoggingCallback<>());
        publicWebsocket.onBookLv2Event(List.of("BTC_USDT", "ETH_USDT"), new PoloLoggingCallback<>());

        publicWebsocket.onSymbolEvent(List.of("BTC_USDT"), new PoloLoggingCallback<>());

        publicWebsocket.onCurrenciesEvent(List.of("all"), new PoloLoggingCallback<>());
        publicWebsocket.onExchangeEvent(new PoloLoggingCallback<>());

        // example using inline callback
        final ObjectMapper objectMapper = new ObjectMapper();
        PoloApiCallback<TickerEvent> callback = new PoloApiCallback<>() {

            @Override
            public void onResponse(PoloEvent<TickerEvent> response) throws JsonProcessingException {

                log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
            }

            @Override
            public void onFailure(Throwable t) {
                log.warn("error", t);
            }
        };

        publicWebsocket.onTickerEvent(List.of("ADA_USDT"), callback);

        // example using unsubscribe and close

        WebSocket forTickerEvent = publicWebsocket.onTickerEvent(List.of("ADA_USDT", "BTC_USDT"), callback);
        try {
            Thread.sleep(5000L);
            // unsubscribe from BTC_USDT but keep ticker subscription for ADA_USDT
            publicWebsocket.unsubscribe(forTickerEvent, List.of(CHANNEL_TICKER), List.of("BTC_USDT"));
            Thread.sleep(5000L);
            // close ticker event websocket
            publicWebsocket.close(forTickerEvent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("结1束");

    }

    private static void websocketPrivateTest() {
        OkHttpClient httpClient = new OkHttpClient.Builder().build();

        SpotPoloPrivateWebsocketClient privateWebsocket = new SpotPoloPrivateWebsocketClient(httpClient, PRIVATE_WS_URL, API_KEY, SECRET);

        privateWebsocket.onOrderEvent(List.of("BTC_USDT"), new PoloLoggingCallback<>());

        // example of resubscribing after a failure
        final ObjectMapper objectMapper = new ObjectMapper();
        privateWebsocket.onOrderEvent(List.of("BTC_USDT"), new PoloApiCallback<OrderEvent>() {
            @Override
            public void onResponse(PoloEvent<OrderEvent> response) throws JsonProcessingException {
                log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
            }

            @Override
            public void onFailure(Throwable t) {
                try {
                    Thread.sleep(5000);
                    privateWebsocket.onOrderEvent(List.of("BTC_USDT"), this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        privateWebsocket.onBalanceEvent(new PoloLoggingCallback<>());
        privateWebsocket.onCreateOrderEvent("123456", CreateOrderRequest.builder().symbol("BTC_USDT").type("LIMIT").quantity("1100").side("BUY").price("40.50000").timeInForce("").clientOrderId("").build(), new PoloLoggingCallback<>());
        privateWebsocket.onCancelOrderEvent("123456", CancelOrderRequest.builder().orderIds(List.of("357171754193473536")).clientOrderIds(List.of()).build(), new PoloLoggingCallback<>());
        privateWebsocket.onCancelAllOrderEvent("123456", new PoloLoggingCallback<>());
        System.out.println("结束");
    }

}