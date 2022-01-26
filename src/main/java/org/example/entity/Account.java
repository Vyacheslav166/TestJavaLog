package org.example.entity;

import java.util.concurrent.atomic.AtomicInteger;

public class Account {

    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    private String ID;
    private Long money;

    public Account(Long money) {
        this.ID = Integer.toString(COUNTER.getAndIncrement());
        this.money = money;

    }

    public String getID() {
        return ID;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (!ID.equals(account.ID)) return false;
        return money.equals(account.money);
    }

    @Override
    public int hashCode() {
        int result = ID.hashCode();
        result = 31 * result + money.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "ID='" + ID + '\'' +
                ", money=" + money +
                '}';
    }
}
