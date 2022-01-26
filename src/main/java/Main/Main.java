package Main;

import Entity.Account;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final int COUNT_ENTITY = 4;
    private static final int COUNT_THREAD = 2;
    private static final int COUNT_TRANSACTION = 30;
    public static List<Account> accounts = new ArrayList<>();
    public static List<Thread> threads = new ArrayList<>();

    private static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        logger.debug("This is a debug message");
        logger.info("This is an info message");
        logger.warn("This is a warn message");
        logger.error("This is an error message");
        logger.fatal("This is a fatal message");

        for (int i = 0; i < COUNT_ENTITY; i++) {
            accounts.add(new Account(10000L));
        }
        for (int i = 0; i < COUNT_THREAD; i++) {
            MoneyTransfer moneyTransfer = new MoneyTransfer();
            threads.add(moneyTransfer);
            moneyTransfer.start();
        }

    }

    public static class MoneyTransfer extends Thread {

        public static volatile AtomicInteger countTransaction = new AtomicInteger(0);

        @Override
        public void run() {
            while (!isInterrupted()) {
                MoneyTransfer.transaction();
                try {
                    Thread.sleep(Math.round((Math.random() * 1000) + 1000));
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        public static void transaction() {
            Account in = accounts.get((int) Math.round(Math.random() * (COUNT_ENTITY - 1)));
            Account out = accounts.get((int) Math.round(Math.random() * (COUNT_ENTITY - 1)));
            while (in.equals(out)) {
                out = accounts.get((int) Math.round(Math.random() * (COUNT_ENTITY - 1)));
            }
            synchronized (in) {
                synchronized (out) {
                    Long transactionValue = Math.round(Math.random() * out.getMoney());
                    out.setMoney(out.getMoney() - transactionValue);
                    in.setMoney(in.getMoney() + transactionValue);
                    if (countTransaction.incrementAndGet() >= COUNT_TRANSACTION) {
                        for (Thread thread : threads) {
                            thread.interrupt();
                        }
                    }
                }
            }
        }
    }
}
