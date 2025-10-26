package com.banque.change.remote;

import javax.ejb.Remote;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Remote
public interface ChangeRemote {
    String getDefaultCurrency();
    List<String> getCurrencies();
    BigDecimal getRate(String currency, Date atDate);
    BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency, Date atDate);
}
