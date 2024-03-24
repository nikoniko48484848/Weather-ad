/**
 *
 *  @author Rączka Nikodem S27866
 *
 */

package zad1;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

public class Service {

    public String country;
    public String city;

    public Service(String country) {
        this.country = country;
    }

    public String getWeather(String miasto) {
        this.city = miasto;
        return getJsonContent("https://api.openweathermap.org/data/2.5/weather?q="+miasto+"&appid=44e30b600722303a6f5c76f9b26cd75f&units=metric");
    }

    public Double getRateFor(String kodWaluty) {
        // 6e68ae66a4a9b48121f01d8c
        // country/argument
        String url = "https://v6.exchangerate-api.com/v6/6e68ae66a4a9b48121f01d8c/latest/"+kodWaluty;

        Rate rate = new Gson().fromJson(getJsonContent(url), Rate.class);
        Locale locale = new Locale.Builder().setRegion(getCountryCode(country)).build();
        Currency currency = Currency.getInstance(locale);
        System.out.println(rate.conversion_rates.get(currency.getCurrencyCode()));

        Double resRate = 1/Double.parseDouble(rate.conversion_rates.get(currency.getCurrencyCode()));

        return resRate;
    }

    public Double getNBPRate() {
        // PLN/z api
        String countryCode = getCountryCode(country);
        Locale locale = new Locale.Builder().setRegion(countryCode).build();
        Currency currency = Currency.getInstance(locale);
        String[] tableNames = {"A", "B"};
        if (!countryCode.equals("PL")) {
            System.out.println(countryCode);
            PLNRate plnRate = new PLNRate();
            for (String tableName : tableNames) {
                String url = "http://api.nbp.pl/api/exchangerates/rates/"+tableName+"/" + currency.getCurrencyCode() + "/?format=json";
                plnRate = new Gson().fromJson(getJsonContent(url), PLNRate.class);
                if (plnRate != null)
                    break;
            }

            System.out.println(plnRate.rates.get(0).mid);
            double resRate = 1/plnRate.rates.get(0).mid;
            return resRate;
        }
        return 1.;
    }

    public String getJsonContent(String url) {
        String jsonContent = "";
        // 44e30b600722303a6f5c76f9b26cd75f
        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(new URI(url).toURL().openConnection().getInputStream()))
        ) {
            jsonContent = br.lines().collect(Collectors.joining());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return jsonContent;
    }

    public static String getCountryCode(String country) {
        for (String countryCode : Locale.getISOCountries()) {
            Locale locale = new Locale.Builder().setRegion(countryCode).build();
            if (country.equalsIgnoreCase(locale.getDisplayCountry())) {
                return countryCode;
            }
        }
        return "";
    }
}

class Rate {
    Map<String, String> conversion_rates = new HashMap<>();
}

class PLNRate {
    List<Rate1> rates;
}

class Rate1 {
    double mid;
}
