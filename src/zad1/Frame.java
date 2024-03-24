package zad1;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Frame extends JFrame {
    public JPanel topPanel;
    public Service service;
    public String weatherJsonData;
    public Double rate1, rate2;
    public Frame(Service service, String weatherJsonData, Double rate1, Double rate2) {
        this.service = service;
        this.weatherJsonData = weatherJsonData;
        this.rate1 = rate1;
        this.rate2 = rate2;

        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.setTitle("Weather and Exchange Rates");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        topPanel = new JPanel(new GridLayout(1, 3, 5, 15));
        JPanel wikiPanel = getWikiPanel();

        this.setMainPanel();

        this.add(topPanel);
        this.add(wikiPanel);
        this.setVisible(true);
    }

    private void setMainPanel() {
        JPanel weatherPanel = getWeatherPanel();
        JPanel currencyPanel = getCurrencyPanel();
        JPanel plnPanel = getPlnPanel();

        topPanel.add(weatherPanel);
        topPanel.add(currencyPanel);
        topPanel.add(plnPanel);
    }

    private JPanel getPlnPanel() {
        JPanel resPanel = new JPanel();
        JLabel label = new JLabel("PLN rate: " + rate2);
        resPanel.add(label);
        return resPanel;
    }

    private JPanel getCurrencyPanel() {
        JPanel resPanel = new JPanel();
        JLabel label = new JLabel("Rate: " + rate1);
        resPanel.add(label);
        return resPanel;
    }

    private JPanel getWikiPanel() {
        JFXPanel webPanel = new JFXPanel();
        JPanel resPanel = new JPanel(new BorderLayout());

        Platform.runLater(() -> {
            WebView browser = new WebView();
            WebEngine webEngine = browser.getEngine();
            String url = "https://en.wikipedia.org/wiki/" + service.city;
            webEngine.load(url);

            webPanel.setScene(new Scene(browser));
        });

        resPanel.add(webPanel, BorderLayout.CENTER);
        return resPanel;
    }

    private JPanel getWeatherPanel() {
        JPanel resPanel = new JPanel(new BorderLayout());
        String jsonData = this.weatherJsonData;
        JSONWeather jsonWeather = new Gson().fromJson(jsonData, JSONWeather.class);
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("Sky: " + jsonWeather.weather.get(0).main);
        listModel.addElement("Temperature: " + jsonWeather.main.get("temp") + "C");
        listModel.addElement("Feels like: " + jsonWeather.main.get("feels_like") + "C");
        listModel.addElement("Pressure: " + jsonWeather.main.get("pressure"));
        listModel.addElement("Wind speed: " + jsonWeather.wind.get("speed") + "km/h");

        JList<String> weatherList = new JList<>(listModel);
        resPanel.add(weatherList);

        return resPanel;
    }
}

class JSONWeather {
    List<WeatherMain> weather;
    Map<String, Double> main = new HashMap<>();
    Map<String, Double> wind = new HashMap<>();
}

class WeatherMain {
    String main;
}