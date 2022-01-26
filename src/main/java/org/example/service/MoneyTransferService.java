package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entity.Account;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Перевод денежных средств
 */

public class MoneyTransferService extends Thread {
    private final Logger logger = LogManager.getLogger(MoneyTransferService.class);
    //Счетчик транзакций
    public static volatile AtomicInteger countTransaction = new AtomicInteger(0);

    private final List<Account> accounts;
    private final Integer totalTransactions;

    public MoneyTransferService(List<Account> accounts, Integer totalTransactions) {
        this.accounts = accounts;
        this.totalTransactions = totalTransactions;
    }

    @Override
    public void run() {
        logger.info("Start thread {}", Thread.currentThread());
        while (!isInterrupted()) {
            try {
                transaction();
                logger.info("Sleep thread = {}", Thread.currentThread());
                Thread.sleep(Math.round((Math.random() * 1000) + 1000));
            } catch (InterruptedException e) {
                logger.error(e);
                return;
            }
        }
    }

    /**
     * Перевод денег с одного счета на другой.
     * Счет "in", на который переводим деньги, и счет "out", с которого снимаем деньги, выбираются случайным образом.
     * Если счет "out", выбранный случайным образом, совпадет со счетом "in", то счет "out" выбирается заново.
     * Сумма денежного перевода выбирается случаным образом, но не должна превышать баланс на счете "out"
     */
    public void transaction() {
        Account in = accounts.get((int) Math.round(Math.random() * (accounts.size() - 1)));
        Account out = accounts.get((int) Math.round(Math.random() * (accounts.size() - 1)));
        while (in.equals(out)) {
            out = accounts.get((int) Math.round(Math.random() * (accounts.size() - 1)));
        }
        logger.info("Ready transaction: Thread = {}, Account out = {}, Account in = {}"
                , Thread.currentThread()
                , out
                , in);

        synchronized (in) {
            synchronized (out) {
                if (countTransaction.incrementAndGet() > totalTransactions) {
                    logger.info("Stop thread = {}", Thread.currentThread());
                    Thread.currentThread().interrupt();
                    return;
                }
                logger.info("Start transaction = {}: Thread = {}, Account out = {}, Account in = {}"
                        , countTransaction
                        , Thread.currentThread()
                        , out
                        , in);

                Long transactionValue = Math.round(Math.random() * out.getMoney());
                out.setMoney(out.getMoney() - transactionValue);
                in.setMoney(in.getMoney() + transactionValue);

                logger.info("Finish transaction = {}: Thread = {}, Account out = {}, Account in = {}"
                        , countTransaction
                        , Thread.currentThread()
                        , out
                        , in);


            }
        }
    }
}
