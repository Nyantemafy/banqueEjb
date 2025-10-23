package com.banque.change.ejb;

import com.banque.change.remote.ChangeRemote;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless(name = "ChangeBean")
public class ChangeBean implements ChangeRemote {

    private static final String DEFAULT_CURRENCY = "MGA";
    private static final String RATES_RESOURCE = "/rates.txt";
    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
    private static final Logger LOGGER = Logger.getLogger(ChangeBean.class.getName());

    private static class Rate {
        final String currency;
        final Date start;
        final Date end;
        final BigDecimal rateMGAperUnit; // MGA per 1 unit of currency
        Rate(String currency, Date start, Date end, BigDecimal rateMGAperUnit){
            this.currency = currency; this.start = start; this.end = end; this.rateMGAperUnit = rateMGAperUnit;
        }
        boolean activeOn(Date d){
            if (d == null) return true;
            boolean afterStart = (start == null) || !d.before(start);
            boolean beforeEnd = (end == null) || !d.after(end);
            return afterStart && beforeEnd;
        }
    }

    private final Map<String, List<Rate>> ratesByCurrency = new HashMap<>();

    @PostConstruct
    public void init(){
        loadRates();
    }

    private void loadRates(){
        ratesByCurrency.clear();
        InputStream is = getClass().getResourceAsStream(RATES_RESOURCE);
        if (is == null) {
            is = getClass().getResourceAsStream("rates.txt");
        }
        if (is == null) {
            LOGGER.log(Level.WARNING, "rates.txt not found on classpath at '/rates.txt' nor 'rates.txt'. Only default currency will be available.");
            ratesByCurrency.put(DEFAULT_CURRENCY, Collections.singletonList(new Rate(DEFAULT_CURRENCY, null, null, BigDecimal.ONE)));
            return;
        }
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))){
                String line;
                while((line = br.readLine()) != null){
                    line = line.trim();
                    if(line.isEmpty() || line.startsWith("#")) continue;
                    // CSV-like: nom_devise;date_debut;date_fin;cours
                    String[] parts = line.split("[;,]\\s*");
                    if(parts.length < 4) continue;
                    String cur = parts[0].trim().toUpperCase();
                    Date start = parseDate(parts[1].trim());
                    Date end = parseDate(parts[2].trim());
                    BigDecimal rate = new BigDecimal(parts[3].trim());
                    ratesByCurrency.computeIfAbsent(cur, k -> new ArrayList<>()).add(new Rate(cur, start, end, rate));
                }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error while reading rates.txt: {0}", e.getMessage());
        }
        // Always ensure MGA exists
        ratesByCurrency.putIfAbsent(DEFAULT_CURRENCY, Collections.singletonList(new Rate(DEFAULT_CURRENCY, null, null, BigDecimal.ONE)));
        // sort periods for each currency
        for(List<Rate> list : ratesByCurrency.values()){
            list.sort(Comparator.comparing(r -> r.start, Comparator.nullsFirst(Comparator.naturalOrder())));
        }
        try {
            Set<String> keys = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            keys.addAll(ratesByCurrency.keySet());
            LOGGER.info("Loaded currencies: " + String.join(", ", keys));
        } catch (Exception ignore) {}
    }

    private static Date parseDate(String s){
        if(s == null || s.isEmpty() || s.equalsIgnoreCase("null")) return null;
        try { return DF.parse(s); } catch (ParseException e) { return null; }
    }

    @Override
    public String getDefaultCurrency() { return DEFAULT_CURRENCY; }

    @Override
    public List<String> getCurrencies() {
        Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        set.addAll(ratesByCurrency.keySet());
        return new ArrayList<>(set);
    }

    @Override
    public BigDecimal getRate(String currency, Date atDate) {
        if(currency == null || currency.trim().isEmpty()) return BigDecimal.ONE;
        String cur = currency.toUpperCase();
        if(DEFAULT_CURRENCY.equals(cur)) return BigDecimal.ONE;
        List<Rate> list = ratesByCurrency.get(cur);
        if(list == null || list.isEmpty()) return null;
        Date d = atDate;
        for(Rate r : list){
            if(r.activeOn(d)) return r.rateMGAperUnit;
        }
        // Fallback: last known
        return list.get(list.size()-1).rateMGAperUnit;
    }

    @Override
    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency, Date atDate) {
        if(amount == null) return null;
        String from = (fromCurrency==null?DEFAULT_CURRENCY:fromCurrency.toUpperCase());
        String to = (toCurrency==null?DEFAULT_CURRENCY:toCurrency.toUpperCase());
        if(from.equals(to)) return amount;
        MathContext mc = new MathContext(20, RoundingMode.HALF_UP);
        if(DEFAULT_CURRENCY.equals(from)){
            // MGA -> target
            BigDecimal rate = getRate(to, atDate);
            if(rate==null || BigDecimal.ZERO.compareTo(rate)==0) return null;
            return amount.divide(rate, 6, RoundingMode.HALF_UP);
        } else if (DEFAULT_CURRENCY.equals(to)){
            // from -> MGA
            BigDecimal rate = getRate(from, atDate);
            if(rate==null) return null;
            return amount.multiply(rate, mc);
        } else {
            // from -> MGA -> to
            BigDecimal inMGA = convert(amount, from, DEFAULT_CURRENCY, atDate);
            if(inMGA==null) return null;
            return convert(inMGA, DEFAULT_CURRENCY, to, atDate);
        }
    }
}
