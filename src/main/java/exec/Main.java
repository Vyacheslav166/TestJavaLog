package exec;

import entity.Account;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class Main {

    //Количество счетов
    private static final int COUNT_ENTITY = 4;
    //Количество потоков
    private static final int COUNT_THREAD = 2;
    //Количество транзакций
    private static final int COUNT_TRANSACTION = 30;
    //Список счетов
    public static List<Account> accounts = new ArrayList<>();
    //Список потоков
    public static List<Thread> threads = new ArrayList<>();

    private static final Logger logger = LogManager.getLogger(exec.Main.class);

    public static void main(String[] args) {

        //Создаем список счетов
        logger.info("Start main metod");
        for (int i = 0; i < COUNT_ENTITY; i++) {
            accounts.add(new Account(10000L));
        }
        //Создаем список потоков и запускаем их
        logger.info("Made accounts");
        for (int i = 0; i < COUNT_THREAD; i++) {
            MoneyTransfer moneyTransfer = new MoneyTransfer();
            logger.info("Made thread = {}", moneyTransfer);
            threads.add(moneyTransfer);
            moneyTransfer.start();
        }
        logger.info("Finish main metod");

    }

    /**
     * Перевод денежных средств
     */
    public static class MoneyTransfer extends Thread {

        //Счетчик транзакций
        public static volatile AtomicInteger countTransaction = new AtomicInteger(0);

        @Override
        public void run() {
            while (!isInterrupted()) {
                MoneyTransfer.transaction();
                try {
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
         * Сумма денежного перевода выбирается случаным образом, но не должна превышать баланс на счете "out"
         */
        public static void transaction() {
            Account in = accounts.get((int) Math.round(Math.random() * (COUNT_ENTITY - 1)));
            Account out = accounts.get((int) Math.round(Math.random() * (COUNT_ENTITY - 1)));
            while (in.equals(out)) {
                out = accounts.get((int) Math.round(Math.random() * (COUNT_ENTITY - 1)));
            }
            logger.info("Ready transaction: Thread = {}, Account out = {}, Account in = {}",
                    Thread.currentThread(), out, in);

            synchronized (in) {
                synchronized (out) {
                    logger.info("Start transaction = {}: Thread = {}, Account out = {}, Account in = {}",
                            countTransaction, Thread.currentThread(), out, in);

                    Long transactionValue = Math.round(Math.random() * out.getMoney());
                    out.setMoney(out.getMoney() - transactionValue);
                    in.setMoney(in.getMoney() + transactionValue);

                    logger.info("Finish transaction = {}: Thread = {}, Account out = {}, Account in = {}",
                            countTransaction, Thread.currentThread(), out, in);

                    if (countTransaction.incrementAndGet() >= COUNT_TRANSACTION) {
                        for (Thread thread : threads) {
                            thread.interrupt();
                            logger.info("Stop transactions");
                        }
                    }
                }
            }
        }
    }
}
