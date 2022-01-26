package org.example;

import org.example.entity.Account;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.service.MoneyTransferService;

import java.util.ArrayList;
import java.util.List;


public class Main {

    //Количество счетов
    private static final int COUNT_ENTITY = 4;
    //Количество потоков
    private static final int COUNT_THREAD = 2;
    //Количество транзакций
    private static final int COUNT_TRANSACTION = 30;
    //Список счетов
    public static List<Account> accounts = new ArrayList<>();

    private static final Logger logger = LogManager.getLogger(Main.class);


    public static void main(String[] args) {

        //Создаем список счетов
        logger.info("Start main metod");
        for (int i = 0; i < COUNT_ENTITY; i++) {
            accounts.add(new Account(10000L));
        }
        //Создаем список потоков и запускаем их
        logger.info("Made accounts");
        for (int i = 0; i < COUNT_THREAD; i++) {
            MoneyTransferService moneyTransferService = new MoneyTransferService(accounts, COUNT_TRANSACTION);
            moneyTransferService.start();
        }
        logger.info("Finish main metod");
    }
}
